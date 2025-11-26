package de.rubixdev.inventorio.packet

import java.util.concurrent.Executor
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.handling.IPayloadContext

fun <T : CustomPayload> consumeClient(consumer: T.(Executor) -> Unit) =
    { payload: T, ctx: IPayloadContext ->
        payload.consumer { ctx.enqueueWork(it) }
    }

fun <T : CustomPayload> consumeServer(consumer: T.(Executor, ServerPlayerEntity) -> Unit) =
    { payload: T, ctx: IPayloadContext ->
        payload.consumer({ ctx.enqueueWork(it) }, ctx.player() as ServerPlayerEntity)
    }

object InventorioNetworkingNeoForge : InventorioNetworking {
    private const val PROTOCOL_VERSION = "1.9"

    @SubscribeEvent
    fun registerPayloads(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(PROTOCOL_VERSION)

        registrar.playBidirectional(
            SelectUtilitySlotPacket.ID,
            SelectUtilitySlotPacket.CODEC,
            DirectionalPayloadHandler(
                consumeClient(SelectUtilitySlotPacket::consume),
                consumeServer(SelectUtilitySlotPacket::consume),
            ),
        )

        registrar.playToClient(
            GlobalSettingsS2CPacket.ID,
            GlobalSettingsS2CPacket.CODEC,
            consumeClient(GlobalSettingsS2CPacket::consume),
        )
        registrar.playToClient(
            UpdateAddonStacksS2CPacket.ID,
            UpdateAddonStacksS2CPacket.CODEC,
            consumeClient(UpdateAddonStacksS2CPacket::consume),
        )

        registrar.playToServer(
            UseBoostRocketC2SPacket.ID,
            UseBoostRocketC2SPacket.CODEC,
            consumeServer(UseBoostRocketC2SPacket::consume),
        )
        registrar.playToServer(
            SwappedHandsModeC2SPacket.ID,
            SwappedHandsModeC2SPacket.CODEC,
            consumeServer(SwappedHandsModeC2SPacket::consume),
        )
        registrar.playToServer(
            MoveItemToUtilityBeltC2SPacket.ID,
            MoveItemToUtilityBeltC2SPacket.CODEC,
            consumeServer(MoveItemToUtilityBeltC2SPacket::consume),
        )
        registrar.playToServer(
            OpenInventorioScreenC2SPacket.ID,
            OpenInventorioScreenC2SPacket.CODEC,
            consumeServer(OpenInventorioScreenC2SPacket::consume),
        )
        registrar.playToServer(
            SwapItemsInHandsKeyC2SPacket.ID,
            SwapItemsInHandsKeyC2SPacket.CODEC,
            consumeServer(SwapItemsInHandsKeyC2SPacket::consume),
        )
    }

    override fun sendToPlayer(player: ServerPlayerEntity, packet: CustomPayload) {
        PacketDistributor.sendToPlayer(player, packet)
    }

    @OnlyIn(Dist.CLIENT)
    override fun sendToServer(packet: CustomPayload) {
        PacketDistributor.sendToServer(packet)
    }
}
