package me.lizardofoz.inventorio.client

import me.lizardofoz.inventorio.client.config.InventorioConfig
import me.lizardofoz.inventorio.client.config.InventorioConfigScreenMenu
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.util.SegmentedHotbar
import me.lizardofoz.inventorio.util.usageHotbarBlackList
import me.lizardofoz.inventorio.util.usageDisplayToolWhiteList
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand

@Environment(EnvType.CLIENT)
object InventorioKeyHandler
{
    fun handleItemUsage(player: PlayerEntity): Array<Hand>
    {
        //Some items which can be used as a tool (e.g. trident) would REPLACE a selected hotbar stack upon use.
        //But something like VANILLA axes and shovels work fine.
        //Some modded stuff breaks it tho.
        val displayTool = player.inventoryAddon?.displayTool ?: ItemStack.EMPTY
        var useMainHand = (displayTool.isEmpty || usageDisplayToolWhiteList.invoke(displayTool)) && !usageHotbarBlackList.invoke(player.getStackInHand(Hand.MAIN_HAND))
        var useOffHand = !usageHotbarBlackList.invoke(player.getStackInHand(Hand.OFF_HAND))

        if (PlayerInventoryAddon.Client.triesToUseUtility)
            useMainHand = false
        else if (!InventorioConfig.useItemAppliesToOffhand && !InventorioControls.keyUseUtility.isUnbound)
            useOffHand = false

        return if (useMainHand && useOffHand)
            arrayOf(Hand.MAIN_HAND, Hand.OFF_HAND)
        else if (useMainHand)
            arrayOf(Hand.MAIN_HAND)
        else if (useOffHand)
            arrayOf(Hand.OFF_HAND)
        else
            arrayOf()
    }

    /**
     * Returns true if we shall cancel the vanilla hotbar slot selection
     */
    fun handleSegmentedHotbarSlotSelection(inventory: PlayerInventory, slotToSelect: Int): Boolean
    {
        if (InventorioConfig.segmentedHotbar != SegmentedHotbar.ON)
            return false
        if (slotToSelect > 2)
            return true
        val addon = PlayerInventoryAddon.Client
        if (addon.selectedHotbarSection == -1)
            addon.selectedHotbarSection = slotToSelect
        else
        {
            inventory.selectedSlot = slotToSelect + 3 * addon.selectedHotbarSection
            addon.selectedHotbarSection = -1
        }
        return true
    }

    fun tick()
    {
        val client = MinecraftClient.getInstance()
        val player = client.player ?: return
        val inventoryAddon = player.inventoryAddon ?: return

        if ((client as MinecraftClientAccessor).itemUseCooldown <= 0)
        {
            if (InventorioControls.keyUseUtility.isPressed && !player.isUsingItem)
                PlayerInventoryAddon.Client.activateSelectedUtility()
            if (player.isFallFlying && InventorioControls.keyFireBoostRocket.isThisOrVanillaPressed)
                inventoryAddon.fireRocketFromInventory()
        }
        //This code handles finishing the usage of the utility belt item if you have a dedicated "use utility" key
        if (!InventorioControls.keyUseUtility.isPressed && PlayerInventoryAddon.Client.isUsingUtility)
            client.interactionManager?.stopUsingItem(player)

        //Scroll through the Utility Belt
        if (InventorioControls.keyNextUtility.wasPressed())
            inventoryAddon.switchToNextUtility(1)
        if (InventorioControls.keyPrevUtility.wasPressed())
            inventoryAddon.switchToNextUtility(-1)

        //Settings. If Cloth Config mod is present, then we don't need to handle various "setting toggle" buttons
        if (!InventorioControls.optionToggleKeysEnabled)
        {
            if (InventorioControls.keyOpenSettingsMenu.wasPressed())
                client.openScreen(InventorioConfigScreenMenu.get(client.currentScreen))
        }
        else
        {
            if (InventorioControls.keyOptionToggleSegmentedHotbar.wasPressed())
                player.sendMessage(TranslatableText("inventorio.option_toggle_key.segmented_hotbar." + InventorioConfig.toggleSegmentedHotbarMode().name), true)
            if (InventorioControls.keyOptionToggleScrollWheelUtilityBelt.wasPressed())
                player.sendMessage(TranslatableText("inventorio.option_toggle_key.scroll_wheel_utility_belt." + InventorioConfig.toggleScrollWheelUtilityBeltMode()), true)
            if (InventorioControls.keyOptionToggleCanThrowUnloyalTrident.wasPressed())
                player.sendMessage(TranslatableText("inventorio.option_toggle_key.can_throw_unloyal_trident." + InventorioConfig.toggleCanThrowUnloyalTrident()), true)
            if (InventorioControls.keyOptionToggleUseItemAppliesToOffhand.wasPressed())
                player.sendMessage(TranslatableText("inventorio.option_toggle_key.use_item_applies_to_offhand." + InventorioConfig.toggleUseItemAppliesToOffhand()), true)
            if (InventorioControls.keyOptionToggleSwappedHands.wasPressed())
                player.sendMessage(TranslatableText("inventorio.option_toggle_key.swapped_hands." + InventorioConfig.toggleSwappedHands()), true)
        }
    }
}