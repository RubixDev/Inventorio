package me.lizardofoz.inventorio.packet

import io.netty.buffer.PooledByteBufAllocator
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object InventorioNetworkingFabric : InventorioNetworking
{
    init
    {
        ServerPlayNetworking.registerGlobalReceiver(UseBoostRocketC2SPacket.identifier, UseBoostRocketC2SPacket::consume)
        ServerPlayNetworking.registerGlobalReceiver(SelectUtilitySlotC2SPacket.identifier, SelectUtilitySlotC2SPacket::consume)

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
            ClientPlayNetworking.registerGlobalReceiver(SelectUtilitySlotS2CPacket.identifier, SelectUtilitySlotS2CPacket::consume)
    }

    @Environment(EnvType.CLIENT)
    override fun c2sSendSelectedUtilitySlot(selectedUtility: Int)
    {
        sendC2S(SelectUtilitySlotC2SPacket.identifier) {
            SelectUtilitySlotC2SPacket.write(it, selectedUtility)
        }
    }

    @Environment(EnvType.CLIENT)
    override fun c2sUseBoostRocket()
    {
        sendC2S(UseBoostRocketC2SPacket.identifier) { }
    }

    override fun s2cSendSelectedUtilitySlot(player: ServerPlayerEntity)
    {
        val inventoryAddon = player.inventoryAddon ?: return
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        SelectUtilitySlotS2CPacket.write(buf, inventoryAddon.selectedUtility)
        ServerPlayNetworking.send(player, SelectUtilitySlotS2CPacket.identifier, buf)
    }

    @Environment(EnvType.CLIENT)
    private fun sendC2S(id: Identifier, func: (PacketByteBuf) -> Unit)
    {
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        func(buf)
        ClientPlayNetworking.send(id, buf)
    }
}