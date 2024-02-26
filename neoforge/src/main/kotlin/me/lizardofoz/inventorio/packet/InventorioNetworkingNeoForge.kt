package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
object InventorioNetworkingNeoForge : InventorioNetworking
{
    private const val PROTOCOL_VERSION = "1.8"

    @SubscribeEvent
    fun registerPayloads(event: RegisterPayloadHandlerEvent) {
        val registrar = event.registrar("inventorio").versioned(PROTOCOL_VERSION)

        registrar.play(SelectUtilitySlotPacket.identifier, ::SelectUtilitySlotPacket) {
            handler -> handler.client(SelectUtilitySlotPacket::consumeClient).server(SelectUtilitySlotPacket::consumeServer)
        }
        registrar.play(GlobalSettingsS2CPacket.identifier, ::GlobalSettingsS2CPacket) { handler -> handler.client(GlobalSettingsS2CPacket::consume) }
        registrar.play(UpdateAddonStacksS2CPacket.identifier, ::UpdateAddonStacksS2CPacket) { handler -> handler.client(UpdateAddonStacksS2CPacket::consume) }

        registrar.play(UseBoostRocketC2SPacket.identifier, { buf -> UseBoostRocketC2SPacket() }) { handler -> handler.server(UseBoostRocketC2SPacket::consume) }
        registrar.play(SwappedHandsModeC2SPacket.identifier, ::SwappedHandsModeC2SPacket) { handler -> handler.server(SwappedHandsModeC2SPacket::consume) }
        registrar.play(MoveItemToUtilityBeltC2SPacket.identifier, ::MoveItemToUtilityBeltC2SPacket) { handler -> handler.server(MoveItemToUtilityBeltC2SPacket::consume) }
        registrar.play(OpenInventorioScreenC2SPacket.identifier, { buf -> OpenInventorioScreenC2SPacket() }) { handler -> handler.server(OpenInventorioScreenC2SPacket::consume) }
        registrar.play(SwapItemsInHandsKeyC2SPacket.identifier, { buf -> SwapItemsInHandsKeyC2SPacket() }) { handler -> handler.server(SwapItemsInHandsKeyC2SPacket::consume) }
    }

    override fun s2cSelectUtilitySlot(player: ServerPlayerEntity)
    {
        val inventoryAddon = player.inventoryAddon ?: return
        PacketDistributor.PLAYER.with(player).send(SelectUtilitySlotPacket(inventoryAddon.selectedUtility))
    }

    override fun s2cGlobalSettings(player: ServerPlayerEntity)
    {
        PacketDistributor.PLAYER.with(player).send(GlobalSettingsS2CPacket())
    }

    override fun s2cUpdateAddonStacks(player: ServerPlayerEntity, updatedStacks: Map<Int, ItemStack>)
    {
        PacketDistributor.PLAYER.with(player).send(UpdateAddonStacksS2CPacket(updatedStacks))
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sSelectUtilitySlot(selectedUtility: Int)
    {
        PacketDistributor.SERVER.noArg().send(SelectUtilitySlotPacket(selectedUtility))
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sUseBoostRocket()
    {
        PacketDistributor.SERVER.noArg().send(UseBoostRocketC2SPacket())
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sSetSwappedHandsMode(swappedHands: Boolean)
    {
        if (MinecraftClient.getInstance().networkHandler != null)
            PacketDistributor.SERVER.noArg().send(SwappedHandsModeC2SPacket(swappedHands))
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sMoveItemToUtilityBelt(sourceSlot: Int)
    {
        PacketDistributor.SERVER.noArg().send(MoveItemToUtilityBeltC2SPacket(sourceSlot))
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sOpenInventorioScreen()
    {
        PacketDistributor.SERVER.noArg().send(OpenInventorioScreenC2SPacket())
    }

    @OnlyIn(Dist.CLIENT)
    override fun c2sSwapItemsInHands()
    {
        PacketDistributor.SERVER.noArg().send(SwapItemsInHandsKeyC2SPacket())
    }
}
