package me.lizardofoz.inventorio.util

import com.google.common.collect.ImmutableList
import net.minecraft.item.*

object SlotRestrictionFilters
{
    val toolBelt: List<(ItemStack) -> Boolean>

    init
    {
        val toolBeltItems = mutableListOf<(ItemStack) -> Boolean>()
        toolBeltItems.add { it.item is PickaxeItem }
        toolBeltItems.add { it.item is SwordItem || it.item is TridentItem }
        toolBeltItems.add { it.item is AxeItem }
        toolBeltItems.add { it.item is ShovelItem }
        toolBeltItems.add { it.item is HoeItem || it.item is ShearsItem }

        this.toolBelt = ImmutableList.copyOf(toolBeltItems)
    }
}