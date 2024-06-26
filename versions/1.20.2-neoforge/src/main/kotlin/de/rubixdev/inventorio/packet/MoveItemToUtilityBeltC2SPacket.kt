package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.InventorioScreenHandler.Companion.inventorioScreenHandler
import net.minecraft.network.PacketByteBuf
import net.neoforged.neoforge.network.NetworkEvent

class MoveItemToUtilityBeltC2SPacket {
    private var sourceSlot = 0

    // Sender's constructor
    constructor(sourceSlot: Int) {
        this.sourceSlot = sourceSlot
    }

    // Receiver's constructor
    constructor(buf: PacketByteBuf) {
        sourceSlot = buf.readByte().toInt()
    }

    // Sender's writer
    fun write(buf: PacketByteBuf) {
        buf.writeByte(sourceSlot)
    }

    // Receiver's consumer
    fun consume(context: NetworkEvent.Context) {
        val player = context.sender ?: return
        context.enqueueWork {
            val screenHandler = player.inventorioScreenHandler ?: return@enqueueWork
            screenHandler.tryTransferToUtilityBeltSlot(screenHandler.getSlot(sourceSlot))
        }
        context.packetHandled = true
    }
}
