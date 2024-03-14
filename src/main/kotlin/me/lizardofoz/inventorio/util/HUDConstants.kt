@file:JvmName("HUDConstants")

package me.lizardofoz.inventorio.util

@JvmField val CANVAS_VANILLA_SELECTION_FRAME_POS = Point2F(188f, 0f)
@JvmField val CANVAS_VANILLA_SELECTION_FRAME_SIZE = Rectangle(0, 0, 24, 24)

@JvmField val CANVAS_SECTION_SELECTION_FRAME = Point2F(0f, 0f)
@JvmField val HUD_SECTION_SELECTION = Rectangle(62, 23, 64, 24)
@JvmField val SLOT_HOTBAR_SIZE = Rectangle(0, 0, 20, 19)

@JvmField val CANVAS_ACTIVE_TOOL_FRAME = Point2F(234f, 0f)
@JvmField val HUD_ACTIVE_TOOL_FRAME = Rectangle(133, 22, 22, 22)
@JvmField val SLOT_ACTIVE_TOOL_FRAME = Point2I(136, 19)

@JvmField val CANVAS_UTILITY_BELT = Point2F(84f, 0f)
@JvmField val CANVAS_UTILITY_BELT_BCG = Point2F(132f, 0f)
@JvmField val HUD_UTILITY_BELT = Rectangle(121, 22, 48, 22)
@JvmField val SLOT_UTILITY_BELT_1 = Point2I(147, 18)
@JvmField val SLOT_UTILITY_BELT_2 = Point2I(110, 18)
@JvmField val SLOT_UTILITY_BELT_3 = Point2I(105, 19)

@JvmField val CANVAS_SEGMENTED_HOTBAR = Point2F(0f, 24f)
@JvmField val HUD_SEGMENTED_HOTBAR = Rectangle(65, 22, 190, 22)

const val HUD_SEGMENTED_HOTBAR_GAP = 4

const val LEFT_HANDED_UTILITY_BELT_OFFSET = 254
const val LEFT_HANDED_DISPLAY_TOOL_OFFSET = -228
