package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.InventorioScreenHandler.Companion.inventorioScreenHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import net.neoforged.neoforge.network.handling.PlayPayloadContext
import kotlin.jvm.optionals.getOrNull

class MoveItemToUtilityBeltC2SPacket: CustomPayload
{
    companion object {
        val identifier = Identifier("inventorio", "move_to_utility_c2s")
    }
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

    override fun id(): Identifier = identifier

    //Sender's writer
    override fun write(buf: PacketByteBuf)
    {
        buf.writeByte(sourceSlot)
    }

    //Receiver's consumer
    fun consume(context: PlayPayloadContext)
    {
        val player = context.player.getOrNull() ?: return
        context.workHandler.execute {
            val screenHandler = player.inventorioScreenHandler ?: return@execute
            screenHandler.tryTransferToUtilityBeltSlot(screenHandler.getSlot(sourceSlot))
        }
    }
}