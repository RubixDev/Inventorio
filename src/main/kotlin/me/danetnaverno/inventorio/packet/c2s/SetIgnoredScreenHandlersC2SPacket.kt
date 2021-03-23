package me.danetnaverno.inventorio.packet.c2s

import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.player.PlayerAddon
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object SetIgnoredScreenHandlersC2SPacket
{
    val identifier = Identifier("inventorio","set_ignored_screens_c2s")

    fun consume(context: PacketContext, buf: PacketByteBuf)
    {
        val size = buf.readInt()
        val list = mutableListOf<String>()
        for (i in 0 until size)
            list.add(buf.readString(32767))

        RobertoGarbagio.LOGGER.info("Applying SetIgnoredScreenHandlersC2SPacket: $list")
        PlayerAddon[context.player].setAllIgnoredScreenHandlers(list)
    }

    fun write(buf: PacketByteBuf, ignoredScreenHandlers: List<String> = mutableListOf())
    {
        buf.writeInt(ignoredScreenHandlers.count())
        for (str in ignoredScreenHandlers)
            buf.writeString(str)
    }
}