package me.lizardofoz.inventorio.packet

import java.util.function.Supplier
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
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
