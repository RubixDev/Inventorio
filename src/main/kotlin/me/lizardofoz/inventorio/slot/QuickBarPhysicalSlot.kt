package me.lizardofoz.inventorio.slot

import net.minecraft.inventory.Inventory
import net.minecraft.screen.slot.Slot

class QuickBarPhysicalSlot(val shortcutSlot: QuickBarShortcutSlot, inventory: Inventory, index: Int, x: Int, y: Int) : Slot(inventory, index, x, y)