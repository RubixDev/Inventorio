package me.lizardofoz.inventorio.player

import me.lizardofoz.inventorio.client.ui.PlayerInventoryUIAddon
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.player.inventory.PlayerInventoryExtraStuff
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity

/**
 * This class is responsible for the inventory addon itself,
 * while [PlayerInventoryUIAddon] is responsible for the visuals of the Player Screen UI
 * and [PlayerScreenHandlerAddon] is responsible for the slots and player interacting with the slots
 */
class PlayerInventoryAddon internal constructor(player: PlayerEntity) : PlayerInventoryExtraStuff(player)
{
    @Environment(EnvType.CLIENT)
    object Client
    {
        val local get() = MinecraftClient.getInstance().player?.inventoryAddon
        @JvmField var selectedHotbarSection = -1
        @JvmField var triesToUseUtility = false
        @JvmField var isUsingUtility = false

        fun activateSelectedUtility()
        {
            triesToUseUtility = true
            isUsingUtility = true
            (MinecraftClient.getInstance() as MinecraftClientAccessor).invokeDoItemUse()
            triesToUseUtility = false
        }
    }

    companion object
    {
        @JvmStatic
        val PlayerEntity.inventoryAddon: PlayerInventoryAddon?
            get() = (this.inventory as InventoryDuck).inventorioAddon
    }
}