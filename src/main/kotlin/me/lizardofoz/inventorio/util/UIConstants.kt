package me.lizardofoz.inventorio.util

import java.awt.Point
import java.awt.Rectangle

val CANVAS_INVENTORY_TOP = Point(0, 0)
val GUI_INVENTORY_TOP = Rectangle(0, 0, 198, 79)

val CANVAS_INVENTORY_MAIN = Point(0, 79)
fun GUI_INVENTORY_MAIN(extensionRows: Int) = Rectangle(0, 79 + GUI_INVENTORY_EXTENSION(extensionRows).height, 198, 87)
fun SLOTS_INVENTORY_MAIN(extensionRows: Int) = Point(8, 84 + GUI_INVENTORY_EXTENSION(extensionRows).height)
fun SLOTS_INVENTORY_HOTBAR(extensionRows: Int) = Point(8, 142 + GUI_INVENTORY_EXTENSION(extensionRows).height)

val CANVAS_INVENTORY_EXTENSION = Point(0, 166)
fun GUI_INVENTORY_EXTENSION(extensionRows: Int) = Rectangle(0, 79, 198, if (extensionRows <= 0) 0 else 4 + extensionRows * 18)
val SLOT_INVENTORY_EXTENSION = Point(8, 84)

val CANVAS_UTILITY_BELT_COLUMN_2 = Point(198, 0)
val GUI_UTILITY_BELT_COLUMN_2 = Rectangle(94, 7, 18, 72)
val SLOT_UTILITY_BELT_COLUMN_1 = Point(77, 8)

val GUI_TOOL_BELT = Rectangle(173, 69, 18, 90)
val CANVAS_TOOL_BELT = Point(216, 0)
val SLOT_TOOL_BELT = Point(174, 70)

val CANVAS_TOOLS = Rectangle(234, 22, 16, 16)

val CANVAS_UTILITY_BELT_FRAME = Rectangle(234, 0, 22, 22)
val GUI_UTILITY_BELT_FRAME_ORIGIN = Point(74, 5)

const val CRAFTING_GRID_OFFSET_X = 20