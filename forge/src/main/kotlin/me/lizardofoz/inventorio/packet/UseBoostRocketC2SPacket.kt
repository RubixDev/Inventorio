package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraftforge.fmllegacy.network.NetworkEvent
import java.util.function.Supplier

class UseBoostRocketC2SPacket
{
    fun consume(supplier: Supplier<NetworkEvent.Context>)
    {
        val sender = supplier.get().sender ?: return
        supplier.get().enqueueWork {
            sender.inventoryAddon?.fireRocketFromInventory()
        }
        supplier.get().packetHandled = true
    }
}