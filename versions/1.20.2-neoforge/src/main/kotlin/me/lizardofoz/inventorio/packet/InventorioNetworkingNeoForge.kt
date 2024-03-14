package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.network.NetworkRegistry
import net.neoforged.neoforge.network.PlayNetworkDirection

@Suppress("INACCESSIBLE_TYPE", "UNUSED_ANONYMOUS_PARAMETER")
object InventorioNetworkingNeoForge : InventorioNetworking {
    private const val PROTOCOL_VERSION = "1.7"

    private val INSTANCE = NetworkRegistry.newSimpleChannel(
        Identifier("inventorio", "packets"),
        { PROTOCOL_VERSION },
        { PROTOCOL_VERSION == it },
        { PROTOCOL_VERSION == it },
    )

    init
    {
        INSTANCE.registerMessage(
            0,
            SelectUtilitySlotPacket::class.java,
            { packet, buf -> packet.write(buf) },
            { buf -> SelectUtilitySlotPacket(buf) },
            { packet, context -> packet.consume(context) },
        )

        INSTANCE.registerMessage(
            1,
            UpdateAddonStacksS2CPacket::class.java,
            { packet, buf -> packet.write(buf) },
            { buf -> UpdateAddonStacksS2CPacket(buf) },
            { packet, context -> packet.consume(context) },
        )

        INSTANCE.registerMessage(
            2,
            GlobalSettingsS2CPacket::class.java,
            { packet, buf -> packet.write(buf) },
            { buf -> GlobalSettingsS2CPacket(buf) },
            { packet, context -> packet.consume(context) },
        )

        INSTANCE.registerMessage(
            3,
            UseBoostRocketC2SPacket::class.java,
            { packet, buf -> },
            { buf -> UseBoostRocketC2SPacket() },
            { packet, context -> packet.consume(context) },
        )

        INSTANCE.registerMessage(
            4,
            SwappedHandsModeC2SPacket::class.java,
            { packet, buf -> packet.write(buf) },
            { buf -> SwappedHandsModeC2SPacket(buf) },
            { packet, context -> packet.consume(context) },
        )

        INSTANCE.registerMessage(
            5,
            MoveItemToUtilityBeltC2SPacket::class.java,
            { packet, buf -> packet.write(buf) },
            { buf -> MoveItemToUtilityBeltC2SPacket(buf) },
            { packet, context -> packet.consume(context) },
        )

        INSTANCE.registerMessage(
            6,
            OpenInventorioScreenC2SPacket::class.java,
            { packet, buf -> },
            { buf -> OpenInventorioScreenC2SPacket() },
            { packet, context -> packet.consume(context) },
        )

        INSTANCE.registerMessage(
            7,
            SwapItemsInHandsKeyC2SPacket::class.java,
            { packet, buf -> },
            { buf -> SwapItemsInHandsKeyC2SPacket() },
            { packet, context -> packet.consume(context) },
        )
    }

    override fun s2cSelectUtilitySlot(player: ServerPlayerEntity) {
        val inventoryAddon = player.inventoryAddon ?: return
        INSTANCE.sendTo(SelectUtilitySlotPacket(inventoryAddon.selectedUtility), player.networkHandler.connection, PlayNetworkDirection.PLAY_TO_CLIENT)
    }

    override fun s2cGlobalSettings(player: ServerPlayerEntity) {
        INSTANCE.sendTo(GlobalSettingsS2CPacket(), player.networkHandler.connection, PlayNetworkDirection.PLAY_TO_CLIENT)
    }

    override fun s2cUpdateAddonStacks(player: ServerPlayerEntity, updatedStacks: Map<Int, ItemStack>) {
        INSTANCE.sendTo(UpdateAddonStacksS2CPacket(updatedStacks), player.networkHandler.connection, PlayNetworkDirection.PLAY_TO_CLIENT)
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sSelectUtilitySlot(selectedUtility: Int) {
        INSTANCE.sendToServer(SelectUtilitySlotPacket(selectedUtility))
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sUseBoostRocket() {
        INSTANCE.sendToServer(UseBoostRocketC2SPacket())
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sSetSwappedHandsMode(swappedHands: Boolean) {
        if (MinecraftClient.getInstance().networkHandler != null) {
            INSTANCE.sendToServer(SwappedHandsModeC2SPacket(swappedHands))
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sMoveItemToUtilityBelt(sourceSlot: Int) {
        INSTANCE.sendToServer(MoveItemToUtilityBeltC2SPacket(sourceSlot))
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sOpenInventorioScreen() {
        INSTANCE.sendToServer(OpenInventorioScreenC2SPacket())
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sSwapItemsInHands() {
        INSTANCE.sendToServer(SwapItemsInHandsKeyC2SPacket())
    }
}
