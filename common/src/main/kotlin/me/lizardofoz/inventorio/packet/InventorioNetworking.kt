package me.lizardofoz.inventorio.packet

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.server.network.ServerPlayerEntity

interface InventorioNetworking
{
    fun s2cSendSelectedUtilitySlot(player: ServerPlayerEntity)

    @Environment(EnvType.CLIENT)
    fun c2sSendSelectedUtilitySlot(selectedUtility: Int)

    @Environment(EnvType.CLIENT)
    fun c2sUseBoostRocket()

    @Environment(EnvType.CLIENT)
    fun c2sSetSwappedHands(swappedHands: Boolean)

    @Environment(EnvType.CLIENT)
    fun c2sSendItemToUtilityBelt(sourceSlot: Int)

    companion object
    {
        lateinit var INSTANCE: InventorioNetworking
    }
}