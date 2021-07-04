package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.InventorioScreenHandler
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class OpenInventorioScreenC2SPacket
{
    fun consume(supplier: Supplier<NetworkEvent.Context>)
    {
        val sender = supplier.get().sender ?: return
        supplier.get().enqueueWork {
            InventorioScreenHandler.open(sender)
        }
        supplier.get().packetHandled = true
    }
}