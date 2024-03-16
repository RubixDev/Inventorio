package de.rubixdev.inventorio.packet

import com.mojang.datafixers.util.Pair
import de.rubixdev.inventorio.player.PlayerInventoryAddon
import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.entity.EquipmentSlot
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.neoforged.neoforge.network.NetworkEvent
import net.neoforged.neoforge.network.PlayNetworkDirection

class SelectUtilitySlotPacket {
    private var utilitySlot = 0

    // Sender's constructor
    constructor(utilitySlot: Int) {
        this.utilitySlot = utilitySlot
    }

    // Receiver's constructor
    constructor(buf: PacketByteBuf) {
        this.utilitySlot = buf.readByte().toInt()
    }

    // Sender's writer
    fun write(buf: PacketByteBuf) {
        buf.writeByte(utilitySlot)
    }

    // Receiver's consumer
    fun consume(context: NetworkEvent.Context) {
        if (context.direction == PlayNetworkDirection.PLAY_TO_SERVER) {
            val player = context.sender ?: return
            context.enqueueWork {
                player.inventoryAddon?.selectedUtility = utilitySlot

                val broadcastPacket = EntityEquipmentUpdateS2CPacket(player.id, listOf(Pair(EquipmentSlot.OFFHAND, player.offHandStack)))
                (player.world as ServerWorld).chunkManager.sendToOtherNearbyPlayers(player, broadcastPacket)
            }
        } else {
            context.enqueueWork {
                PlayerInventoryAddon.Client.local?.selectedUtility = utilitySlot
            }
        }
        context.packetHandled = true
    }
}
