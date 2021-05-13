package me.lizardofoz.inventorio.packet

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.server.network.ServerPlayerEntity

interface InventorioNetworking
{
    @Environment(EnvType.CLIENT)
    fun c2sSendSelectedUtilitySlot(selectedUtility: Int)

    @Environment(EnvType.CLIENT)
    fun c2sUseBoostRocket()

    fun s2cSendSelectedUtilitySlot(player: ServerPlayerEntity)

    companion object
    {
        lateinit var INSTANCE: InventorioNetworking
    }
}