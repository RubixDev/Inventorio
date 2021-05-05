package me.lizardofoz.inventorio.player

import com.google.common.collect.ImmutableList
import me.lizardofoz.inventorio.client.ui.PlayerInventoryUIAddon
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.screenhandler.PlayerScreenHandlerAddon
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.ToolItem
import net.minecraft.util.collection.DefaultedList
import kotlin.math.max
import kotlin.math.sign

/**
 * This class is responsible for the inventory itself,
 * while [PlayerInventoryUIAddon] is responsible for the visuals of the UI
 * and [PlayerScreenHandlerAddon] is responsible for the slots and player interaction with them
 */
class PlayerInventoryAddon internal constructor(private val inventory: PlayerInventory)
{
    internal val extension = DefaultedList.ofSize(EXTENSION_SIZE, ItemStack.EMPTY)!!
    internal val toolBelt = DefaultedList.ofSize(TOOL_BELT_SIZE, ItemStack.EMPTY)!!
    internal val utilityBelt = DefaultedList.ofSize(UTILITY_BELT_SIZE, ItemStack.EMPTY)!!
    //Minecraft has hardcoded slot indexes (40 for inventory and 45 for screen handlers) for the offhand.
    //This is a hack to counter-act any potential unaccounted Mojang hardcoding
    internal val dudOffhand = DefaultedList.ofSize(1, ItemStack.EMPTY)!!

    //Note: the order of elements is important. Don't change it.
    val combinedInventory = ImmutableList.of(inventory.main, inventory.armor, dudOffhand, extension, toolBelt, utilityBelt)!!

    private val player = inventory.player!!
    private val playerAddon by lazy { PlayerAddon[player] }

    var selectedUtility = 0
    var mainHandDisplayTool = ItemStack.EMPTY!!

    //==============================
    //Injects. These functions are either injected or redirected to by a mixin of a [PlayerInventory] class
    //==============================

    fun size(): Int
    {
        return inventory.main.size + inventory.armor.size + dudOffhand.size + extension.size + toolBelt.size + utilityBelt.size
    }

    fun getMainHandStack(): ItemStack
    {
        return if (player.handSwinging && mainHandDisplayTool.isNotEmpty)
            mainHandDisplayTool
        else
            inventory.main[inventory.selectedSlot]
    }

    fun getEmptyExtensionSlot(): Int
    {
        val offset = EXTENSION_RANGE.first
        for (i in playerAddon.getAvailableExtensionSlotsRange())
        {
            if (extension[i - offset].isEmpty)
                return i
        }
        return -1
    }

    fun getOccupiedExtensionSlotWithRoomForStack(stack: ItemStack): Int
    {
        val offset = EXTENSION_RANGE.first
        for (i in playerAddon.getAvailableExtensionSlotsRange())
        {
            if (canStackAddMore(extension[i - offset], stack))
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

    fun prePlayerAttack(target: Entity)
    {
        player.handSwinging = true
        mainHandDisplayTool = if (!toolBelt[SLOT_INDEX_SWORD].isEmpty)
            toolBelt[SLOT_INDEX_SWORD]
        else
            toolBelt[SLOT_INDEX_AXE]
        player.attributes.addTemporaryModifiers(mainHandDisplayTool.getAttributeModifiers(EquipmentSlot.MAINHAND))
    }

    fun postPlayerAttack(target: Entity)
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
        PlayerAddon.Client.triesToUseUtility = true
        (MinecraftClient.getInstance() as MinecraftClientAccessor).invokeDoItemUse()
        PlayerAddon.Client.triesToUseUtility = false
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
        return arrayOf(findNextUtility(-1).first, utilityBelt[selectedUtility], findNextUtility(1).first)
    }

    private fun canStackAddMore(existingStack: ItemStack, stack: ItemStack): Boolean
    {
        return !existingStack.isEmpty && areItemsSimilar(existingStack, stack) && existingStack.isStackable && existingStack.count < existingStack.maxCount && existingStack.count < 64
    }

    /**
     * Checks if two item stacks are similar AND non-empty.
     *
     * ItemStack counts are not considered.
     */
    private fun areItemsSimilar(stack1: ItemStack, stack2: ItemStack): Boolean
    {
        return stack1.isNotEmpty && stack1.item === stack2.item && ItemStack.areTagsEqual(stack1, stack2)
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