package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon.Companion.screenHandlerAddon
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class MoveItemToUtilityBeltC2SPacket
{
    private var sourceSlot = 0

    //Sender's constructor
    constructor(sourceSlot: Int)
    {
        this.sourceSlot = sourceSlot
    }

    //Receiver's constructor
    constructor(buf: PacketByteBuf)
    {
        sourceSlot = buf.readByte().toInt()
    }

    //Sender's writer
    fun write(buf: PacketByteBuf)
    {
        buf.writeByte(sourceSlot)
    }

    //Receiver's consumer
    fun consume(supplier: Supplier<NetworkEvent.Context>)
    {
        val player = supplier.get().sender ?: return
        supplier.get().enqueueWork {
            player.screenHandlerAddon?.tryTransferToUtilityBeltSlot(player.playerScreenHandler.getSlot(sourceSlot))
        }
        supplier.get().packetHandled = true
    }
}