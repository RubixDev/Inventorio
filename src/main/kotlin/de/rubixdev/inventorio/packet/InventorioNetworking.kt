package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity

interface InventorioNetworking {
    fun sendToPlayer(player: ServerPlayerEntity, packet: CustomPayload)

    @Environment(EnvType.CLIENT)
    fun sendToServer(packet: CustomPayload)

    fun s2cSelectUtilitySlot(player: ServerPlayerEntity) {
        val inventoryAddon = player.inventoryAddon ?: return
        sendToPlayer(player, SelectUtilitySlotPacket(inventoryAddon.selectedUtility.toByte()))
    }

    fun s2cGlobalSettings(player: ServerPlayerEntity) {
        sendToPlayer(player, GlobalSettingsS2CPacket())
    }

    fun s2cUpdateAddonStacks(player: ServerPlayerEntity, updatedStacks: Map<Int, ItemStack>) {
        sendToPlayer(player, UpdateAddonStacksS2CPacket(updatedStacks))
    }

    @Environment(EnvType.CLIENT)
    fun c2sSelectUtilitySlot(selectedUtility: Int) {
        sendToServer(SelectUtilitySlotPacket(selectedUtility.toByte()))
    }

    @Environment(EnvType.CLIENT)
    fun c2sUseBoostRocket() {
        sendToServer(UseBoostRocketC2SPacket)
    }

    @Environment(EnvType.CLIENT)
    fun c2sSetSwappedHandsMode(swappedHands: Boolean) {
        if (MinecraftClient.getInstance().networkHandler != null) {
            sendToServer(SwappedHandsModeC2SPacket(swappedHands))
        }
    }

    @Environment(EnvType.CLIENT)
    fun c2sMoveItemToUtilityBelt(sourceSlot: Int) {
        sendToServer(MoveItemToUtilityBeltC2SPacket(sourceSlot))
    }

    @Environment(EnvType.CLIENT)
    fun c2sOpenInventorioScreen() {
        sendToServer(OpenInventorioScreenC2SPacket)
    }

    @Environment(EnvType.CLIENT)
    fun c2sSwapItemsInHands() {
        sendToServer(SwapItemsInHandsKeyC2SPacket)
    }

    companion object {
        @JvmStatic
        @get:JvmName("getInstance")
        @set:JvmName("setInstance")
        @Suppress("ktlint:standard:property-naming")
        lateinit var INSTANCE: InventorioNetworking
    }
}
