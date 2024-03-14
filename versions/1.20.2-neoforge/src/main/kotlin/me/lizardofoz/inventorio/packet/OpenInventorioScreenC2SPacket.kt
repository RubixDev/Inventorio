package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.InventorioScreenHandler
import net.neoforged.neoforge.network.NetworkEvent

class OpenInventorioScreenC2SPacket {
    fun consume(context: NetworkEvent.Context) {
        val sender = context.sender ?: return
        context.enqueueWork {
            InventorioScreenHandler.open(sender)
        }
        context.packetHandled = true
    }
}
