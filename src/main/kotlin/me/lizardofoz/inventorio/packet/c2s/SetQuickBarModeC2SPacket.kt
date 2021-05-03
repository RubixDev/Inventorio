package me.lizardofoz.inventorio.packet.c2s

import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.util.QuickBarMode
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object SetQuickBarModeC2SPacket
{
    val identifier = Identifier("inventorio", "set_quick_bar_mode_c2s")

    fun consume(context: PacketContext, buf: PacketByteBuf)
    {
        val quickBarMode = QuickBarMode.values()[buf.readByte().toInt()]
        PlayerAddon[context.player].setQuickBarMode(quickBarMode)
    }

    fun write(buf: PacketByteBuf, quickBarMode: QuickBarMode = QuickBarMode.FILTERED)
    {
        buf.writeByte(quickBarMode.ordinal)
    }
}