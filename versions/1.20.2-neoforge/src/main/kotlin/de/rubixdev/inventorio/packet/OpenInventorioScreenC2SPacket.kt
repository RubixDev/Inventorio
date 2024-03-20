package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.AbstractInventorioScreenHandler
import net.neoforged.neoforge.network.NetworkEvent

class OpenInventorioScreenC2SPacket {
    fun consume(context: NetworkEvent.Context) {
        val sender = context.sender ?: return
        context.enqueueWork {
            AbstractInventorioScreenHandler.open(sender)
        }
        context.packetHandled = true
    }
}
