package me.lizardofoz.inventorio.slot

import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag

/**
 * This isn't a great thing, since [ItemStack] is final (changed with an access widener),
 * but so many things call [ItemStack.decrement] and [ItemStack.setCount] directly, my hand was forced
 */
class QuickBarItemStack(val inventorio: PlayerInventoryAddon, item: ItemConvertible, tag: CompoundTag?) : ItemStack(item)
{
    constructor(inventory: PlayerInventoryAddon, stack: ItemStack) : this(inventory, stack.item, stack.tag)

    init
    {
        this.tag = tag
    }

    override fun setCount(count: Int)
    {
    }

    override fun getCount(): Int
    {
        //return Math.max(1, inventorio.getTotalAmount(this))
        return 1
    }

    override fun decrement(amount: Int)
    {
        inventorio.decrementFromQuickBar(this, amount)
    }
}