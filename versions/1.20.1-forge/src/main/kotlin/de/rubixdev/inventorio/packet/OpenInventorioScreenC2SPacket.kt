package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.AbstractInventorioScreenHandler
import java.util.function.Supplier
import net.minecraftforge.network.NetworkEvent

class OpenInventorioScreenC2SPacket {
    fun consume(supplier: Supplier<NetworkEvent.Context>) {
        val sender = supplier.get().sender ?: return
        supplier.get().enqueueWork {
            AbstractInventorioScreenHandler.open(sender)
        }
        supplier.get().packetHandled = true
    }
}
