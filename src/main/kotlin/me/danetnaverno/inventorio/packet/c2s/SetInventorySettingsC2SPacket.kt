package me.danetnaverno.inventorio.packet.c2s

import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.util.QuickBarMode
import me.danetnaverno.inventorio.util.UtilityBeltMode
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.server.network.ServerPlayNetworkHandler

class SetInventorySettingsC2SPacket(
        var quickBarMode: QuickBarMode = QuickBarMode.DEFAULT,
        var utilityBeltMode: UtilityBeltMode = UtilityBeltMode.FILTERED
) : Packet<ServerPlayPacketListener>
{
    override fun read(buf: PacketByteBuf)
    {
        quickBarMode = QuickBarMode.values()[buf.readByte().toInt()]
        utilityBeltMode = UtilityBeltMode.values()[buf.readByte().toInt()]
    }

    override fun write(buf: PacketByteBuf)
    {
        buf.writeByte(quickBarMode.ordinal)
        buf.writeByte(utilityBeltMode.ordinal)
    }

    override fun apply(listener: ServerPlayPacketListener)
    {
        val player = (listener as ServerPlayNetworkHandler).player
        PlayerAddon[player].trySetRestrictionModesC2S(quickBarMode, utilityBeltMode)
        RobertoGarbagio.LOGGER.info("Applying SetInventorySettingsC2SPacket: $quickBarMode $utilityBeltMode")
    }
}