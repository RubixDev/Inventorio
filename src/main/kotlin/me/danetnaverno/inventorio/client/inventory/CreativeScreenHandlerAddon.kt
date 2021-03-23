package me.danetnaverno.inventorio.client.inventory

import me.danetnaverno.inventorio.mixin.ScreenHandlerAccessor
import me.danetnaverno.inventorio.mixin.client.CreativeInventoryScreenAccessor
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.quickbar.QuickBarHandlerWidget
import me.danetnaverno.inventorio.util.*
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
    private lateinit var quickBarHandlerWidget: QuickBarHandlerWidget

    override fun tryInitialize(slot: Slot): Boolean
    {
        return true
    }

    override fun initialize(playerAddon: PlayerAddon)
    {
        disableSurvivalInventory(playerAddon);
    }

    fun enableSurvivalInventory(playerAddon: PlayerAddon)
    {
        val accessor = handler as ScreenHandlerAccessor
        handler.slots.clear()
        accessor.trackedSlots.clear()
        handler.scrollItems(0.0f)

        val playerSlots = playerAddon.player.playerScreenHandler.slots

        val slotRect = Rectangle(9, 18*3, 0, 0)
        for (i in MAIN_INVENTORY_RANGE)
        {
            val playerSlot = playerSlots[i]
            accessor.addASlot(CreativeInventoryScreen.CreativeSlot(playerSlot, playerSlot.id,
                    slotRect.x + (i % 12) * INVENTORY_SLOT_SIZE,
                    slotRect.y + (i / 12) * INVENTORY_SLOT_SIZE))
        }

        val quickBarRect = Rectangle(9, 112, 0, 0)
        for ((absoluteIndex, relativeIndex) in QUICK_BAR_PHYS_RANGE.withRelativeIndex())
        {
            val playerSlot = playerSlots[absoluteIndex]
            accessor.addASlot(CreativeInventoryScreen.CreativeSlot(playerSlot, playerSlot.id,
                    quickBarRect.x + (relativeIndex % 12) * INVENTORY_SLOT_SIZE,
                    quickBarRect.y + (relativeIndex / 12) * INVENTORY_SLOT_SIZE))
        }
        for ((absoluteIndex, relativeIndex) in QUICK_BAR_SHORTCUTS_RANGE.withRelativeIndex())
        {
            val playerSlot = playerSlots[absoluteIndex]
            accessor.addASlot(CreativeInventoryScreen.CreativeSlot(playerSlot, playerSlot.id,
                    quickBarRect.x + (relativeIndex % 12) * INVENTORY_SLOT_SIZE,
                    quickBarRect.y + (relativeIndex / 12) * INVENTORY_SLOT_SIZE))
        }

    }

    fun disableSurvivalInventory(playerAddon: PlayerAddon)
    {
        val accessor = handler as ScreenHandlerAccessor
        handler.slots.clear()
        accessor.trackedSlots.clear()
        for (i in 0 until INVENTORIO_ROW_LENGTH)
            for (j in 0 until 5)
                accessor.addASlot(CreativeInventoryScreen.LockableSlot(CreativeInventoryScreenAccessor.getCreativeInventory(),
                        i + j * INVENTORIO_ROW_LENGTH,
                        9 + i * INVENTORY_SLOT_SIZE, 18 + j * INVENTORY_SLOT_SIZE))

        quickBarHandlerWidget = QuickBarHandlerWidget(playerAddon.inventoryAddon)
        quickBarHandlerWidget.createQuickBarSlots(handler, 9, 112, QUICK_BAR_PHYS_RANGE)
    }


    override fun onSlotClick(slotIndex: Int, clickData: Int, actionType: SlotActionType, player: PlayerEntity): ItemStack?
    {
        val result = quickBarHandlerWidget.onSlotClick(handler, slotIndex, clickData, actionType, player)
        if (result != null)
            player.inventory.cursorStack = ItemStack.EMPTY
        return result
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

