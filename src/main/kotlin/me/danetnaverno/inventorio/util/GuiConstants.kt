package me.danetnaverno.inventorio.util

import java.awt.Rectangle

val GUI_INVENTORY_SLOT_SIZE = 18

val GUI_PLAYER_INVENTORY_TOP_WITH_DECO = Rectangle(-13, -13, 256, 96)
fun GUI_PLAYER_INVENTORY_EXTENSION_WITH_DECO_P1(extensionRowCount: Int) = Rectangle(
        -13, 70,
        256, 13 + extensionRowCount * GUI_INVENTORY_SLOT_SIZE)

fun GUI_PLAYER_INVENTORY_EXTENSION_WITH_DECO_P2(extensionRowCount: Int) = Rectangle(
        -13, 83 + extensionRowCount * GUI_INVENTORY_SLOT_SIZE,
        256, 17)

fun GUI_PLAYER_INVENTORY_MAIN_WITH_DECO(extensionRowCount: Int) = Rectangle(
-13, 83 + GUI_PLAYER_INVENTORY_EXTENSION_PART(extensionRowCount).height,
256, 96)

fun GUI_PLAYER_INVENTORY_QUICK_BAR(extensionRowCount: Int) = Rectangle(
        7, 141 + GUI_PLAYER_INVENTORY_EXTENSION_PART(extensionRowCount).height,
        216, 18)

val GUI_PLAYER_INVENTORY_UTILITY_EXT = Rectangle(86, -1, 34, 88)
val GUI_PLAYER_INVENTORY_UTILITY_SELECTION_START_POS = Rectangle(70, 1, 18, 72)

val GUI_PLAYER_INVENTORY_TOP_PART = Rectangle(0, 0, 230, 79)
fun GUI_PLAYER_INVENTORY_EXTENSION_PART(extensionRowCount: Int) = Rectangle(
        0, 79,
        230, if (extensionRowCount <= 0) 0 else (4 + extensionRowCount * GUI_INVENTORY_SLOT_SIZE))


val GUI_EXTERNAL_INVENTORY_TOP_PART = Rectangle(-13, -13, 256, 30)
fun GUI_EXTERNAL_INVENTORY_EXTENSION_PART_TOP(extensionRowCount: Int) = Rectangle(
        -13, 4,
        256, 13 + extensionRowCount * GUI_INVENTORY_SLOT_SIZE)
fun GUI_EXTERNAL_INVENTORY_EXTENSION_PART_BOTTOM(extensionRowCount: Int) = Rectangle(
        -13, 17 + extensionRowCount * GUI_INVENTORY_SLOT_SIZE,
        256, 13 + 4)

fun GUI_EXTERNAL_INVENTORY_MAIN_PART(extensionRowCount: Int) = Rectangle(
        -13, 17 + if (extensionRowCount <= 0) 0 else (4 + extensionRowCount * GUI_INVENTORY_SLOT_SIZE),
        256, 96)

fun GUI_EXTERNAL_INVENTORY_INGORE_BUTTON(extensionRowCount: Int) = Rectangle(
        232, GUI_EXTERNAL_INVENTORY_MAIN_PART(extensionRowCount).y + 57,
        20, 20)