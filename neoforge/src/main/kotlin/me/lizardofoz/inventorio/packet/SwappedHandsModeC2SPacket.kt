package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import net.neoforged.neoforge.network.handling.PlayPayloadContext
import kotlin.jvm.optionals.getOrNull

class SwappedHandsModeC2SPacket: CustomPayload
{
    companion object {
        val identifier = Identifier("inventorio", "swapped_hands")
    }
    private var swappedHands = false

    //Sender's constructor
    constructor(swappedHands: Boolean)
    {
        this.swappedHands = swappedHands
    }

    //Receiver's constructor
    constructor(buf: PacketByteBuf)
    {
        swappedHands = buf.readBoolean()
    }

    override fun id(): Identifier = identifier

    //Sender's writer
    override fun write(buf: PacketByteBuf)
    {
        buf.writeBoolean(swappedHands)
    }

    //Receiver's consumer
    fun consume(context: PlayPayloadContext)
    {
        val sender = context.player.getOrNull() ?: return
        context.workHandler.execute {
            sender.inventoryAddon?.swappedHands = swappedHands
        }
    }
}