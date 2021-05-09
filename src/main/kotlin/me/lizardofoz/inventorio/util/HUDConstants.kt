package me.lizardofoz.inventorio.util

import java.awt.Point
import java.awt.Rectangle
import java.awt.geom.Point2D

val CANVAS_VANILLA_SELECTION_FRAME_POS = Point2D.Float(188f, 0f)
val CANVAS_VANILLA_SELECTION_FRAME_SIZE = Rectangle(0, 0, 24, 24)

val CANVAS_SECTION_SELECTION_FRAME = Point2D.Float(0f, 0f)
val HUD_SECTION_SELECTION = Rectangle(62, 23, 64, 24)
val SLOT_HOTBAR_SIZE = Rectangle(0, 0, 20, 19)

val CANVAS_ACTIVE_TOOL_FRAME = Point2D.Float(234f, 0f)
val HUD_ACTIVE_TOOL_FRAME = Rectangle(133, 22, 22, 22)
val SLOT_ACTIVE_TOOL_FRAME = Point(136, 19)

val CANVAS_UTILITY_BELT = Point2D.Float(84f, 0f)
val CANVAS_UTILITY_BELT_BCG = Point2D.Float(132f, 0f)
val HUD_UTILITY_BELT = Rectangle(121, 22, 48, 22)
val SLOT_UTILITY_BELT_1 = Point(147, 18)
val SLOT_UTILITY_BELT_2 = Point(110, 18)
val SLOT_UTILITY_BELT_3 = Point(105, 19)

val CANVAS_SEGMENTED_HOTBAR = Point2D.Float(0f, 24f)
val HUD_SEGMENTED_HOTBAR = Rectangle(65, 22, 190, 22)

const val HUD_SEGMENTED_HOTBAR_GAP = 4

const val LEFT_HANDED_UTILITY_BELT_OFFSET = 254
const val LEFT_HANDED_DISPLAY_TOOL_OFFSET = -228