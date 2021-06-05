package me.lizardofoz.inventorio.packet

import io.netty.buffer.PooledByteBufAllocator
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object InventorioNetworkingFabric : InventorioNetworking
{
    init
    {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
            ClientPlayNetworking.registerGlobalReceiver(SelectUtilitySlotPacket.identifier, SelectUtilitySlotPacket::consume)

        ServerPlayNetworking.registerGlobalReceiver(UseBoostRocketC2SPacket.identifier, UseBoostRocketC2SPacket::consume)
        ServerPlayNetworking.registerGlobalReceiver(SelectUtilitySlotPacket.identifier, SelectUtilitySlotPacket::consume)
        ServerPlayNetworking.registerGlobalReceiver(SwappedHandsC2SPacket.identifier, SwappedHandsC2SPacket::consume)
        ServerPlayNetworking.registerGlobalReceiver(SendItemToUtilityBeltC2SPacket.identifier, SendItemToUtilityBeltC2SPacket::consume)
    }

    override fun s2cSendSelectedUtilitySlot(player: ServerPlayerEntity)
    {
        val inventoryAddon = player.inventoryAddon ?: return
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        SelectUtilitySlotPacket.write(buf, inventoryAddon.selectedUtility)
        ServerPlayNetworking.send(player, SelectUtilitySlotPacket.identifier, buf)
    }

    @Environment(EnvType.CLIENT)
    override fun c2sSendSelectedUtilitySlot(selectedUtility: Int)
    {
        sendC2S(SelectUtilitySlotPacket.identifier) {
            SelectUtilitySlotPacket.write(it, selectedUtility)
        }
    }

    @Environment(EnvType.CLIENT)
    override fun c2sUseBoostRocket()
    {
        sendC2S(UseBoostRocketC2SPacket.identifier) { }
    }

    @Environment(EnvType.CLIENT)
    override fun c2sSetSwappedHands(swappedHands: Boolean)
    {
        if (MinecraftClient.getInstance().networkHandler != null)
            sendC2S(SwappedHandsC2SPacket.identifier) {
                SwappedHandsC2SPacket.write(it, swappedHands)
            }
    }

    @Environment(EnvType.CLIENT)
    override fun c2sSendItemToUtilityBelt(sourceSlot: Int)
    {
        sendC2S(SendItemToUtilityBeltC2SPacket.identifier) {
            SendItemToUtilityBeltC2SPacket.write(it, sourceSlot)
        }
    }

    @Environment(EnvType.CLIENT)
    private fun sendC2S(id: Identifier, func: (PacketByteBuf) -> Unit)
    {
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        func(buf)
        ClientPlayNetworking.send(id, buf)
    }
}