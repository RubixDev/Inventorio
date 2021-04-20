package me.lizardofoz.inventorio.packet.c2s

import me.lizardofoz.inventorio.RobertoGarbagio
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.util.INVENTORIO_ROW_LENGTH
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList

object SetNewQuickBarC2SPacket
{
    val identifier = Identifier("inventorio", "set_new_quickbar_c2s")

    fun consume(context: PacketContext, buf: PacketByteBuf)
    {
        val items = DefaultedList.ofSize(INVENTORIO_ROW_LENGTH, ItemStack.EMPTY)

        val len = buf.readByte()
        for (i in 0 until len)
            items[i] = ItemStack.fromTag(buf.readCompoundTag())

        RobertoGarbagio.LOGGER.info("Applying SetNewQuickBarC2SPacket: $items")
        PlayerAddon[context.player].setQuickBar(items)
    }

    fun write(buf: PacketByteBuf, items: MutableList<ItemStack>)
    {
        buf.writeByte(items.size)
        for (item in items)
            buf.writeCompoundTag(item.toTag(CompoundTag()))
    }
}