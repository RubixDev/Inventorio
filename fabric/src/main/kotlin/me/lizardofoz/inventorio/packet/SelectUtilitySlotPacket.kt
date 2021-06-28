package me.lizardofoz.inventorio.packet

import com.mojang.datafixers.util.Pair
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.entity.EquipmentSlot
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier

@Suppress("UNUSED_PARAMETER")
object SelectUtilitySlotPacket
{
    val identifier = Identifier("inventorio", "select_utility")

    //Server's receiving consumer
    fun consume(server: MinecraftServer, player: ServerPlayerEntity, handler: ServerPlayNetworkHandler, buf: PacketByteBuf, responseSender: PacketSender)
    {
        val utilitySlot = buf.readByte().toInt()
        server.execute {
            player.inventoryAddon?.selectedUtility = utilitySlot

            //Resending the current offhand item (aka a selected utility belt item) of this player to other players
            val broadcastPacket = EntityEquipmentUpdateS2CPacket(player.id, listOf(Pair(EquipmentSlot.OFFHAND, player.offHandStack)))
            (player.world as ServerWorld).chunkManager.sendToOtherNearbyPlayers(player, broadcastPacket)
        }
    }

    //Client's receiving consumer
    fun consume(client: MinecraftClient, handler: ClientPlayNetworkHandler, buf: PacketByteBuf, responseSender: PacketSender)
    {
        val utilitySlot = buf.readByte().toInt()
        client.execute {
            PlayerInventoryAddon.Client.local?.selectedUtility = utilitySlot
        }
    }

    fun write(buf: PacketByteBuf, slot: Int = 0)
    {
        buf.writeByte(slot)
    }
}