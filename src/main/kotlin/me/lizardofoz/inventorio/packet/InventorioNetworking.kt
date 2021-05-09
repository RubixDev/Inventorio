package me.lizardofoz.inventorio.packet

import io.netty.buffer.PooledByteBufAllocator
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
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
        ServerSidePacketRegistry.INSTANCE.register(UseBoostRocketC2SPacket.identifier, UseBoostRocketC2SPacket::consume)
        ServerSidePacketRegistry.INSTANCE.register(SelectUtilitySlotC2SPacket.identifier, SelectUtilitySlotC2SPacket::consume)
        ClientSidePacketRegistry.INSTANCE.register(SelectUtilitySlotS2CPacket.identifier, SelectUtilitySlotS2CPacket::consume)
    }

    @Environment(EnvType.CLIENT)
    fun c2sSendSelectedUtilitySlot(selectedUtility: Int)
    {
        sendC2S(SelectUtilitySlotC2SPacket.identifier) {
            SelectUtilitySlotC2SPacket.write(it, selectedUtility)
        }
    }

    @Environment(EnvType.CLIENT)
    fun c2sUseBoostRocket()
    {
        sendC2S(UseBoostRocketC2SPacket.identifier) { }
    }

    fun s2cSendSelectedUtilitySlot(player: PlayerEntity)
    {
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        SelectUtilitySlotS2CPacket.write(buf, player.inventoryAddon.selectedUtility)
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, SelectUtilitySlotS2CPacket.identifier, buf)
    }

    @Environment(EnvType.CLIENT)
    private fun sendC2S(id: Identifier, func: (PacketByteBuf) -> Unit)
    {
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        func(buf)
        ClientSidePacketRegistry.INSTANCE.sendToServer(id, buf)
    }
}