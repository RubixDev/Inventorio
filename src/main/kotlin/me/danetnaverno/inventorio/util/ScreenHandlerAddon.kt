package me.danetnaverno.inventorio.util

import me.danetnaverno.inventorio.player.PlayerAddon
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

interface ScreenHandlerAddon
{
    fun tryInitialize(slot: Slot): Boolean

    fun initialize(playerAddon: PlayerAddon)

    fun initialize(playerAddon: PlayerAddon,
                   guiOffsetX: Int, guiOffsetY: Int,
                   slotOffsetX: Int, slotOffsetY: Int)

    fun offsetPlayerSlots(containerSlotOffsetX: Int, containerSlotOffsetY: Int, playerSlotOffsetX: Int, playerSlotOffsetY: Int)

    fun onSlotClick(slotIndex: Int, clickData: Int, actionType: SlotActionType, player: PlayerEntity): ItemStack?
}