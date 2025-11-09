package de.rubixdev.inventorio.packet

import com.mojang.datafixers.util.Pair
import de.rubixdev.inventorio.player.PlayerInventoryAddon
import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import io.netty.buffer.ByteBuf
import java.util.concurrent.Executor
import net.minecraft.entity.EquipmentSlot
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier

data class SelectUtilitySlotPacket(val slot: Byte = 0) : CustomPayload {
    companion object {
        val ID = CustomPayload.Id<SelectUtilitySlotPacket>(Identifier("inventorio", "select_utility"))
        val CODEC: PacketCodec<ByteBuf, SelectUtilitySlotPacket> =
            PacketCodecs.BYTE.xmap(::SelectUtilitySlotPacket) { it.slot }
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    // Server's receiving consumer
    fun consume(executor: Executor, player: ServerPlayerEntity) {
        executor.execute {
            player.inventoryAddon?.selectedUtility = slot.toInt()

            // Resending the current offhand item (aka a selected utility belt item) of this player to other players
            val broadcastPacket =
                EntityEquipmentUpdateS2CPacket(
                    player.id,
                    listOf(Pair(EquipmentSlot.OFFHAND, player.offHandStack)),
                )
            (player.world as ServerWorld).chunkManager.sendToOtherNearbyPlayers(player, broadcastPacket)
        }
    }

    // Client's receiving consumer
    fun consume(executor: Executor) {
        executor.execute { PlayerInventoryAddon.Client.local?.selectedUtility = slot.toInt() }
    }
}
