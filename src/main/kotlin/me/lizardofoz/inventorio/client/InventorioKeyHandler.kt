package me.lizardofoz.inventorio.client

import me.lizardofoz.inventorio.client.InventorioControls.keyNextUtility
import me.lizardofoz.inventorio.client.InventorioControls.keyOpenConfig
import me.lizardofoz.inventorio.client.InventorioControls.keyPrevUtility
import me.lizardofoz.inventorio.client.InventorioControls.keyUseUtility
import me.lizardofoz.inventorio.client.config.InventorioConfigData
import me.lizardofoz.inventorio.client.config.InventorioConfigScreenMenu
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.util.QuickBarSimplified
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerInventory

@Environment(EnvType.CLIENT)
object InventorioKeyHandler
{
    fun hasDedicatedUseUtilityButton() : Boolean
    {
        return !MinecraftClient.getInstance().options.keyUse.equals(keyUseUtility)
    }

    fun handleSlotSelection(inventory: PlayerInventory, selectedSlot: Int)
    {
        if (InventorioConfigData.config().quickBarSimplified != QuickBarSimplified.ON)
        {
            inventory.selectedSlot = selectedSlot
        }
        else if (PlayerAddon.Client.selectedQuickBarSection == -1)
        {
            if (selectedSlot in 0..2)
                PlayerAddon.Client.selectedQuickBarSection = selectedSlot
        }
        else if (selectedSlot in 0..3)
        {
            inventory.selectedSlot = selectedSlot + 4 * PlayerAddon.Client.selectedQuickBarSection
            PlayerAddon.Client.selectedQuickBarSection = -1
        }
    }

    fun tick(client: MinecraftClient)
    {
        val options = client.options ?: return
        val player = client.player ?: return

        if (keyOpenConfig.wasPressed())
        {
            client.openScreen(InventorioConfigScreenMenu.get(null))
        }

        if (keyNextUtility.wasPressed())
            PlayerAddon[player].inventoryAddon.switchToNextUtility(1)
        if (keyPrevUtility.wasPressed())
            PlayerAddon[player].inventoryAddon.switchToNextUtility(-1)
        if (hasDedicatedUseUtilityButton() && keyUseUtility.isPressed && (client as MinecraftClientAccessor).itemUseCooldown <= 0)
            PlayerAddon[player].inventoryAddon.activateSelectedUtility()

        //Shoot fireworks with Jump button
        if (options.keyJump.wasPressed() && player.isFallFlying)
            PlayerAddon[player].fireRocketFromInventory()
    }
}