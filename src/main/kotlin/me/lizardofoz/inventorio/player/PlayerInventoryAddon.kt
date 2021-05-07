package me.lizardofoz.inventorio.player

import me.lizardofoz.inventorio.client.ui.PlayerInventoryUIAddon
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.mixin.accessor.SimpleInventoryAccessor
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
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
 * and [PlayerScreenHandlerAddon] is responsible for the slots and player interaction with them
 */
class PlayerInventoryAddon internal constructor(val player: PlayerEntity) : SimpleInventory(DEEP_POCKETS_MAX_SIZE + TOOL_BELT_SIZE + UTILITY_BELT_SIZE)
{
    @Suppress("CAST_NEVER_SUCCEEDS")
    private val accessor = this as SimpleInventoryAccessor

    val stacks : MutableList<ItemStack>
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

    fun getMainHandStack(): ItemStack?
    {
        return if (player.handSwinging && mainHandDisplayTool.isNotEmpty)
            mainHandDisplayTool
        else
            null
    }

    fun getOffHandStack(): ItemStack
    {
        return getSelectedUtilityStack()
    }

    fun getEmptyExtensionSlot(): Int
    {
        for (i in getAvailableDeepPocketsRange())
        {
            if (deepPockets[i].isEmpty)
                return i
        }
        return -1
    }

    fun getOccupiedExtensionSlotWithRoomForStack(stack: ItemStack): Int
    {
        for (i in getAvailableDeepPocketsRange())
        {
            if (canStackAddMore(deepPockets[i], stack))
                return i
        }
        return -1
    }

    fun getBlockBreakingSpeed(block: BlockState): Float
    {
        val tool = getMostEffectiveTool(block)
        mainHandDisplayTool = tool
        return max(1f, tool.getMiningSpeedMultiplier(block))
    }

    fun prePlayerAttack()
    {
        player.handSwinging = true
        mainHandDisplayTool = if (!toolBelt[SLOT_INDEX_SWORD].isEmpty)
            toolBelt[SLOT_INDEX_SWORD]
        else
            toolBelt[SLOT_INDEX_AXE]
        player.attributes.addTemporaryModifiers(mainHandDisplayTool.getAttributeModifiers(EquipmentSlot.MAINHAND))
    }

    fun postPlayerAttack()
    {
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
    fun switchToNextUtility(direction: Int): Boolean
    {
        val (nextSlot, slotIndex) = findNextUtility(direction)
        if (nextSlot.isEmpty)
            return false
        selectedUtility = slotIndex
        InventorioNetworking.C2SSendSelectedUtilitySlot(slotIndex)
        return true
    }

    @Environment(EnvType.CLIENT)
    fun activateSelectedUtility()
    {
        Client.triesToUseUtility = true
        (MinecraftClient.getInstance() as MinecraftClientAccessor).invokeDoItemUse()
        Client.triesToUseUtility = false
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
        //todo exclude explosive rockets
        if (itemStack.item is FireworkItem)
        {
            if (player.world.isClient)
                InventorioNetworking.C2SUseBoostRocket()
            else
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

    fun getMostEffectiveTool(block: BlockState): ItemStack
    {
        val selectedItem = player.inventory.getStack(player.inventory.selectedSlot)
        if (selectedItem.item is ToolItem)
            return selectedItem
        val result = toolBelt.maxByOrNull { it.getMiningSpeedMultiplier(block) } ?: ItemStack.EMPTY
        return if (result.getMiningSpeedMultiplier(block) > 1.0f)
            result
        else if (block.material == Material.GLASS && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, toolBelt[SLOT_INDEX_PICKAXE]) > 0)
            toolBelt[SLOT_INDEX_PICKAXE] //Here we apply a silk touch pickaxe (if present) as a tool to mine glass-alike blocks
        else
            ItemStack.EMPTY
    }

    fun getDisplayedUtilities(): Array<ItemStack>
    {
        return arrayOf(findNextUtility(-1).first, getSelectedUtilityStack(), findNextUtility(1).first)
    }

    fun getSelectedUtilityStack(): ItemStack
    {
        return utilityBelt[selectedUtility]
    }

    fun getAvailableDeepPocketsRange(): IntRange
    {
        return INVENTORY_ADDON_DEEP_POCKETS_RANGE.first until
                INVENTORY_ADDON_DEEP_POCKETS_RANGE.first + getExtensionRows() * VANILLA_ROW_LENGTH
    }

    fun getUnavailableDeepPocketsRange(): IntRange
    {
        return getAvailableDeepPocketsRange().last + 1 .. INVENTORY_ADDON_DEEP_POCKETS_RANGE.last
    }

    fun getExtensionRows(): Int
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
        var selectedHotBarSection = -1
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