package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.InventorioScreenHandler
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

@Suppress("UNUSED_PARAMETER")
object OpenInventorioScreenC2SPacket
{
    val identifier = Identifier("inventorio", "open_screen")

    fun consume(server: MinecraftServer, player: ServerPlayerEntity, handler: ServerPlayNetworkHandler, buf: PacketByteBuf, responseSender: PacketSender)
    {
        server.execute {
            InventorioScreenHandler.open(player)
        }
    }

    fun write(buf: PacketByteBuf)
    {
    }
}