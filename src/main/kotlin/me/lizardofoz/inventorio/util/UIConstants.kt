package me.lizardofoz.inventorio.util

import java.awt.Point
import java.awt.Rectangle
import java.awt.geom.Point2D

const val SLOT_UI_SIZE = 18

val CANVAS_INVENTORY_TOP = Point2D.Float(0f, 0f)
val GUI_INVENTORY_TOP = Rectangle(0, 0, 198, 79)

val CANVAS_INVENTORY_MAIN = Point2D.Float(0f, 79f)
fun GUI_INVENTORY_MAIN(extensionRows: Int) = Rectangle(0, 79 + DEEP_POCKETS_EXTRA_HEIGH(extensionRows), 198, 87)
fun SLOTS_INVENTORY_MAIN(extensionRows: Int) = Point(8, 84 + DEEP_POCKETS_EXTRA_HEIGH(extensionRows))
fun SLOTS_INVENTORY_HOTBAR(extensionRows: Int) = Point(8, 142 + DEEP_POCKETS_EXTRA_HEIGH(extensionRows))

val CANVAS_INVENTORY_EXTENSION = Point2D.Float(0f, 166f)
fun GUI_INVENTORY_EXTENSION(extensionRows: Int) = Rectangle(0, 79, 198, DEEP_POCKETS_EXTRA_HEIGH(extensionRows))
val SLOT_INVENTORY_EXTENSION = Point(8, 84)

val CANVAS_UTILITY_BELT_COLUMN_2 = Point2D.Float(198f, 0f)
val GUI_UTILITY_BELT_COLUMN_2 = Rectangle(94, 7, 18, 72)
val SLOT_UTILITY_BELT_COLUMN_1 = Point(77, 8)

fun GUI_TOOL_BELT(extensionRows: Int) = Rectangle(173, 69 + DEEP_POCKETS_EXTRA_HEIGH(extensionRows), 18, 90)
val CANVAS_TOOL_BELT = Point2D.Float(216f, 0f)
fun SLOT_TOOL_BELT(extensionRows: Int) = Point(174, 70 + DEEP_POCKETS_EXTRA_HEIGH(extensionRows))

val CANVAS_TOOLS = Rectangle(234, 22, 16, 16)

val CANVAS_UTILITY_BELT_FRAME = Rectangle(234, 0, 22, 22)
val GUI_UTILITY_BELT_FRAME_ORIGIN = Point(74, 5)

fun DEEP_POCKETS_EXTRA_HEIGH(extensionRows: Int) = if (extensionRows <= 0) 0 else 4 + extensionRows * 18
const val CRAFTING_GRID_OFFSET_X = 20