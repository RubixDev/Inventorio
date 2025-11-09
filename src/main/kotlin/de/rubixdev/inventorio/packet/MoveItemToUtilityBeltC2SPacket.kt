package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.InventorioScreenHandler.Companion.inventorioScreenHandler
import io.netty.buffer.ByteBuf
import java.util.concurrent.Executor
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

data class MoveItemToUtilityBeltC2SPacket(val sourceSlot: Int = 0) : CustomPayload {
    companion object {
        val ID = CustomPayload.Id<MoveItemToUtilityBeltC2SPacket>(Identifier("inventorio", "move_to_utility_c2s"))
        val CODEC: PacketCodec<ByteBuf, MoveItemToUtilityBeltC2SPacket> = PacketCodecs.VAR_INT.xmap(::MoveItemToUtilityBeltC2SPacket) { it.sourceSlot }
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    fun consume(executor: Executor, player: ServerPlayerEntity) {
        executor.execute {
            val screenHandler = player.inventorioScreenHandler ?: return@execute
            screenHandler.tryTransferToUtilityBeltSlot(screenHandler.getSlot(sourceSlot))
        }
    }
}
