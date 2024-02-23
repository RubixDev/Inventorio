package me.lizardofoz.inventorio.packet

import com.mojang.datafixers.util.Pair
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.entity.EquipmentSlot
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.CustomPayload
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.neoforged.neoforge.network.handling.PlayPayloadContext
import kotlin.jvm.optionals.getOrNull

class SelectUtilitySlotPacket : CustomPayload
{
    companion object {
        val identifier = Identifier("inventorio", "select_utility")
    }
    private var utilitySlot = 0

    //Sender's constructor
    constructor(utilitySlot: Int)
    {
        this.utilitySlot = utilitySlot
    }

    //Receiver's constructor
    constructor(buf: PacketByteBuf)
    {
        this.utilitySlot = buf.readByte().toInt()
    }

    override fun id(): Identifier = identifier

    //Sender's writer
    override fun write(buf: PacketByteBuf)
    {
        buf.writeByte(utilitySlot)
    }

    //Client's receiving consumer
    fun consumeClient(context: PlayPayloadContext)
    {
        context.workHandler.execute {
            PlayerInventoryAddon.Client.local?.selectedUtility = utilitySlot
        }
    }

    //Server's receiving consumer
    fun consumeServer(context: PlayPayloadContext) {
        val player = context.player.getOrNull() ?: return
        context.workHandler.execute {
            player.inventoryAddon?.selectedUtility = utilitySlot

            val broadcastPacket = EntityEquipmentUpdateS2CPacket(player.id, listOf(Pair(EquipmentSlot.OFFHAND, player.offHandStack)))
            (player.world as ServerWorld).chunkManager.sendToOtherNearbyPlayers(player, broadcastPacket)
        }
    }
}