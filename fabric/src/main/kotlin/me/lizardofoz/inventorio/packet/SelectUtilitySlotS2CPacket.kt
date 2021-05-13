package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

@Suppress("UNUSED_PARAMETER")
object SelectUtilitySlotS2CPacket
{
    val identifier = Identifier("inventorio", "set_inventory_settings_s2c")

    fun consume(client: MinecraftClient, handler: ClientPlayNetworkHandler, buf: PacketByteBuf, responseSender: PacketSender)
    {
        val utilitySlot = buf.readByte().toInt()
        client.execute {
            PlayerInventoryAddon.Client.local.selectedUtility = utilitySlot
        }
    }

    fun write(buf: PacketByteBuf, selectedUtilitySlot: Int = 0)
    {
        buf.writeByte(selectedUtilitySlot)
    }
}