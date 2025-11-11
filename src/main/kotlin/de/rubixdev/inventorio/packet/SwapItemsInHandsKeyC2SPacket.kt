package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import de.rubixdev.inventorio.util.id
import io.netty.buffer.ByteBuf
import java.util.concurrent.Executor
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity

object SwapItemsInHandsKeyC2SPacket : CustomPayload {
    val ID = CustomPayload.Id<SwapItemsInHandsKeyC2SPacket>("swap_items_in_hands".id)
    val CODEC: PacketCodec<ByteBuf, SwapItemsInHandsKeyC2SPacket> = PacketCodec.unit(SwapItemsInHandsKeyC2SPacket)

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    fun consume(payload: SwapItemsInHandsKeyC2SPacket, executor: Executor, player: ServerPlayerEntity) {
        executor.execute { player.inventoryAddon?.swapItemsInHands() }
    }
}
