package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import io.netty.buffer.PooledByteBufAllocator
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object InventorioNetworkingFabric : InventorioNetworking {
    init {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(SelectUtilitySlotPacket.identifier, SelectUtilitySlotPacket::consume)
            ClientPlayNetworking.registerGlobalReceiver(GlobalSettingsS2CPacket.identifier, GlobalSettingsS2CPacket::consume)
            ClientPlayNetworking.registerGlobalReceiver(UpdateAddonStacksS2CPacket.identifier, UpdateAddonStacksS2CPacket::consume)
        }

        ServerPlayNetworking.registerGlobalReceiver(UseBoostRocketC2SPacket.identifier, UseBoostRocketC2SPacket::consume)
        ServerPlayNetworking.registerGlobalReceiver(SelectUtilitySlotPacket.identifier, SelectUtilitySlotPacket::consume)
        ServerPlayNetworking.registerGlobalReceiver(SwapItemsInHandsKeyC2SPacket.identifier, SwapItemsInHandsKeyC2SPacket::consume)
        ServerPlayNetworking.registerGlobalReceiver(SwappedHandsModeC2SPacket.identifier, SwappedHandsModeC2SPacket::consume)
        ServerPlayNetworking.registerGlobalReceiver(MoveItemToUtilityBeltC2SPacket.identifier, MoveItemToUtilityBeltC2SPacket::consume)
        ServerPlayNetworking.registerGlobalReceiver(OpenInventorioScreenC2SPacket.identifier, OpenInventorioScreenC2SPacket::consume)
    }

    override fun s2cSelectUtilitySlot(player: ServerPlayerEntity) {
        val inventoryAddon = player.inventoryAddon ?: return
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        SelectUtilitySlotPacket.write(buf, inventoryAddon.selectedUtility)
        ServerPlayNetworking.send(player, SelectUtilitySlotPacket.identifier, buf)
    }

    override fun s2cGlobalSettings(player: ServerPlayerEntity) {
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        GlobalSettingsS2CPacket.write(buf)
        ServerPlayNetworking.send(player, GlobalSettingsS2CPacket.identifier, buf)
    }

    override fun s2cUpdateAddonStacks(player: ServerPlayerEntity, updatedStacks: Map<Int, ItemStack>) {
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        UpdateAddonStacksS2CPacket.write(buf, updatedStacks)
        ServerPlayNetworking.send(player, UpdateAddonStacksS2CPacket.identifier, buf)
    }

    @Environment(EnvType.CLIENT)
    override fun c2sSelectUtilitySlot(selectedUtility: Int) {
        sendC2S(SelectUtilitySlotPacket.identifier) {
            SelectUtilitySlotPacket.write(it, selectedUtility)
        }
    }

    @Environment(EnvType.CLIENT)
    override fun c2sUseBoostRocket() {
        sendC2S(UseBoostRocketC2SPacket.identifier) { }
    }

    @Environment(EnvType.CLIENT)
    override fun c2sSetSwappedHandsMode(swappedHands: Boolean) {
        if (MinecraftClient.getInstance().networkHandler != null) {
            sendC2S(SwappedHandsModeC2SPacket.identifier) {
                SwappedHandsModeC2SPacket.write(it, swappedHands)
            }
        }
    }

    @Environment(EnvType.CLIENT)
    override fun c2sMoveItemToUtilityBelt(sourceSlot: Int) {
        sendC2S(MoveItemToUtilityBeltC2SPacket.identifier) {
            MoveItemToUtilityBeltC2SPacket.write(it, sourceSlot)
        }
    }

    @Environment(EnvType.CLIENT)
    override fun c2sOpenInventorioScreen() {
        sendC2S(OpenInventorioScreenC2SPacket.identifier) {
            OpenInventorioScreenC2SPacket.write(it)
        }
    }

    override fun c2sSwapItemsInHands() {
        sendC2S(SwapItemsInHandsKeyC2SPacket.identifier) { }
    }

    @Environment(EnvType.CLIENT)
    private fun sendC2S(id: Identifier, func: (PacketByteBuf) -> Unit) {
        val buf = PacketByteBuf(PooledByteBufAllocator.DEFAULT.buffer())
        func(buf)
        ClientPlayNetworking.send(id, buf)
    }
}
