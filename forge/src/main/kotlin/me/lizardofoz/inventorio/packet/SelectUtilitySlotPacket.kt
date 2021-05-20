package me.lizardofoz.inventorio.packet

import com.mojang.datafixers.util.Pair
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.entity.EquipmentSlot
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.server.world.ServerWorld
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class SelectUtilitySlotPacket
{
    private var utilitySlot = 0

    @Suppress("unused")
    constructor()
    {
    }

    constructor(utilitySlot: Int)
    {
        this.utilitySlot = utilitySlot
    }

    constructor(buf: PacketByteBuf)
    {
        this.utilitySlot = buf.readByte().toInt()
    }

    fun write(buf: PacketByteBuf)
    {
        buf.writeByte(utilitySlot)
    }

    fun consume(supplier: Supplier<NetworkEvent.Context>)
    {
        if (supplier.get().direction == NetworkDirection.PLAY_TO_SERVER)
        {
            val player = supplier.get().sender ?: return
            supplier.get().enqueueWork {
                player.inventoryAddon?.selectedUtility = utilitySlot

                val broadcastPacket = EntityEquipmentUpdateS2CPacket(player.entityId, listOf(Pair(EquipmentSlot.OFFHAND, player.offHandStack)))
                (player.world as ServerWorld).chunkManager.sendToOtherNearbyPlayers(player, broadcastPacket)
            }
            supplier.get().packetHandled = true
        }
        else
        {
            supplier.get().enqueueWork { selectOnClient() }
            supplier.get().packetHandled = true
        }
    }

    @OnlyIn(Dist.CLIENT)
    private fun selectOnClient()
    {
        PlayerInventoryAddon.Client.local.selectedUtility = utilitySlot
    }
}