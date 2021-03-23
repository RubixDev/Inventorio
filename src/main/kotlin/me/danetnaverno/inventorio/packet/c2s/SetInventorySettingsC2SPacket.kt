package me.danetnaverno.inventorio.packet.c2s

import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.util.QuickBarMode
import me.danetnaverno.inventorio.util.UtilityBeltMode
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object SetInventorySettingsC2SPacket
{
    val identifier = Identifier("inventorio", "set_inventory_settings_c2s")

    fun consume(context: PacketContext, buf: PacketByteBuf)
    {
        val quickBarMode = QuickBarMode.values()[buf.readByte().toInt()]
        val utilityBeltMode = UtilityBeltMode.values()[buf.readByte().toInt()]

        val player = context.player
        PlayerAddon[player].trySetRestrictionModesC2S(quickBarMode, utilityBeltMode)
        RobertoGarbagio.LOGGER.info("Applying SetInventorySettingsC2SPacket: $quickBarMode $utilityBeltMode")
    }

    fun write(buf: PacketByteBuf, quickBarMode: QuickBarMode = QuickBarMode.DEFAULT, utilityBeltMode: UtilityBeltMode = UtilityBeltMode.FILTERED)
    {
        buf.writeByte(quickBarMode.ordinal)
        buf.writeByte(utilityBeltMode.ordinal)
    }
}