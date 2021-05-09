package me.lizardofoz.inventorio.util

import com.google.common.collect.ImmutableList
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon
import net.minecraft.item.*

class Point2I(val x: Int, val y: Int)
class Point2F(val x: Float, val y: Float)
class Rectangle(val x: Int, val y: Int, val width: Int, val height: Int)

enum class SegmentedHotbar
{
    OFF, ONLY_VISUAL, ON
}

interface ScreenHandlerDuck
{
    var screenHandlerAddon: PlayerScreenHandlerAddon
}

interface InventoryDuck
{
    val inventorioAddon: PlayerInventoryAddon
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
