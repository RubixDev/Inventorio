package me.danetnaverno.inventorio.player

import com.google.common.collect.ImmutableList
import me.danetnaverno.inventorio.mixin.client.MinecraftClientAccessor
import me.danetnaverno.inventorio.packet.InventorioNetworking
import me.danetnaverno.inventorio.quickbar.QuickBarInventory
import me.danetnaverno.inventorio.slot.QuickBarItemStack
import me.danetnaverno.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import kotlin.math.max
import kotlin.math.sign

class PlayerInventoryAddon internal constructor(val inventory: PlayerInventory)
{
    internal val extension = DefaultedList.ofSize(maxExtensionSlots, ItemStack.EMPTY)!!
    internal val toolBelt = DefaultedList.ofSize(toolbeltLength, ItemStack.EMPTY)!!
    internal val utilityBelt = DefaultedList.ofSize(utilityBarLength, ItemStack.EMPTY)!!
    //Minecraft has hardcoded slot indexes (40 for inventory and 45 for screen handlers) for the offhand.
    //This is a hack to counter-act any potential unaccounted Mojang hardcoding
    internal val dudOffhand = DefaultedList.ofSize(1, ItemStack.EMPTY)!!
    //Some QuickBar modes allow the player to store some items _physically_ on the QuickBar.
    //Thus, they have to be saved within player's inventory
    internal val physicalQuickQar = DefaultedList.ofSize(inventorioRowLength, ItemStack.EMPTY)!!
    val shortcutQuickBar = QuickBarInventory(this)

    //Note: the order of elements is important. Don't change it.
    val combinedInventory = ImmutableList.of(inventory.main, inventory.armor, dudOffhand, extension, toolBelt, utilityBelt, physicalQuickQar)!!

    private val player = inventory.player!!
    private val playerAddon by lazy { PlayerAddon[player] }

    var selectedUtility = 0
    var mainHandDisplayTool = ItemStack.EMPTY!!

    //==============================
    //Injects. These functions are either injected or redirected to by a mixin of a [PlayerInventory] class
    //==============================

    fun size(): Int
    {
        return inventory.main.size + inventory.armor.size + dudOffhand.size + extension.size + toolBelt.size + utilityBelt.size + physicalQuickQar.size
    }

    fun getMainHandStack(): ItemStack
    {
        return if (player.handSwinging && mainHandDisplayTool.isNotEmpty)
            mainHandDisplayTool
        else if (physicalQuickQar[inventory.selectedSlot].isNotEmpty)
            physicalQuickQar[inventory.selectedSlot]
        else if (player.isCreative || hasAnySimilar(playerAddon.inventoryAddon.shortcutQuickBar.getStack(inventory.selectedSlot)))
            playerAddon.inventoryAddon.shortcutQuickBar.getStack(inventory.selectedSlot)
        else
            ItemStack.EMPTY
    }

    fun getEmptyExtensionSlot(): Int
    {
        val offset = extensionSlotsRange.first
        for (i in MathStuffConstants.getAvailableExtensionSlotsRange(player))
        {
            if (extension[i - offset].isEmpty)
                return i
        }
        return -1
    }

    fun getOccupiedExtensionSlotWithRoomForStack(stack: ItemStack): Int
    {
        val offset = extensionSlotsRange.first
        for (i in MathStuffConstants.getAvailableExtensionSlotsRange(player))
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

    @Environment(EnvType.CLIENT)
    fun scrollInHotbar(scrollAmount: Double)
    {
        inventory.selectedSlot -= scrollAmount.sign.toInt()
        while (inventory.selectedSlot < 0)
            inventory.selectedSlot += inventorioRowLength
        while (inventory.selectedSlot >= inventorioRowLength)
            inventory.selectedSlot -= inventorioRowLength
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
            (selectedUtility + 1 until utilityBarLength) + (0 until selectedUtility)
        else
            (selectedUtility - 1 downTo 0) + (utilityBarLength - 1 downTo selectedUtility + 1)

        for (i in range)
            if (utilityBelt[i].isNotEmpty)
                return Pair(utilityBelt[i], i)

        return Pair(ItemStack.EMPTY, -1)
    }

    fun decrementFromQuickBar(stack: QuickBarItemStack, amount: Int)
    {
        for (section in combinedInventory)
        {
            val item = section.firstOrNull { areItemsSimilar(stack, it) }
            if (item != null)
            {
                item.decrement(amount)
                return
            }
        }
    }

    //==============================
    //Utility methods
    //==============================

    fun hasAnySimilar(stack: ItemStack): Boolean
    {
        return combinedInventory.any { inv -> inv.any { item -> areItemsSimilar(stack, item) }}
                || areItemsSimilar(inventory.cursorStack, stack)
    }

    fun getTotalAmount(stack: ItemStack): Int
    {
        if (MathStuffConstants.canPlayerStoreItemStackPhysicallyInQuickBar(player, stack))
            //return stack.count
            throw IllegalStateException("A physical item is stored in a shortcut quickbar")
        var result = 0

        for (section in combinedInventory)
            for (itStack in section)
                if (areItemsSimilar(stack, itStack))
                    result += itStack.count

        if (areItemsSimilar(stack, inventory.cursorStack))
            result += inventory.cursorStack.count

        return result
    }

    fun getMostEffectiveTool(block: BlockState): ItemStack
    {
        val result = toolBelt.maxByOrNull { it.getMiningSpeedMultiplier(block) } ?: ItemStack.EMPTY
        return if (result.getMiningSpeedMultiplier(block) > 1.0f)
            result
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