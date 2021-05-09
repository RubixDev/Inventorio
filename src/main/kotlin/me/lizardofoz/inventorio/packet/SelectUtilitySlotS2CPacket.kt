package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

@Suppress("UNUSED_PARAMETER")
object SelectUtilitySlotS2CPacket
{
    val identifier = Identifier("inventorio", "set_inventory_settings_s2c")

    fun consume(context: PacketContext, buf: PacketByteBuf)
    {
        val utilitySlot = buf.readByte().toInt()
        context.taskQueue.execute {
            PlayerInventoryAddon.Client.local.selectedUtility = utilitySlot
        }
    }

    fun write(buf: PacketByteBuf, selectedUtilitySlot: Int = 0)
    {
        buf.writeByte(selectedUtilitySlot)
    }
}