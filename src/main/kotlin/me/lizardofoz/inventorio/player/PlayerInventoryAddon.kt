package me.lizardofoz.inventorio.player

import me.lizardofoz.inventorio.client.ui.PlayerInventoryUIAddon
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.mixin.accessor.SimpleInventoryAccessor
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FireworkRocketEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.FireworkItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.util.Hand
import kotlin.math.max
import kotlin.math.sign

/**
 * This class is responsible for the inventory addon itself,
 * while [PlayerInventoryUIAddon] is responsible for the visuals of the Player Screen UI
 * and [PlayerScreenHandlerAddon] is responsible for the slots and player interacting with the slots
 */
class PlayerInventoryAddon internal constructor(val player: PlayerEntity) : SimpleInventory(DEEP_POCKETS_MAX_SIZE + TOOL_BELT_SIZE + UTILITY_BELT_SIZE)
{
    @Suppress("CAST_NEVER_SUCCEEDS")
    private val accessor = this as SimpleInventoryAccessor

    val stacks: MutableList<ItemStack>
    val deepPockets: MutableList<ItemStack>
    val toolBelt: MutableList<ItemStack>
    val utilityBelt: MutableList<ItemStack>

    var selectedUtility = 0
    var mainHandDisplayTool = ItemStack.EMPTY!!

    init
    {
        stacks = accessor.stacks!!
        deepPockets = stacks.subList(INVENTORY_ADDON_DEEP_POCKETS_RANGE.first, INVENTORY_ADDON_DEEP_POCKETS_RANGE.last + 1)
        toolBelt = stacks.subList(INVENTORY_ADDON_TOOL_BELT_RANGE.first, INVENTORY_ADDON_TOOL_BELT_RANGE.last + 1)
        utilityBelt = stacks.subList(INVENTORY_ADDON_UTILITY_BELT_RANGE.first, INVENTORY_ADDON_UTILITY_BELT_RANGE.last + 1)
    }

    //==============================
    //Injects. These functions are either injected or redirected to by a mixin of a [PlayerInventory] class
    //==============================

    /**
     * Returns null if we want to proceed with vanilla behaviour.
     * Responsible for a functioning main hand, rather than what's in the selected hotbar slot
     */
    fun getMainHandStack(): ItemStack?
    {
        return if (player.handSwinging && mainHandDisplayTool.isNotEmpty)
            mainHandDisplayTool
        else
            null
    }

    /**
     * Responsible for a functioning offhand, rather than what's in the deprecated offhand slot
     */
    fun getOffHandStack(): ItemStack
    {
        return getSelectedUtilityStack()
    }

    /**
     * This method looks for the first empty slot in the Deep Pockets and the Utility Belt
     */
    fun getFirstEmptyAddonSlot(): Int
    {
        for (index in getAvailableDeepPocketsRange())
        {
            if (deepPockets[index].isEmpty)
                return index
        }
        for ((index, utilityBeltStack) in utilityBelt.withIndex())
        {
            if (utilityBeltStack.isEmpty)
                return index
        }
        return -1
    }

    /**
     * This method looks for the first slot in the Deep Pockets and the Utility Belt that might take the input item stack
     */
    fun getFirstOccupiedAddonSlotWithRoomForStack(inputStack: ItemStack): Int
    {
        for (index in getAvailableDeepPocketsRange())
        {
            if (canStackAddMore(deepPockets[index], inputStack))
                return index
        }
        for ((index, utilityBeltStack) in utilityBelt.withIndex())
        {
            if (canStackAddMore(utilityBeltStack, inputStack))
                return index
        }
        return -1
    }

    /**
     * Returns the block breaking speed based on the return of [getMostPrefferedTool]
     * Note: the calling injector will discard the result if another mod sets a bigger value than the return
     */
    fun getMiningSpeedMultiplier(block: BlockState): Float
    {
        val tool = getMostPrefferedTool(block)
        mainHandDisplayTool = tool
        return max(1f, tool.getMiningSpeedMultiplier(block))
    }

    fun prePlayerAttack()
    {
        //If a player tries to attack with a tool, respect that tool and don't change anything
        if (player.inventory.getStack(player.inventory.selectedSlot).item is ToolItem)
            return

        //Or else set a sword/trident as a weapon of choice, or an axe if a sword slot is empty
        player.handSwinging = true
        mainHandDisplayTool = if (!toolBelt[SLOT_INDEX_SWORD].isEmpty)
            toolBelt[SLOT_INDEX_SWORD]
        else
            toolBelt[SLOT_INDEX_AXE]
        //For some reason we need to manually add weapon's attack modifiers - the game doesn't do that for us
        player.attributes.addTemporaryModifiers(mainHandDisplayTool.getAttributeModifiers(EquipmentSlot.MAINHAND))
    }

    fun postPlayerAttack()
    {
        if (player.inventory.getStack(player.inventory.selectedSlot).item is ToolItem)
            return
        player.attributes.removeModifiers(mainHandDisplayTool.getAttributeModifiers(EquipmentSlot.MAINHAND))
    }

    fun mendToolBeltItems(experience: Int): Int
    {
        var amount = experience
        for (itemStack in toolBelt)
            if (itemStack.isNotEmpty && itemStack.isDamaged)
            {
                val delta = Math.min(amount * 2, itemStack.damage)
                amount -= delta
                itemStack.damage -= delta
                return amount
            }
        return amount
    }

    //==============================
    //Additional functionality
    //==============================

