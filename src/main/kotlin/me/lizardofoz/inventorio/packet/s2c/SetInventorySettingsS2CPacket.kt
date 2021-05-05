package me.lizardofoz.inventorio.packet.s2c

import me.lizardofoz.inventorio.RobertoGarbagio
import me.lizardofoz.inventorio.player.PlayerAddon
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object SetInventorySettingsS2CPacket
{
    val identifier = Identifier("inventorio", "set_inventory_settings_s2c")

    fun consume(context: PacketContext, buf: PacketByteBuf)
    {
        val selectedUtilitySlot = buf.readByte().toInt()

        PlayerAddon.Client.local.inventoryAddon.selectedUtility = selectedUtilitySlot
        RobertoGarbagio.LOGGER.info("Applying SetInventorySettingsS2CPacket: $selectedUtilitySlot")
    }

    fun write(buf: PacketByteBuf, selectedUtilitySlot: Int = 0)
    {
        buf.writeByte(selectedUtilitySlot)
    }
}