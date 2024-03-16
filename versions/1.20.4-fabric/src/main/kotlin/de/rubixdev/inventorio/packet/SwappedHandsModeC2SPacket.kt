package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

@Suppress("UNUSED_PARAMETER")
object SwappedHandsModeC2SPacket {
    val identifier = Identifier("inventorio", "swapped_hands")

    fun consume(server: MinecraftServer, player: ServerPlayerEntity, handler: ServerPlayNetworkHandler, buf: PacketByteBuf, responseSender: PacketSender) {
        val swappedHands = buf.readBoolean()
        server.execute {
            player.inventoryAddon?.swappedHands = swappedHands
        }
    }

    fun write(buf: PacketByteBuf, swappedHands: Boolean = false) {
        buf.writeBoolean(swappedHands)
    }
}
