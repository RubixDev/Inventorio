package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.client.MinecraftClient
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkRegistry

@Suppress("INACCESSIBLE_TYPE", "UNUSED_ANONYMOUS_PARAMETER")
object InventorioNetworkingForge : InventorioNetworking
{
    private const val PROTOCOL_VERSION = "1.3"

    private val INSTANCE = NetworkRegistry.newSimpleChannel(
            Identifier("inventorio", "packets"),
            { PROTOCOL_VERSION },
            { PROTOCOL_VERSION == it },
            { PROTOCOL_VERSION == it }
    )!!

    init
    {
        INSTANCE.registerMessage(0, SelectUtilitySlotPacket::class.java,
                { packet, buf -> packet.write(buf) },
                { buf -> SelectUtilitySlotPacket(buf) },
                { packet, supplier -> packet.consume(supplier) })

        INSTANCE.registerMessage(1, UseBoostRocketC2SPacket::class.java,
                { packet, buf -> },
                { buf -> UseBoostRocketC2SPacket() },
                { packet, supplier -> packet.consume(supplier) })

        INSTANCE.registerMessage(2, SwappedHandsC2SPacket::class.java,
            { packet, buf -> packet.write(buf) },
            { buf -> SwappedHandsC2SPacket(buf) },
            { packet, supplier -> packet.consume(supplier) })

        INSTANCE.registerMessage(3, SendItemToUtilityBeltC2SPacket::class.java,
            { packet, buf -> packet.write(buf) },
            { buf -> SendItemToUtilityBeltC2SPacket(buf) },
            { packet, supplier -> packet.consume(supplier) })
    }

    override fun s2cSendSelectedUtilitySlot(player: ServerPlayerEntity)
    {
        val inventoryAddon = player.inventoryAddon ?: return
        INSTANCE.sendTo(SelectUtilitySlotPacket(inventoryAddon.selectedUtility), player.networkHandler.connection, NetworkDirection.PLAY_TO_CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    override fun c2sSetSwappedHands(swappedHands: Boolean)
    {
        if (MinecraftClient.getInstance().networkHandler != null)
            INSTANCE.sendToServer(SwappedHandsC2SPacket(swappedHands))
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sSendItemToUtilityBelt(sourceSlot: Int)
    {
        INSTANCE.sendToServer(SendItemToUtilityBeltC2SPacket(sourceSlot))
    }
}