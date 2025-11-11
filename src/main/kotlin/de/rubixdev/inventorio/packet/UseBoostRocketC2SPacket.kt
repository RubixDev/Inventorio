package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import de.rubixdev.inventorio.util.id
import io.netty.buffer.ByteBuf
import java.util.concurrent.Executor
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity

object UseBoostRocketC2SPacket : CustomPayload {
    val ID = CustomPayload.Id<UseBoostRocketC2SPacket>("fire_boost_rocket_c2s".id)
    val CODEC: PacketCodec<ByteBuf, UseBoostRocketC2SPacket> = PacketCodec.unit(UseBoostRocketC2SPacket)

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    fun consume(payload: UseBoostRocketC2SPacket, executor: Executor, player: ServerPlayerEntity) {
        executor.execute { player.inventoryAddon?.fireRocketFromInventory() }
    }
}
