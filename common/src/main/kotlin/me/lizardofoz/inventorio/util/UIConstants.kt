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

@JvmField val CANVAS_UTILITY_BELT_COLUMN_2 = Point2F(238f, 22f)
@JvmField val GUI_UTILITY_BELT_COLUMN_2 = Rectangle(94, 7, 18, 72)
@JvmField val SLOT_UTILITY_BELT_COLUMN_1 = Point2I(77, 8)

@JvmField val CANVAS_TOOL_BELT = Point2F(238f, 95f)

@JvmField val CANVAS_TOOL_BELT_UI_EXTENSION = Rectangle(198, 0, 27, 166)
@JvmField val GUI_TOOL_BELT_UI_EXTENSION = Point2I(191, 0)

@JvmField val CANVAS_UTILITY_BELT_FRAME = Rectangle(234, 0, 22, 22)
@JvmField val GUI_UTILITY_BELT_FRAME_ORIGIN = Point2I(74, 5)

fun DEEP_POCKETS_EXTRA_HEIGHT(deepPocketsRows: Int) = if (deepPocketsRows <= 0) 0 else 4 + deepPocketsRows * 18
const val CRAFTING_GRID_OFFSET_X = 20

@JvmField val GUI_RECIPE_WIDGET_BUTTON = Rectangle(125, 61, 20, 18)

@JvmField val CANVAS_INVENTORY_TEXTURE_SIZE = Point2I(256, 256)
@JvmField val CANVAS_WIDGETS_TEXTURE_SIZE = Point2I(256, 64)

@JvmField val CANVAS_TOGGLE_BUTTON_ON = Point2I(243, 114)
@JvmField val CANVAS_TOGGLE_BUTTON_OFF = Point2I(243, 120)
const val CANVAS_TOGGLE_BUTTON_HOVER_SHIFT = 12
@JvmField val GUI_TOGGLE_BUTTON_OFFSET = Rectangle(-19, 5, 13, 6)
