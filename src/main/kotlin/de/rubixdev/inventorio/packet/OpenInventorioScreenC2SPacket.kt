package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.InventorioScreenHandler
import io.netty.buffer.ByteBuf
import java.util.concurrent.Executor
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object OpenInventorioScreenC2SPacket : CustomPayload {
    val ID = CustomPayload.Id<OpenInventorioScreenC2SPacket>(Identifier("inventorio", "open_screen"))
    val CODEC: PacketCodec<ByteBuf, OpenInventorioScreenC2SPacket> = PacketCodec.unit(OpenInventorioScreenC2SPacket)

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    fun consume(payload: OpenInventorioScreenC2SPacket, executor: Executor, player: ServerPlayerEntity) {
        executor.execute { InventorioScreenHandler.open(player) }
    }
}
