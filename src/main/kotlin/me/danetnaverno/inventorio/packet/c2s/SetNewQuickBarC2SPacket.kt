package me.danetnaverno.inventorio.packet.c2s

import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.player.PlayerAddon
import net.minecraft.client.options.HotbarStorageEntry
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.server.network.ServerPlayNetworkHandler

class SetNewQuickBarC2SPacket(var items: HotbarStorageEntry = HotbarStorageEntry()) : Packet<ServerPlayPacketListener>
{
    override fun read(buf: PacketByteBuf)
    {
        val len = buf.readByte()
        for(i in 0 until len)
            items[i] = ItemStack.fromTag(buf.readCompoundTag())
        RobertoGarbagio.LOGGER.info("Reading SetNewQuickBarC2SPacket: $items")
    }

    override fun write(buf: PacketByteBuf)
    {
        buf.writeByte(items.size)
        for (item in items)
            buf.writeCompoundTag(item.toTag(CompoundTag()))
        RobertoGarbagio.LOGGER.info("Writing SetNewQuickBarC2SPacket: $items")
    }

    override fun apply(listener: ServerPlayPacketListener)
    {
        RobertoGarbagio.LOGGER.info("Applying SetNewQuickBarC2SPacket: $items")
        val player = (listener as ServerPlayNetworkHandler).player
        PlayerAddon[player].setQuickBar(items)
    }
}