package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.InventorioScreenHandler.Companion.inventorioScreenHandler
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

@Suppress("UNUSED_PARAMETER")
object MoveItemToUtilityBeltC2SPacket {
    val identifier = Identifier("inventorio", "move_to_utility_c2s")

    fun consume(server: MinecraftServer, player: ServerPlayerEntity, handler: ServerPlayNetworkHandler, buf: PacketByteBuf, responseSender: PacketSender) {
        val sourceSlot = buf.readByte().toInt()
        server.execute {
            val screenHandler = player.inventorioScreenHandler ?: return@execute
            screenHandler.tryTransferToUtilityBeltSlot(screenHandler.getSlot(sourceSlot))
        }
    }

    fun write(buf: PacketByteBuf, sourceSlot: Int = 0) {
        buf.writeByte(sourceSlot)
    }
}
