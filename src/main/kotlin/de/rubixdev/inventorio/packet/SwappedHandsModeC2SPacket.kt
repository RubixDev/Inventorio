package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import de.rubixdev.inventorio.util.id
import io.netty.buffer.ByteBuf
import java.util.concurrent.Executor
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity

data class SwappedHandsModeC2SPacket(val swappedHands: Boolean = false) : CustomPayload {
    companion object {
        val ID = CustomPayload.Id<SwappedHandsModeC2SPacket>("swapped_hands".id)
        val CODEC: PacketCodec<ByteBuf, SwappedHandsModeC2SPacket> = PacketCodecs.BOOL.xmap(::SwappedHandsModeC2SPacket) { it.swappedHands }
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    fun consume(executor: Executor, player: ServerPlayerEntity) {
        executor.execute { player.inventoryAddon?.swappedHands = swappedHands }
    }
}
