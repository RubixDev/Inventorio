package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import java.util.function.Supplier
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.network.NetworkEvent

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
    fun consume(supplier: Supplier<NetworkEvent.Context>) {
        val sender = supplier.get().sender ?: return
        supplier.get().enqueueWork {
            sender.inventoryAddon?.swappedHands = swappedHands
        }
        supplier.get().packetHandled = true
    }
}
