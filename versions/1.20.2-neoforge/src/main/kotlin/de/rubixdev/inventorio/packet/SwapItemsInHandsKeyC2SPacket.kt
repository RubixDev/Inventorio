package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.neoforged.neoforge.network.NetworkEvent

class SwapItemsInHandsKeyC2SPacket {
    // Receiver's consumer
    fun consume(context: NetworkEvent.Context) {
        val sender = context.sender ?: return
        context.enqueueWork {
            sender.inventoryAddon?.swapItemsInHands()
        }
        context.packetHandled = true
    }
}
