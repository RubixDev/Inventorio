package me.lizardofoz.inventorio.packet

import com.mojang.datafixers.util.Pair
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier

object SelectUtilitySlotC2SPacket
{
    val identifier = Identifier("inventorio","select_utility_c2s")

    fun consume(context: PacketContext, buf: PacketByteBuf)
    {
        val slot = buf.readByte().toInt()
        val player = context.player
        player.inventoryAddon.selectedUtility = slot

        //Resending the current offhand item (aka a selected utility belt item) of this player to other players
        val broadcastPacket = EntityEquipmentUpdateS2CPacket(player.entityId, listOf(Pair(EquipmentSlot.OFFHAND, player.offHandStack)))
        (player.world as ServerWorld).chunkManager.sendToOtherNearbyPlayers(player, broadcastPacket)
    }

    fun write(buf: PacketByteBuf, slot: Int = 0)
    {
        buf.writeByte(slot)
    }
}