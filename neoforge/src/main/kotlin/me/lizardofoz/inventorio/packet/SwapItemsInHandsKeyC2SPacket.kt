package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import net.neoforged.neoforge.network.handling.PlayPayloadContext
import kotlin.jvm.optionals.getOrNull

class SwapItemsInHandsKeyC2SPacket: CustomPayload
{
    companion object {
        val identifier = Identifier("inventorio", "swap_items_in_hands")
    }

    override fun id(): Identifier = identifier

    override fun write(buf: PacketByteBuf?) {}

    //Receiver's consumer
    fun consume(context: PlayPayloadContext)
    {
        val sender = context.player.getOrNull() ?: return
        context.workHandler.execute {
            sender.inventoryAddon?.swapItemsInHands()
        }
    }
}