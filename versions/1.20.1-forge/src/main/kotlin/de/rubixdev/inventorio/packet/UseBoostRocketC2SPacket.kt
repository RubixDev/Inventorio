package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import java.util.function.Supplier
import net.minecraftforge.network.NetworkEvent

class UseBoostRocketC2SPacket {
    fun consume(supplier: Supplier<NetworkEvent.Context>) {
        val sender = supplier.get().sender ?: return
        supplier.get().enqueueWork {
            sender.inventoryAddon?.fireRocketFromInventory()
        }
        supplier.get().packetHandled = true
    }
}
