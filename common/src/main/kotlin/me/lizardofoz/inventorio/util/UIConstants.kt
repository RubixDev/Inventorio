@file:JvmName("UIConstants")
@file:Suppress("FunctionName")

package me.lizardofoz.inventorio.util

const val SLOT_UI_SIZE = 18
const val INVENTORY_HEIGHT = 166
const val INVENTORY_TITLE_X = 97

@JvmField val CANVAS_INVENTORY_TOP = Point2F(0f, 0f)
@JvmField val GUI_INVENTORY_TOP = Rectangle(0, 0, 198, 79)

@JvmField val CANVAS_INVENTORY_MAIN = Point2F(0f, 79f)
fun GUI_INVENTORY_MAIN(deepPocketsRows: Int) = Rectangle(0, 79 + DEEP_POCKETS_EXTRA_HEIGHT(deepPocketsRows), 198, 87)
fun SLOTS_INVENTORY_MAIN(deepPocketsRows: Int) = Point2I(8, 84 + DEEP_POCKETS_EXTRA_HEIGHT(deepPocketsRows))
fun SLOTS_INVENTORY_HOTBAR(deepPocketsRows: Int) = Point2I(8, 142 + DEEP_POCKETS_EXTRA_HEIGHT(deepPocketsRows))

@JvmField val CANVAS_INVENTORY_DEEP_POCKETS = Point2F(0f, 166f)
fun GUI_INVENTORY_DEEP_POCKETS(deepPocketsRows: Int) = Rectangle(0, 79, 198, DEEP_POCKETS_EXTRA_HEIGHT(deepPocketsRows))
@JvmField val SLOT_INVENTORY_DEEP_POCKETS = Point2I(8, 84)

@JvmField val CANVAS_UTILITY_BELT_COLUMN_2 = Point2F(198f, 0f)
@JvmField val GUI_UTILITY_BELT_COLUMN_2 = Rectangle(94, 7, 18, 72)
@JvmField val SLOT_UTILITY_BELT_COLUMN_1 = Point2I(77, 8)

fun GUI_TOOL_BELT(deepPocketsRows: Int) = Rectangle(173, 69 + DEEP_POCKETS_EXTRA_HEIGHT(deepPocketsRows), 18, 90)
@JvmField val CANVAS_TOOL_BELT = Point2F(216f, 0f)
fun SLOT_TOOL_BELT(deepPocketsRows: Int) = Point2I(174, 70 + DEEP_POCKETS_EXTRA_HEIGHT(deepPocketsRows))

@JvmField val CANVAS_TOOLS = Rectangle(234, 22, 16, 16)

@JvmField val CANVAS_UTILITY_BELT_FRAME = Rectangle(234, 0, 22, 22)
@JvmField val GUI_UTILITY_BELT_FRAME_ORIGIN = Point2I(74, 5)

fun DEEP_POCKETS_EXTRA_HEIGHT(deepPocketsRows: Int) = if (deepPocketsRows <= 0) 0 else 4 + deepPocketsRows * 18
const val CRAFTING_GRID_OFFSET_X = 20

@JvmField val GUI_RECIPE_WIDGET_WINDOW_OFFSET = Point2I(0, 19)
@JvmField val GUI_RECIPE_WIDGET_BUTTON_OFFSET = Point2I(124, 22)
@JvmField val CANVAS_RECIPE_WIDGET_BUTTON = Rectangle(0, 0, 20, 18)
const val GUI_RECIPE_WIDGET_BUTTON_TOOLTIP_OFFSET = 19

@JvmField val CANVAS_INVENTORY_TEXTURE_SIZE = Point2I(256, 256)
@JvmField val CANVAS_WIDGETS_TEXTURE_SIZE = Point2I(256, 64)