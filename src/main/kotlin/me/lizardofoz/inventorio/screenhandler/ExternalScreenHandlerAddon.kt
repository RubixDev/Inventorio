package me.lizardofoz.inventorio.screenhandler

import me.lizardofoz.inventorio.mixin.accessor.ScreenHandlerAccessor
import me.lizardofoz.inventorio.mixin.accessor.SlotAccessor
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.quickbar.QuickBarHandlerWidget
import me.lizardofoz.inventorio.util.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class ExternalScreenHandlerAddon internal constructor(val handler: ScreenHandler) : ScreenHandlerAddon
{
    private var quickBarHandlerWidget: QuickBarHandlerWidget? = null
    private var initStage = 0

    override fun tryInitialize(slot: Slot): Boolean
    {
        //The problem is that we want to inject into every single ScreenHandler, that has a slot assigned to the player's inventory.
        //But a general ScreenHandler has no way to trace a player. This is a workaround.
        if (slot.inventory is PlayerInventory)
        {
            if (initStage == 0)
            {
                initStage = 1
                val playerAddon = PlayerAddon[(slot.inventory as PlayerInventory).player]
                if (!playerAddon.isScreenHandlerIgnored(handler))
                {
                    initialize(playerAddon)
                    initStage = 2
                }
            }
            return initStage == 1
        }
        return true
    }

    override fun initialize(playerAddon: PlayerAddon)
    {
        val nonPlayerItems = handler.slots.filter { playerAddon.inventoryAddon.inventory != it.inventory }
        val offsetPoint = ScreenHandlerOffsets.getScreenHandlerOffset(handler)
        val playerStartX = offsetPoint.x
        val playerStartY = (nonPlayerItems.maxOfOrNull { it.y } ?: 0) +
                INVENTORY_SLOT_SIZE + 17 + 4 + offsetPoint.y
        val slotOffsetX = 0
        val slotOffsetY = 0
        initialize(playerAddon,
                playerStartX, playerStartY,
                slotOffsetX, slotOffsetY)
    }

    fun initialize(playerAddon: PlayerAddon, guiOffsetX: Int, guiOffsetY: Int, slotOffsetX: Int, slotOffsetY: Int)
    {
        val playerInventory = playerAddon.inventoryAddon.inventory
        val accessor = handler as ScreenHandlerAccessor
        quickBarHandlerWidget = QuickBarHandlerWidget(playerAddon.inventoryAddon)

        val gui_container_extension_slot_startX = 8
        val expansionSize = playerAddon.getAvailableExtensionSlotsRange()
        val mainStartY = playerAddon.getExtraRows() * INVENTORY_SLOT_SIZE

        val offset = if (expansionSize.isEmpty()) 0 else 4

        //Main Inventory
        for (i in MAIN_INVENTORY_RANGE)
        {
            val x = i % INVENTORIO_ROW_LENGTH
            val y = i / INVENTORIO_ROW_LENGTH
            accessor.addASlot(Slot(playerInventory, i,
                    guiOffsetX + gui_container_extension_slot_startX + x * INVENTORY_SLOT_SIZE,
                    guiOffsetY + offset + mainStartY + y * INVENTORY_SLOT_SIZE))
        }

        //Expansion
        for (slot in expansionSize.withRelativeIndex())
        {
            val x = slot.relativeIndex % INVENTORIO_ROW_LENGTH
            val y = slot.relativeIndex / INVENTORIO_ROW_LENGTH
            accessor.addASlot(Slot(playerInventory, slot.absoluteIndex,
                    guiOffsetX + gui_container_extension_slot_startX + x * INVENTORY_SLOT_SIZE,
                    guiOffsetY + y * INVENTORY_SLOT_SIZE))
        }

        val normalStart = offset + mainStartY + INVENTORY_SLOT_SIZE * 3 + 4
        quickBarHandlerWidget?.createQuickBarSlots(handler, guiOffsetX + 8, guiOffsetY + normalStart, QUICK_BAR_PHYS_RANGE)
    }

    fun offsetPlayerSlots(containerSlotOffsetX: Int, containerSlotOffsetY: Int, playerSlotOffsetX: Int, playerSlotOffsetY: Int)
    {
        for (slot in handler.slots)
        {
            val accessor = slot as SlotAccessor
            if (slot.isPlayerSlot)
            {
                accessor.x += containerSlotOffsetX
                accessor.y += containerSlotOffsetY
            }
            else
            {
                accessor.x += playerSlotOffsetX
                accessor.y += playerSlotOffsetY
            }
        }
    }

    override fun onSlotClick(slotIndex: Int, clickData: Int, actionType: SlotActionType, player: PlayerEntity): ItemStack?
    {
        return quickBarHandlerWidget?.onSlotClick(handler, slotIndex, clickData, actionType, player)
    }
}

