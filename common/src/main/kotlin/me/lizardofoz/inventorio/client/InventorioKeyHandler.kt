package me.lizardofoz.inventorio.client

import me.lizardofoz.inventorio.RobertoGarbagio
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.util.SegmentedHotbar
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
        return !MinecraftClient.getInstance().options.keyUse.equals(InventorioControls.INSTANCE.keyUseUtility)
    }

    /**
     * Returns true if we shall cancel the vanilla hotbar slot selection
     */
    fun handleSegmentedHotbarSlotSelection(inventory: PlayerInventory, slotToSelect: Int): Boolean
    {
        if (InventorioConfig.segmentedHotbar != SegmentedHotbar.ON)
            return false
        val addon = PlayerInventoryAddon.Client
        if (addon.selectedHotbarSection == -1)
        {
            if (slotToSelect in 0..2)
                addon.selectedHotbarSection = slotToSelect
            return true
        }
        else if (slotToSelect in 0..2)
        {
            inventory.selectedSlot = slotToSelect + 3 * addon.selectedHotbarSection
            addon.selectedHotbarSection = -1
            return true
        }
        return false
    }

    fun tick()
    {
        val client = MinecraftClient.getInstance()!!
        val options = client.options ?: return
        val player = client.player ?: return
        val hasDedicatedUseUtilityButton = hasDedicatedUseUtilityButton()

        if (InventorioControls.INSTANCE.keySwitchSegmentedHotbarMode.wasPressed())
            player.sendMessage(TranslatableText("inventorio.segmented_hotbar." + InventorioConfig.switchSegmentedHotbarMode().name), true)
        if (InventorioControls.INSTANCE.keyScrollWheelUtilityBeltMode.wasPressed())
            player.sendMessage(TranslatableText("inventorio.scroll_wheel_utility_belt." + InventorioConfig.switchScrollWheelUtilityBeltMode()), true)

        if (InventorioControls.INSTANCE.keyNextUtility.wasPressed())
            player.inventoryAddon.switchToNextUtility(1)
        if (InventorioControls.INSTANCE.keyPrevUtility.wasPressed())
            player.inventoryAddon.switchToNextUtility(-1)
        if (hasDedicatedUseUtilityButton && InventorioControls.INSTANCE.keyUseUtility.isPressed
                && (client as MinecraftClientAccessor).itemUseCooldown <= 0 && !player.isUsingItem)
            player.inventoryAddon.activateSelectedUtility()

        //This code stops the usage of the utility belt item if you have a dedicated "use utility" key
        if (PlayerInventoryAddon.Client.isUsingUtility && hasDedicatedUseUtilityButton && !InventorioControls.INSTANCE.keyUseUtility.isPressed)
            client.interactionManager?.stopUsingItem(player)

        //Shoot fireworks with Jump button
        if (options.keyJump.wasPressed() && player.isFallFlying)
            player.inventoryAddon.fireRocketFromInventory()
    }
}