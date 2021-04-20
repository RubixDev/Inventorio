package me.lizardofoz.inventorio.packet

import io.netty.buffer.PooledByteBufAllocator
import me.lizardofoz.inventorio.client.config.InventorioConfigData
import me.lizardofoz.inventorio.packet.c2s.*
import me.lizardofoz.inventorio.packet.s2c.SetInventorySettingsS2CPacket
import me.lizardofoz.inventorio.player.PlayerAddon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object InventorioNetworking
{
    init
    {
        ServerSidePacketRegistry.INSTANCE.register(FireBoostRocketC2SPacket.identifier, FireBoostRocketC2SPacket::consume)
        ServerSidePacketRegistry.INSTANCE.register(SelectUtilitySlotC2SPacket.identifier, SelectUtilitySlotC2SPacket::consume)
        ServerSidePacketRegistry.INSTANCE.register(SetIgnoredScreenHandlersC2SPacket.identifier, SetIgnoredScreenHandlersC2SPacket::consume)
        ServerSidePacketRegistry.INSTANCE.register(SetInventorySettingsC2SPacket.identifier, SetInventorySettingsC2SPacket::consume)
        ServerSidePacketRegistry.INSTANCE.register(SetNewQuickBarC2SPacket.identifier, SetNewQuickBarC2SPacket::consume)

        ServerSidePacketRegistry.INSTANCE.register(SetInventorySettingsS2CPacket.identifier, SetInventorySettingsS2CPacket::consume)
    }

    @Environment(EnvType.CLIENT)
    fun C2SSendSelectedUtilitySlot(selectedUtility: Int)
    {
        sendC2S(SelectUtilitySlotC2SPacket.identifier) {
            SelectUtilitySlotC2SPacket.write(it, selectedUtility)
        }
    }

    @Environment(EnvType.CLIENT)
    fun C2SSendPlayerSettings(playerAddon: PlayerAddon = PlayerAddon.Client.local)
    {
        sendC2S(SetInventorySettingsC2SPacket.identifier) {
            SetInventorySettingsC2SPacket.write(it, playerAddon.quickBarMode, playerAddon.utilityBeltMode)
        }
    }

    @Environment(EnvType.CLIENT)
    fun C2SSendPlayerSettingsFromDefault()
    {
        sendC2S(SetInventorySettingsC2SPacket.identifier) {
            SetInventorySettingsC2SPacket.write(it, InventorioConfigData.config().quickBarModeDefault, InventorioConfigData.config().utilityBeltModeDefault)
        }
    }

    @Environment(EnvType.CLIENT)
    fun C2SFireRocket()
    {
        sendC2S(FireBoostRocketC2SPacket.identifier) { }
    }

    @Environment(EnvType.CLIENT)
    fun C2SSendIgnoredScreenHandlers()
    {
        sendC2S(SetIgnoredScreenHandlersC2SPacket.identifier) {
            SetIgnoredScreenHandlersC2SPacket.write(it, InventorioConfigData.config().ignoredScreens)
        }
    }

    @Environment(EnvType.CLIENT)
    fun C2SSendNewQuickBar(newItems: MutableList<ItemStack>)
    {
        sendC2S(SetNewQuickBarC2SPacket.identifier) {
            SetNewQuickBarC2SPacket.write(it, newItems)
        }
    }

    fun S2CSendPlayerSettings(player: PlayerEntity)
    {
        val addon = PlayerAddon[player]
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        SetInventorySettingsS2CPacket.write(buf, addon.quickBarMode, addon.utilityBeltMode, addon.inventoryAddon.selectedUtility)
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, SetInventorySettingsS2CPacket.identifier, buf)
    }

    private fun sendC2S(id: Identifier, func: (PacketByteBuf) -> Unit)
    {
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        func(buf)
        ClientSidePacketRegistry.INSTANCE.sendToServer(id, buf)
    }
}