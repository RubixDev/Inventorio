package me.lizardofoz.inventorio.packet

import java.util.function.Supplier
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraftforge.network.NetworkEvent

class SwapItemsInHandsKeyC2SPacket {
    // Receiver's consumer
    fun consume(supplier: Supplier<NetworkEvent.Context>) {
        val sender = supplier.get().sender ?: return
        supplier.get().enqueueWork {
            sender.inventoryAddon?.swapItemsInHands()
        }
        supplier.get().packetHandled = true
    }
}
