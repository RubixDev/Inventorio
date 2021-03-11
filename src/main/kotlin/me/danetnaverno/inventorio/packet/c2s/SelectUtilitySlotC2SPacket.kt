package me.danetnaverno.inventorio.packet.c2s

import com.mojang.datafixers.util.Pair
import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.player.PlayerAddon
import net.minecraft.entity.EquipmentSlot
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ServerPlayPacketListener
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.world.ServerWorld

class SelectUtilitySlotC2SPacket(var slot: Int = 0) : Packet<ServerPlayPacketListener>
{
    override fun read(buf: PacketByteBuf)
    {
        slot = buf.readByte().toInt()
    }

    override fun write(buf: PacketByteBuf)
    {
        buf.writeByte(slot)
    }

    override fun apply(listener: ServerPlayPacketListener)
    {
        RobertoGarbagio.LOGGER.info("Applying SelectUtilitySlotC2SPacket: $slot")
        val player = (listener as ServerPlayNetworkHandler).player
        PlayerAddon[player].inventoryAddon.selectedUtility = slot

        //Resending the current offhand item (aka a selected utility belt item) of this player to other players
        val broadcastPacket = EntityEquipmentUpdateS2CPacket(player.entityId, listOf(Pair(EquipmentSlot.OFFHAND, player.offHandStack)))
        (player.world as ServerWorld).chunkManager.sendToOtherNearbyPlayers(player, broadcastPacket)
    }
}