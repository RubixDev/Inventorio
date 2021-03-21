package me.danetnaverno.inventorio.packet

import me.danetnaverno.inventorio.client.config.InventorioConfigData
import me.danetnaverno.inventorio.packet.c2s.*
import me.danetnaverno.inventorio.packet.s2c.SetInventorySettingsS2CPacket
import me.danetnaverno.inventorio.player.PlayerAddon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.client.options.HotbarStorageEntry
import net.minecraft.entity.player.PlayerEntity

object InventorioNetworking
{
    @Environment(EnvType.CLIENT)
    fun C2SSendSelectedUtilitySlot(selectedUtility: Int)
    {
        ClientSidePacketRegistry.INSTANCE.sendToServer(SelectUtilitySlotC2SPacket(selectedUtility))
    }

    @Environment(EnvType.CLIENT)
    fun C2SSendPlayerSettings(playerAddon: PlayerAddon = PlayerAddon.Client.local)
    {
        ClientSidePacketRegistry.INSTANCE.sendToServer(SetInventorySettingsC2SPacket(playerAddon.quickBarMode, playerAddon.utilityBeltMode))
    }

    @Environment(EnvType.CLIENT)
    fun C2SSendPlayerSettingsFromDefault()
    {
        val packet = SetInventorySettingsC2SPacket(InventorioConfigData.config().quickBarModeDefault, InventorioConfigData.config().utilityBeltModeDefault)
        ClientSidePacketRegistry.INSTANCE.sendToServer(packet)
    }

    @Environment(EnvType.CLIENT)
    fun C2SFireRocket()
    {
        ClientSidePacketRegistry.INSTANCE.sendToServer(FireBoostRocketC2SPacket())
    }

    @Environment(EnvType.CLIENT)
    fun C2SSendIgnoredScreenHandlers()
    {
        ClientSidePacketRegistry.INSTANCE.sendToServer(SetIgnoredScreenHandlersC2SPacket(InventorioConfigData.config().ignoredScreensGlobal))
    }

    @Environment(EnvType.CLIENT)
    fun C2SSendNewQuickBar(newItems: HotbarStorageEntry)
    {
        ClientSidePacketRegistry.INSTANCE.sendToServer(SetNewQuickBarC2SPacket(newItems))
    }

    fun S2CSendPlayerSettings(player: PlayerEntity)
    {
        val addon = PlayerAddon[player]
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, SetInventorySettingsS2CPacket(addon.quickBarMode, addon.utilityBeltMode, addon.inventoryAddon.selectedUtility))
    }
}