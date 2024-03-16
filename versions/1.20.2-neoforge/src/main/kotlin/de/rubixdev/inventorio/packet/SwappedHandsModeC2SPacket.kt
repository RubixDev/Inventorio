package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.network.PacketByteBuf
import net.neoforged.neoforge.network.NetworkEvent

class SwappedHandsModeC2SPacket {
    private var swappedHands = false

    // Sender's constructor
    constructor(swappedHands: Boolean) {
        this.swappedHands = swappedHands
    }

    // Receiver's constructor
    constructor(buf: PacketByteBuf) {
        swappedHands = buf.readBoolean()
    }

    // Sender's writer
    fun write(buf: PacketByteBuf) {
        buf.writeBoolean(swappedHands)
    }

    // Receiver's consumer
    fun consume(context: NetworkEvent.Context) {
        val sender = context.sender ?: return
        context.enqueueWork {
            sender.inventoryAddon?.swappedHands = swappedHands
        }
        context.packetHandled = true
    }
}
