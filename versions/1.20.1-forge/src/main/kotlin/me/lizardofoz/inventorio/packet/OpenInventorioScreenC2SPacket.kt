package me.lizardofoz.inventorio.packet

import java.util.function.Supplier
import me.lizardofoz.inventorio.player.InventorioScreenHandler
import net.minecraftforge.network.NetworkEvent

class OpenInventorioScreenC2SPacket {
    fun consume(supplier: Supplier<NetworkEvent.Context>) {
        val sender = supplier.get().sender ?: return
        supplier.get().enqueueWork {
            InventorioScreenHandler.open(sender)
        }
        supplier.get().packetHandled = true
    }
}
