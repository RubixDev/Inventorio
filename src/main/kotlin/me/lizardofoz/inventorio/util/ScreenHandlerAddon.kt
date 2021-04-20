package me.lizardofoz.inventorio.util

import me.lizardofoz.inventorio.player.PlayerAddon
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

interface ScreenHandlerAddon
{
    fun tryInitialize(slot: Slot): Boolean

    fun initialize(playerAddon: PlayerAddon)

    fun onSlotClick(slotIndex: Int, clickData: Int, actionType: SlotActionType, player: PlayerEntity): ItemStack?
}