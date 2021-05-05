package me.lizardofoz.inventorio.packet

import io.netty.buffer.PooledByteBufAllocator
import me.lizardofoz.inventorio.packet.c2s.FireBoostRocketC2SPacket
import me.lizardofoz.inventorio.packet.c2s.SelectUtilitySlotC2SPacket
import me.lizardofoz.inventorio.packet.s2c.SetInventorySettingsS2CPacket
import me.lizardofoz.inventorio.player.PlayerAddon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object InventorioNetworking
{
    init
    {
        ServerSidePacketRegistry.INSTANCE.register(FireBoostRocketC2SPacket.identifier, FireBoostRocketC2SPacket::consume)
        ServerSidePacketRegistry.INSTANCE.register(SelectUtilitySlotC2SPacket.identifier, SelectUtilitySlotC2SPacket::consume)

        ClientSidePacketRegistry.INSTANCE.register(SetInventorySettingsS2CPacket.identifier, SetInventorySettingsS2CPacket::consume)
    }

    @Environment(EnvType.CLIENT)
    fun C2SSendSelectedUtilitySlot(selectedUtility: Int)
    {
        sendC2S(SelectUtilitySlotC2SPacket.identifier) {
            SelectUtilitySlotC2SPacket.write(it, selectedUtility)
        }
    }

    @Environment(EnvType.CLIENT)
    fun C2SFireRocket()
    {
        sendC2S(FireBoostRocketC2SPacket.identifier) { }
    }

    fun S2CSendPlayerSettings(player: PlayerEntity)
    {
        val addon = PlayerAddon[player]
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        SetInventorySettingsS2CPacket.write(buf, addon.inventoryAddon.selectedUtility)
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, SetInventorySettingsS2CPacket.identifier, buf)
    }

    private fun sendC2S(id: Identifier, func: (PacketByteBuf) -> Unit)
    {
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        func(buf)
        ClientSidePacketRegistry.INSTANCE.sendToServer(id, buf)
    }
}