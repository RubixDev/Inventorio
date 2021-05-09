package me.lizardofoz.inventorio.client

import me.lizardofoz.inventorio.client.InventorioControls.keyNextUtility
import me.lizardofoz.inventorio.client.InventorioControls.keyPrevUtility
import me.lizardofoz.inventorio.client.InventorioControls.keyScrollWheelUtilityBeltMode
import me.lizardofoz.inventorio.client.InventorioControls.keySwitchSegmentedHotbarMode
import me.lizardofoz.inventorio.client.InventorioControls.keyUseUtility
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.TranslatableText

@Environment(EnvType.CLIENT)
object InventorioKeyHandler
{
    fun hasDedicatedUseUtilityButton(): Boolean
    {
        return !MinecraftClient.getInstance().options.keyUse.equals(keyUseUtility)
    }

    fun handleSegmentedHotbarSlotSelection(inventory: PlayerInventory, slotToSelect: Int)
    {
        if (PlayerInventoryAddon.Client.selectedHotbarSection == -1)
        {
            if (slotToSelect in 0..2)
                PlayerInventoryAddon.Client.selectedHotbarSection = slotToSelect
        }
        else if (slotToSelect in 0..2)
        {
            inventory.selectedSlot = slotToSelect + 3 * PlayerInventoryAddon.Client.selectedHotbarSection
            PlayerInventoryAddon.Client.selectedHotbarSection = -1
        }
    }

    fun tick(client: MinecraftClient)
    {
        val options = client.options ?: return
        val player = client.player ?: return

        if (keySwitchSegmentedHotbarMode.wasPressed())
            player.sendMessage(TranslatableText("inventorio.segmented_hotbar." + InventorioConfigData.switchSegmentedHotbarMode().name), true)
        if (keyScrollWheelUtilityBeltMode.wasPressed())
            player.sendMessage(TranslatableText("inventorio.scroll_wheel_utility_belt." + InventorioConfigData.switchScrollWheelUtilityBeltMode()), true)

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