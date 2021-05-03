package me.lizardofoz.inventorio.packet.c2s

import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.util.UtilityBeltMode
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object SetUtilityBeltModeC2SPacket
{
    val identifier = Identifier("inventorio", "set_utility_belt_mode_c2s")

    fun consume(context: PacketContext, buf: PacketByteBuf)
    {
        val utilityBeltMode = UtilityBeltMode.values()[buf.readByte().toInt()]
        PlayerAddon[context.player].setUtilityBeltMode(utilityBeltMode)
    }

    fun write(buf: PacketByteBuf, utilityBeltMode: UtilityBeltMode = UtilityBeltMode.FILTERED)
    {
        buf.writeByte(utilityBeltMode.ordinal)
    }
}