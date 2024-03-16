package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.neoforged.neoforge.network.NetworkEvent

class UseBoostRocketC2SPacket {
    fun consume(context: NetworkEvent.Context) {
        val sender = context.sender ?: return
        context.enqueueWork {
            sender.inventoryAddon?.fireRocketFromInventory()
        }
        context.packetHandled = true
    }
}
