package me.lizardofoz.inventorio.player.inventory

import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.packet.InventorioNetworking
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FireworkRocketEntity
import net.minecraft.item.FireworkItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import kotlin.math.max

abstract class PlayerInventoryExtraStuff protected constructor(player: PlayerEntity) : PlayerInventoryHandFeatures(player)
{
    /**
     * Returns the block breaking speed based on the return of [getMostPreferredTool]
     */
    fun getMiningSpeedMultiplier(block: BlockState): Float
    {
        val tool = getMostPreferredTool(block)
        if (tool != getActualMainHandItem())
            displayTool = tool
        return max(1f, tool.getMiningSpeedMultiplier(block))
    }

    fun getMostPreferredTool(block: BlockState): ItemStack
    {
        //todo some mods will disagree with justifying the most preferred tool by just speed
        //If a player tries to dig with a selected tool, respect that tool and don't change anything
        if (getActualMainHandItem().item is ToolItem)
            return getActualMainHandItem()
        //Try to find the fastest tool on the tool belt to mine this block
        val result = toolBelt.maxByOrNull { it.getMiningSpeedMultiplier(block) } ?: ItemStack.EMPTY
        return if (result.getMiningSpeedMultiplier(block) > 1.0f)
            result
        else if (block.material == Material.GLASS && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, toolBelt[SLOT_INDEX_PICKAXE]) > 0)
            toolBelt[SLOT_INDEX_PICKAXE] //Here we apply a silk touch pickaxe (if present) as a tool to mine glass-alike blocks
        else
            ItemStack.EMPTY
    }

    fun prePlayerAttack()
    {
        //If a player tries to attack with a tool, respect that tool and don't change anything
        if (getActualMainHandItem().item is ToolItem)
            return
        //Or else set a sword/trident as a weapon of choice, or an axe if a sword slot is empty
        player.handSwinging = true
        displayTool = if (!toolBelt[SLOT_INDEX_SWORD].isEmpty)
            toolBelt[SLOT_INDEX_SWORD]
        else
            toolBelt[SLOT_INDEX_AXE]
        //For some reason we need to manually add weapon's attack modifiers - the game doesn't do that for us
        player.attributes.addTemporaryModifiers(displayTool.getAttributeModifiers(EquipmentSlot.MAINHAND))
    }

    fun postPlayerAttack()
    {
        if (getActualMainHandItem().item is ToolItem)
            return
        player.attributes.removeModifiers(displayTool.getAttributeModifiers(EquipmentSlot.MAINHAND))
    }

    fun fireRocketFromInventory()
    {
        if (!player.isFallFlying)
            return
        for (itemStack in player.inventory.main)
            if (tryFireRocket(itemStack))
                return
        for (itemStack in stacks)
            if (tryFireRocket(itemStack))
                return
    }

    private fun tryFireRocket(itemStack: ItemStack): Boolean
    {
        if (itemStack.item is FireworkItem && itemStack.getSubTag("Fireworks")?.getList("Explosions", 10)?.isEmpty() != false)
        {
            val copyStack = itemStack.copy()
            if (!player.abilities.creativeMode)
                itemStack.decrement(1)
            //If this is the client side, request a server to actually fire a rocket.
            if (player.world.isClient)
            {
                displayTool = itemStack
                InventorioNetworking.INSTANCE.c2sUseBoostRocket()
                (MinecraftClient.getInstance() as MinecraftClientAccessor).itemUseCooldown = 4
            }
            else //If this is a server, spawn a firework entity
                player.world.spawnEntity(FireworkRocketEntity(player.world, copyStack, player))
            return true
        }
        return false
    }

    companion object
    {
        const val SLOT_INDEX_PICKAXE = 0
        const val SLOT_INDEX_SWORD = 1
        const val SLOT_INDEX_AXE = 2
        const val SLOT_INDEX_SHOVEL = 3
        const val SLOT_INDEX_HOE = 4
    }
}