package me.danetnaverno.inventorio.packet.c2s

import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.player.PlayerAddon
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.server.network.ServerPlayNetworkHandler

class FireBoostRocketC2SPacket : Packet<ServerPlayPacketListener>
{
    override fun read(buf: PacketByteBuf)
    {
    }

    override fun write(buf: PacketByteBuf)
    {
    }

    override fun apply(listener: ServerPlayPacketListener)
    {
        PlayerAddon[(listener as ServerPlayNetworkHandler).player].fireRocket()
    }
}