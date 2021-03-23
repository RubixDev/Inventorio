package me.danetnaverno.inventorio.packet.c2s

import com.mojang.datafixers.util.Pair
import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.player.PlayerAddon
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
        PlayerAddon[player].inventoryAddon.selectedUtility = slot

        //Resending the current offhand item (aka a selected utility belt item) of this player to other players
        val broadcastPacket = EntityEquipmentUpdateS2CPacket(player.entityId, listOf(Pair(EquipmentSlot.OFFHAND, player.offHandStack)))
        (player.world as ServerWorld).chunkManager.sendToOtherNearbyPlayers(player, broadcastPacket)

        RobertoGarbagio.LOGGER.info("Applying SelectUtilitySlotC2SPacket: $slot")
    }

    fun write(buf: PacketByteBuf, slot: Int = 0)
    {
        buf.writeByte(slot)
    }
}