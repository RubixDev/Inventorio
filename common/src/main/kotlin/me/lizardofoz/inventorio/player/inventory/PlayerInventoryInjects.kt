package me.lizardofoz.inventorio.player.inventory

import me.lizardofoz.inventorio.util.INVENTORY_HOTBAR_RANGE
import me.lizardofoz.inventorio.util.isNotEmpty
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.RangedWeaponItem

abstract class PlayerInventoryInjects protected constructor(player: PlayerEntity) : PlayerInventoryExtension(player)
{
    fun mendToolBeltItems(experience: Int): Int
    {
        var amount = experience
        for (itemStack in toolBelt)
            if (itemStack.isNotEmpty && itemStack.isDamaged && EnchantmentHelper.getLevel(Enchantments.MENDING, itemStack) > 0)
            {
                val delta = Math.min(amount * 2, itemStack.damage)
                amount -= delta
                itemStack.damage -= delta
                return amount
            }
        return amount
    }

    /**
     * Returns null if we want to proceed with vanilla behaviour.
     */
    fun getActiveArrowType(bowStack: ItemStack): ItemStack?
    {
        val predicate = (bowStack.item as RangedWeaponItem).heldProjectiles
        return stacks.firstOrNull { predicate.test(it) }
    }

    /**
     * Returns false if we want to proceed with vanilla behavior
     * Returns true if we did our own logic instead
     */
    fun insertOnlySimilarStack(sourceStack: ItemStack): Boolean
    {
        //Skip unstackable items
        if (!sourceStack.isStackable)
            return false
        //Skip items which can go into hotbar (and allow vanilla to handle it)
        for (i in INVENTORY_HOTBAR_RANGE)
        {
            val hotbarStack = player.inventory.main[i]
            if (areItemsSimilar(sourceStack, hotbarStack) && hotbarStack.count < hotbarStack.maxCount)
                return false
        }
        for (utilityStack in utilityBelt)
        {
            if (areItemsSimilar(sourceStack, utilityStack))
            {
                transfer(sourceStack, utilityStack)
                if (sourceStack.isEmpty)
                    return true
            }
        }
        for (index in getAvailableDeepPocketsRange())
        {
            val targetStack = deepPockets[index]
            if (areItemsSimilar(sourceStack, targetStack))
            {
                transfer(sourceStack, targetStack)
                if (sourceStack.isEmpty)
                    return true
            }
        }
        return false
    }

    /**
     * Returns false if we want to proceed with vanilla behavior
     * Returns true if we did our own logic instead
     */
    fun insertStackIntoEmptySlot(sourceStack: ItemStack): Boolean
    {
        for (index in getAvailableDeepPocketsRange())
        {
            if (deepPockets[index].isEmpty)
            {
                deepPockets[index] = sourceStack.copy()
                sourceStack.count = 0
                markDirty()
                return true
            }
        }
        return false
    }

    private fun transfer(sourceStack: ItemStack, targetStack: ItemStack)
    {
        val i = Math.min(this.maxCountPerStack, targetStack.maxCount)
        val j = Math.min(sourceStack.count, i - targetStack.count)
        if (j > 0)
        {
            targetStack.increment(j)
            sourceStack.decrement(j)
            markDirty()
        }
    }

    /**
     * Returns false if we want to proceed with vanilla behavior
     * Returns true if we did our own logic instead
     */
    fun removeOne(sourceStack: ItemStack): Boolean
    {
        for ((index, stack) in stacks.withIndex())
        {
            if (stack === sourceStack)
            {
                stacks[index] = ItemStack.EMPTY
                return true
            }
        }
        return false
    }
}