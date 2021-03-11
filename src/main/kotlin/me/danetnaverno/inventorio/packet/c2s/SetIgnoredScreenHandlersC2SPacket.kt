package me.danetnaverno.inventorio.packet.c2s

import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.player.PlayerAddon
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.server.network.ServerPlayNetworkHandler

class SetIgnoredScreenHandlersC2SPacket(var ignoredScreenHandlers: List<String> = mutableListOf()) : Packet<ServerPlayPacketListener>
{
    override fun read(buf: PacketByteBuf)
    {
        val size = buf.readInt()
        val list = mutableListOf<String>()
        for (i in 0 until size)
            list.add(buf.readString())
    }

    override fun write(buf: PacketByteBuf)
    {
        buf.writeInt(ignoredScreenHandlers.count())
        for (str in ignoredScreenHandlers)
            buf.writeString(str)
    }

    override fun apply(listener: ServerPlayPacketListener)
    {
        RobertoGarbagio.LOGGER.info("Applying SetIgnoredScreenHandlersC2SPacket: $ignoredScreenHandlers")
        val player = (listener as ServerPlayNetworkHandler).player
        PlayerAddon[player].setAllIgnoredScreenHandlers(ignoredScreenHandlers)
    }
}