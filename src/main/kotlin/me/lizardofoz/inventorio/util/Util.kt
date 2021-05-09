package me.lizardofoz.inventorio.util

import com.google.common.collect.ImmutableList
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon
import net.minecraft.item.*

enum class SegmentedHotbar
{
    OFF, ONLY_VISUAL, ON
}

interface ScreenHandlerDuck
{
    var addon: PlayerScreenHandlerAddon
}

interface InventoryDuck
{
    val addon: PlayerInventoryAddon
}

val ItemStack.isNotEmpty: Boolean
    get() = !this.isEmpty

val toolBeltSlotPredicates = generateToolBeltPredicates()

private fun generateToolBeltPredicates(): List<(ItemStack) -> Boolean>
{
    val toolBeltItems = mutableListOf<(ItemStack) -> Boolean>()
    toolBeltItems.add { it.item is PickaxeItem }
    toolBeltItems.add { it.item is SwordItem || it.item is TridentItem }
    toolBeltItems.add { it.item is AxeItem }
    toolBeltItems.add { it.item is ShovelItem }
    toolBeltItems.add { it.item is HoeItem || it.item is ShearsItem }

    return ImmutableList.copyOf(toolBeltItems)
}
