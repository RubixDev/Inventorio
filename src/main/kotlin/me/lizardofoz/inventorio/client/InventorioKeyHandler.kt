package me.lizardofoz.inventorio.client

import me.lizardofoz.inventorio.client.InventorioControls.keyNextUtility
import me.lizardofoz.inventorio.client.InventorioControls.keyPrevUtility
import me.lizardofoz.inventorio.client.InventorioControls.keyScrollSimplifiedMode
import me.lizardofoz.inventorio.client.InventorioControls.keyUseUtility
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.util.HotBarSimplified
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.TranslatableText

@Environment(EnvType.CLIENT)
object InventorioKeyHandler
{
    fun hasDedicatedUseUtilityButton() : Boolean
    {
        return !MinecraftClient.getInstance().options.keyUse.equals(keyUseUtility)
    }

    fun handleSlotSelection(inventory: PlayerInventory, selectedSlot: Int)
    {
        if (InventorioConfigData.simplifiedHotBar != HotBarSimplified.ON)
        {
            inventory.selectedSlot = selectedSlot
        }
        else if (PlayerInventoryAddon.Client.selectedHotBarSection == -1)
        {
            if (selectedSlot in 0..2)
                PlayerInventoryAddon.Client.selectedHotBarSection = selectedSlot
        }
        else if (selectedSlot in 0..2)
        {
            inventory.selectedSlot = selectedSlot + 3 * PlayerInventoryAddon.Client.selectedHotBarSection
            PlayerInventoryAddon.Client.selectedHotBarSection = -1
        }
    }

    fun tick(client: MinecraftClient)
    {
        val options = client.options ?: return
        val player = client.player ?: return

        if (keyScrollSimplifiedMode.wasPressed())
            player.sendMessage(TranslatableText("inventorio.simplified_hotbar."+ InventorioConfigData.scrollSimplifiedHotBar().name),true)

        if (keyNextUtility.wasPressed())
            player.inventoryAddon.switchToNextUtility(1)
        if (keyPrevUtility.wasPressed())
            player.inventoryAddon.switchToNextUtility(-1)
        if (hasDedicatedUseUtilityButton() && keyUseUtility.isPressed && (client as MinecraftClientAccessor).itemUseCooldown <= 0)
            player.inventoryAddon.activateSelectedUtility()

        //Shoot fireworks with Jump button
        if (options.keyJump.wasPressed() && player.isFallFlying)
            player.inventoryAddon.fireRocketFromInventory()
    }
}