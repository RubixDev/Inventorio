package me.danetnaverno.inventorio.util

import java.awt.Rectangle

val SLOTS_PLAYER_INVENTORY_TOP_PART = Rectangle(0, 0, 230, 79)
fun SLOTS_PLAYER_INVENTORY_EXTENSION_PART(extensionRowCount: Int) = Rectangle(
        8, 84,
        230, if (extensionRowCount <= 0) 0 else (4 + extensionRowCount * INVENTORY_SLOT_SIZE))
fun SLOTS_PLAYER_INVENTORY_ENTIRE_MAIN_PART(extensionRowCount: Int) = Rectangle(
        8, 84 + SLOTS_PLAYER_INVENTORY_EXTENSION_PART(extensionRowCount).height,
        230, 87)

fun SLOTS_PLAYER_INVENTORY_QUICK_BAR(extensionRowCount: Int) = Rectangle(
        8, 142 + SLOTS_PLAYER_INVENTORY_EXTENSION_PART(extensionRowCount).height,
        216, 18)
val SLOTS_PLAYER_INVENTORY_TOOL_BELT = Rectangle(133, 62, 90, 18)
fun SLOTS_PLAYER_INVENTORY_TOOL_BELT_SLOT(slot: Int) = Rectangle(
        134 + INVENTORY_SLOT_SIZE * slot, 62,
        18, 18)
val SLOTS_PLAYER_INVENTORY_UTILITY_BAR_STANDARD = Rectangle(76, 7, 18, 72)
val SLOTS_PLAYER_INVENTORY_UTILITY_BAR_EXTENDED = Rectangle(94, 7, 18, 72)
val SLOTS_PLAYER_INVENTORY_UTILITY_BAR_TOTAL = Rectangle(77, 8, 36, 72)

