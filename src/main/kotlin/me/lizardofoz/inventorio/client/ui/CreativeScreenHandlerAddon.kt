package me.lizardofoz.inventorio.client.ui

import me.lizardofoz.inventorio.mixin.accessor.ScreenHandlerAccessor
import me.lizardofoz.inventorio.mixin.accessor.SlotAccessor
import me.lizardofoz.inventorio.mixin.client.accessor.CreativeInventoryScreenAccessor
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.slot.QuickBarSlot
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.collection.DefaultedList
import java.awt.Rectangle

@Environment(EnvType.CLIENT)
class CreativeScreenHandlerAddon internal constructor(val handler: CreativeInventoryScreen.CreativeScreenHandler) : ScreenHandlerAddon
{
    override fun tryInitialize(slot: Slot): Boolean
    {
        return true
    }

    override fun initialize(playerAddon: PlayerAddon)
    {
        disableSurvivalInventory(playerAddon, null)
    }

    fun enableSurvivalInventory(playerAddon: PlayerAddon, deleteItemSlot: Slot?)
    {
        val accessor = handler as ScreenHandlerAccessor
        handler.slots.clear()
        accessor.trackedSlots.clear()
        handler.scrollItems(0.0f)

        val playerSlots = playerAddon.player.playerScreenHandler.slots

        val slotRect = Rectangle(9, 18 * 3, 0, 0)
        for (i in MAIN_INVENTORY_RANGE)
        {
            val playerSlot = playerSlots[i]
            accessor.addASlot(CreativeInventoryScreen.CreativeSlot(playerSlot, playerSlot.id,
                    slotRect.x + (i % 12) * INVENTORY_SLOT_SIZE,
                    slotRect.y + (i / 12) * INVENTORY_SLOT_SIZE))
        }

        val slotRectTools = Rectangle(135, 33, 90, 18)
        for ((absoluteIndex, relativeIndex) in TOOL_BELT_RANGE.withRelativeIndex())
        {
            val playerSlot = playerSlots[absoluteIndex]
            accessor.addASlot(CreativeInventoryScreen.CreativeSlot(playerSlot, playerSlot.id,
                    slotRectTools.x + relativeIndex * INVENTORY_SLOT_SIZE,
                    slotRectTools.y))
        }

        val quickBarRect = Rectangle(9, 112, 12 * INVENTORY_SLOT_SIZE, INVENTORY_SLOT_SIZE)
        for ((absoluteIndex, relativeIndex) in QUICK_BAR_RANGE.withRelativeIndex())
        {
            val playerSlot = playerSlots[absoluteIndex]
            accessor.addASlot(CreativeInventoryScreen.CreativeSlot(playerSlot, playerSlot.id,
                    quickBarRect.x + (relativeIndex % 12) * INVENTORY_SLOT_SIZE,
                    quickBarRect.y + (relativeIndex / 12) * INVENTORY_SLOT_SIZE))
        }

        if (deleteItemSlot != null)
        {
            handler.slots.add(deleteItemSlot)
            (deleteItemSlot as SlotAccessor).x = quickBarRect.x + quickBarRect.width + 2;
        }
    }

    fun disableSurvivalInventory(playerAddon: PlayerAddon, deleteItemSlot: Slot?)
    {
        val accessor = handler as ScreenHandlerAccessor
        handler.slots.clear()
        accessor.trackedSlots.clear()
        for (i in 0 until INVENTORIO_ROW_LENGTH)
            for (j in 0 until 5)
                accessor.addASlot(CreativeInventoryScreen.LockableSlot(CreativeInventoryScreenAccessor.getCreativeInventory(),
                        i + j * INVENTORIO_ROW_LENGTH,
                        9 + i * INVENTORY_SLOT_SIZE, 18 + j * INVENTORY_SLOT_SIZE))

        for ((absolute, relative) in QUICK_BAR_RANGE.withRelativeIndex())
        {
            accessor.addASlot(QuickBarSlot(playerAddon.inventoryAddon.shortcutQuickBar, relative, playerAddon.inventoryAddon.inventory, absolute,
                    9 + INVENTORY_SLOT_SIZE * (relative / 4),
                    112 + INVENTORY_SLOT_SIZE * (relative % 4)))
        }
        if (deleteItemSlot != null)
        {
            handler.slots.remove(deleteItemSlot)
            accessor.trackedSlots.remove(deleteItemSlot.stack)
        }
    }

    override fun onSlotClick(slotIndex: Int, clickData: Int, actionType: SlotActionType, player: PlayerEntity): ItemStack?
    {
        if (slotIndex in QUICK_BAR_RANGE)
            return (handler.getSlot(slotIndex) as QuickBarSlot).onSlotClick(clickData, actionType, PlayerAddon[player])
        return null
    }

    fun scrollItems(itemList: DefaultedList<ItemStack>, position: Float)
    {
        val i = (itemList.size + INVENTORIO_ROW_LENGTH - 1) / INVENTORIO_ROW_LENGTH - 5
        val j = Math.max(0, (position * i + 0.5).toInt())
        for (k in 0..4)
        {
            for (l in 0 until INVENTORIO_ROW_LENGTH)
            {
                val m = l + (k + j) * INVENTORIO_ROW_LENGTH
                if (m >= 0 && m < itemList.size)
                    CreativeInventoryScreenAccessor.getCreativeInventory().setStack(l + k * INVENTORIO_ROW_LENGTH, itemList[m])
                else
                    CreativeInventoryScreenAccessor.getCreativeInventory().setStack(l + k * INVENTORIO_ROW_LENGTH, ItemStack.EMPTY)
            }
        }
    }
}

