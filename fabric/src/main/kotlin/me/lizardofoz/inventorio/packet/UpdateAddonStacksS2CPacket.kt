package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

@Suppress("UNUSED_PARAMETER")
object UpdateAddonStacksS2CPacket
{
    val identifier = Identifier("inventorio", "update_addon_stacks")

    fun consume(client: MinecraftClient, handler: ClientPlayNetworkHandler, buf: PacketByteBuf, responseSender: PacketSender)
    {
        val size = buf.readInt()
        val updatedStacks = mutableMapOf<Int, ItemStack>()
        for(i in 0 until size)
            updatedStacks[buf.readInt()] = buf.readItemStack()
        client.execute {
            PlayerInventoryAddon.Client.local?.receiveStacksUpdateS2C(updatedStacks)
        }
    }

    fun write(buf: PacketByteBuf, updatedStacks: Map<Int, ItemStack>)
    {
        buf.writeInt(updatedStacks.size)
        for((index, stack) in updatedStacks)
        {
            buf.writeInt(index)
            buf.writeItemStack(stack)
        }
    }
}