    @Environment(EnvType.CLIENT)
    fun activateSelectedUtility()
    {
        Client.triesToUseUtility = true
        (MinecraftClient.getInstance() as MinecraftClientAccessor).invokeDoItemUse()
        Client.triesToUseUtility = false
    }

    @Environment(EnvType.CLIENT)
    fun switchToNextUtility(direction: Int): Boolean
    {
        val (nextSlot, slotIndex) = findNextUtility(direction)
        if (nextSlot.isEmpty)
            return false
        selectedUtility = slotIndex
        InventorioNetworking.c2sSendSelectedUtilitySlot(slotIndex)
        return true
    }

    fun findNextUtility(direction: Int): Pair<ItemStack, Int>
    {
        val range = if (direction.sign >= 0)
            (selectedUtility + 1 until UTILITY_BELT_SIZE) + (0 until selectedUtility)
        else
            (selectedUtility - 1 downTo 0) + (UTILITY_BELT_SIZE - 1 downTo selectedUtility + 1)

        for (i in range)
            if (utilityBelt[i].isNotEmpty)
                return Pair(utilityBelt[i], i)

        return Pair(ItemStack.EMPTY, -1)
    }

    fun fireRocketFromInventory()
    {
        if (!player.isFallFlying)
            return
        for (itemStack in player.inventory.main)
            tryFireRocket(itemStack)
        for (itemStack in deepPockets)
            tryFireRocket(itemStack)
        for (itemStack in utilityBelt)
            tryFireRocket(itemStack)
    }

    private fun tryFireRocket(itemStack: ItemStack)
    {
        if (itemStack.item is FireworkItem && itemStack.getSubTag("Fireworks")?.getList("Explosions", NbtType.COMPOUND)?.isEmpty() != false)
        {
            //If this is the client side, request a server to actually fire a rocket.
            if (player.world.isClient)
                InventorioNetworking.c2sUseBoostRocket()
            else //If this is a server, spawn a firework entity
                player.world.spawnEntity(FireworkRocketEntity(player.world, itemStack, player))
            player.swingHand(Hand.MAIN_HAND)
            itemStack.decrement(1)
            mainHandDisplayTool = itemStack
            return
        }
    }

    //==============================
    //Utility methods
    //==============================

    fun getMostPrefferedTool(block: BlockState): ItemStack
    {
        //If a player tries to attack with a tool, respect that tool and don't change anything
        val selectedItem = player.inventory.getStack(player.inventory.selectedSlot)
        if (selectedItem.item is ToolItem)
            return selectedItem
        //Try to find the fastest tool on the tool belt to mine this block
        val result = toolBelt.maxByOrNull { it.getMiningSpeedMultiplier(block) } ?: ItemStack.EMPTY
        return if (result.getMiningSpeedMultiplier(block) > 1.0f)
            result
        else if (block.material == Material.GLASS && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, toolBelt[SLOT_INDEX_PICKAXE]) > 0)
            toolBelt[SLOT_INDEX_PICKAXE] //Here we apply a silk touch pickaxe (if present) as a tool to mine glass-alike blocks
        else
            ItemStack.EMPTY
    }

    /**
     * Returns 3 utility belt items to display on the HUD
     */
    fun getDisplayedUtilities(): Array<ItemStack>
    {
        return arrayOf(findNextUtility(-1).first, getSelectedUtilityStack(), findNextUtility(1).first)
    }

    fun getSelectedUtilityStack(): ItemStack
    {
        return utilityBelt[selectedUtility]
    }

    //Note: this class returns the range within the INVENTORY, which is different from the range within the Screen Handler
    private fun getAvailableDeepPocketsRange(): IntRange
    {
        return INVENTORY_ADDON_DEEP_POCKETS_RANGE.first until
                INVENTORY_ADDON_DEEP_POCKETS_RANGE.first + getDeepPocketsRowCount() * VANILLA_ROW_LENGTH
    }

    //Note: this class returns the range within the INVENTORY, which is different from the range within the Screen Handler
    private fun getUnavailableDeepPocketsRange(): IntRange
    {
        return getAvailableDeepPocketsRange().last + 1..INVENTORY_ADDON_DEEP_POCKETS_RANGE.last
    }

    fun getDeepPocketsRowCount(): Int
    {
        return EnchantmentHelper.getEquipmentLevel(DeepPocketsEnchantment, player)
    }

    private fun canStackAddMore(existingStack: ItemStack, stack: ItemStack): Boolean
    {
        return !existingStack.isEmpty && areItemsSimilar(existingStack, stack)
                && existingStack.isStackable
                && existingStack.count < existingStack.maxCount
                && existingStack.count < 64
    }

    private fun areItemsSimilar(stack1: ItemStack, stack2: ItemStack): Boolean
    {
        return stack1.isNotEmpty && stack1.item === stack2.item && ItemStack.areTagsEqual(stack1, stack2)
    }

    @Environment(EnvType.CLIENT)
    object Client
    {
        val local get() = MinecraftClient.getInstance().player!!.inventoryAddon
        var selectedHotbarSection = -1
        @JvmField var triesToUseUtility = false
    }

    companion object
    {
        const val SLOT_INDEX_PICKAXE = 0
        const val SLOT_INDEX_SWORD = 1
        const val SLOT_INDEX_AXE = 2
        const val SLOT_INDEX_SHOVEL = 3
        const val SLOT_INDEX_HOE = 4

        @JvmStatic
        val PlayerEntity.inventoryAddon: PlayerInventoryAddon
            get() = (this.inventory as InventoryDuck).addon
    }
}