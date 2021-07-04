package me.lizardofoz.inventorio.packet

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

interface InventorioNetworking
{
    fun s2cSelectUtilitySlot(player: ServerPlayerEntity)

    fun s2cGlobalSettings(player: ServerPlayerEntity)

    fun s2cUpdateAddonStacks(player: ServerPlayerEntity, updatedStacks: Map<Int, ItemStack>)

    @Environment(EnvType.CLIENT)
    fun c2sSelectUtilitySlot(selectedUtility: Int)

    @Environment(EnvType.CLIENT)
    fun c2sUseBoostRocket()

    @Environment(EnvType.CLIENT)
    fun c2sSetSwappedHands(swappedHands: Boolean)

    @Environment(EnvType.CLIENT)
    fun c2sMoveItemToUtilityBelt(sourceSlot: Int)

    @Environment(EnvType.CLIENT)
    fun c2sOpenInventorioScreen()

    companion object
    {
        @JvmStatic
        @get:JvmName("getInstance")
        @set:JvmName("setInstance")
        lateinit var INSTANCE: InventorioNetworking
    }
}