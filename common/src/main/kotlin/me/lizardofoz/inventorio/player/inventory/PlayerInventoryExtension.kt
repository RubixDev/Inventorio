package me.lizardofoz.inventorio.player.inventory

import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.mixin.accessor.SimpleInventoryAccessor
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.util.*
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import kotlin.math.sign

abstract class PlayerInventoryExtension protected constructor(val player: PlayerEntity) : SimpleInventory(DEEP_POCKETS_MAX_SIZE + TOOL_BELT_SIZE + UTILITY_BELT_FULL_SIZE)
{
    @JvmField val stacks: MutableList<ItemStack>
    @JvmField val deepPockets: MutableList<ItemStack>
    @JvmField val toolBelt: MutableList<ItemStack>
    @JvmField val utilityBelt: MutableList<ItemStack>

    var selectedUtility = 0
        set(value)
        {
            field = value.coerceIn(0, 7)
        }

    init
    {
        stacks = (this as SimpleInventoryAccessor).stacks!!
        deepPockets = stacks.subList(INVENTORY_ADDON_DEEP_POCKETS_RANGE.first, INVENTORY_ADDON_DEEP_POCKETS_RANGE.last + 1)
        toolBelt = stacks.subList(INVENTORY_ADDON_TOOL_BELT_RANGE.first, INVENTORY_ADDON_TOOL_BELT_RANGE.last + 1)
        utilityBelt = stacks.subList(INVENTORY_ADDON_UTILITY_BELT_RANGE.first, INVENTORY_ADDON_UTILITY_BELT_RANGE.last + 1)
    }

    fun cloneFrom(oldAddon: PlayerInventoryAddon)
    {
        for ((index, stack) in oldAddon.stacks.withIndex())
            this.setStack(index, stack)
    }

    fun asMap(): Map<Int, ItemStack>
    {
        return stacks.withIndex().associate { it.index to it.value }
    }

    fun fromMap(items: Map<Int, ItemStack>)
    {
        items.forEach { setStack(it.key, it.value) }
    }

    fun dropAll()
    {
        for ((index, itemStack) in stacks.withIndex())
        {
            if (!EnchantmentHelper.hasVanishingCurse(itemStack))
                player.dropItem(itemStack, true, false)
            stacks[index] = ItemStack.EMPTY
        }
    }

    fun switchToNextUtility(direction: Int, skipEmptySlots: Boolean): Boolean
    {
        val slotIndex = findNextUtility(direction, skipEmptySlots).second
        if (slotIndex == -1)
            return false
        selectedUtility = slotIndex
        InventorioNetworking.INSTANCE.c2sSelectUtilitySlot(slotIndex)
        return true
    }

    /**
     * Returns (ItemStack.EMPTY; -1) if no next utility was found
     */
    fun findNextUtility(direction: Int, skipEmptySlots: Boolean): Pair<ItemStack, Int>
    {
        val range = if (direction.sign >= 0)
            (selectedUtility + 1 until getAvailableUtilityBeltSize()) + (0 until selectedUtility)
        else
            (selectedUtility - 1 downTo 0) + (getAvailableUtilityBeltSize() - 1 downTo selectedUtility + 1)

        for (i in range)
            if (!skipEmptySlots || utilityBelt[i].isNotEmpty)
                return Pair(utilityBelt[i], i)

        return Pair(ItemStack.EMPTY, -1)
    }

    /**
     * Returns -1 if no empty utility slot was found
     */
    fun findEmptyUtility(direction: Int): Int
    {
        val range = if (direction.sign >= 0)
            (selectedUtility + 1 until getAvailableUtilityBeltSize()) + (0 until selectedUtility)
        else
            (selectedUtility - 1 downTo 0) + (getAvailableUtilityBeltSize() - 1 downTo selectedUtility + 1)

        for (i in range)
            if (utilityBelt[i].isEmpty)
                return i

        return -1
    }

    /**
     * Note: this class returns the range within the SCREEN HANDLER, which is different from the range within the inventory
     */
    fun getAvailableUtilityBeltSize(): Int
    {
        return if (getDeepPocketsRowCount() > 0) UTILITY_BELT_FULL_SIZE else UTILITY_BELT_SMALL_SIZE
    }

    /**
     * Note: this class returns the range within the INVENTORY, which is different from the range within the Screen Handler
     */
    fun getAvailableDeepPocketsRange(): IntRange
    {
        return INVENTORY_ADDON_DEEP_POCKETS_RANGE.first expandBy getDeepPocketsRowCount() * VANILLA_ROW_LENGTH
    }

    /**
     * Note: this class returns the range within the INVENTORY, which is different from the range within the Screen Handler
     */
    fun getUnavailableDeepPocketsRange(): IntRange
    {
        return getAvailableDeepPocketsRange().last + 1..INVENTORY_ADDON_DEEP_POCKETS_RANGE.last
    }

    fun getDeepPocketsRowCount(): Int
    {
        return EnchantmentHelper.getEquipmentLevel(DeepPocketsEnchantment, player).coerceIn(0, 3)
    }

    protected fun areItemsSimilar(stack1: ItemStack, stack2: ItemStack): Boolean
    {
        return stack1.isNotEmpty && stack1.item === stack2.item && ItemStack.areTagsEqual(stack1, stack2)
    }

    fun getTotalAmount(sampleStack: ItemStack): Int
    {
        var count = 0
        for (i in 0 until player.inventory.size())
        {
            val stack = player.inventory.getStack(i)
            if (areItemsSimilar(stack, sampleStack))
                count += stack.count
        }
        return count + stacks.filter { areItemsSimilar(it, sampleStack) }.sumOf { it.count }
    }
}