package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkRegistry

object InventorioNetworkingForge : InventorioNetworking
{
    private const val PROTOCOL_VERSION = "1"

    val INSTANCE = NetworkRegistry.newSimpleChannel(
            Identifier("inventorio", "packets"),
            { PROTOCOL_VERSION },
            { PROTOCOL_VERSION == it },
            { PROTOCOL_VERSION == it }
    )

    fun initialize()
    {
        INSTANCE.registerMessage(0, SelectUtilitySlotPacket::class.java,
                { packet, buf -> packet.write(buf) },
                { buf -> SelectUtilitySlotPacket(buf) },
                { packet, supplier -> packet.consume(supplier) })

        INSTANCE.registerMessage(1, UseBoostRocketC2SPacket::class.java,
                { packet, buf -> },
                { buf -> UseBoostRocketC2SPacket() },
                { packet, supplier -> packet.consume(supplier) })
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sSendSelectedUtilitySlot(selectedUtility: Int)
    {
        INSTANCE.sendToServer(SelectUtilitySlotPacket(selectedUtility))
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sUseBoostRocket()
    {
        INSTANCE.sendToServer(UseBoostRocketC2SPacket())
    }

    override fun s2cSendSelectedUtilitySlot(player: ServerPlayerEntity)
    {
        INSTANCE.sendTo(SelectUtilitySlotPacket(player.inventoryAddon.selectedUtility), player.networkHandler.connection, NetworkDirection.PLAY_TO_CLIENT)
    }
}