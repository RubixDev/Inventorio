package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import io.netty.buffer.ByteBuf
import java.util.concurrent.Executor
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object UseBoostRocketC2SPacket : CustomPayload {
    val ID = CustomPayload.Id<UseBoostRocketC2SPacket>(Identifier("inventorio", "fire_boost_rocket_c2s"))
    val CODEC: PacketCodec<ByteBuf, UseBoostRocketC2SPacket> = PacketCodec.unit(UseBoostRocketC2SPacket)

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    fun consume(payload: UseBoostRocketC2SPacket, executor: Executor, player: ServerPlayerEntity) {
        executor.execute { player.inventoryAddon?.fireRocketFromInventory() }
    }
}
