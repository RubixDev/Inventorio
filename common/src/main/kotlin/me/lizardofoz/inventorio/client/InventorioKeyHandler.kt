package me.lizardofoz.inventorio.client

import me.lizardofoz.inventorio.client.config.InventorioConfig
import me.lizardofoz.inventorio.client.config.InventorioConfigScreenMenu
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.util.SegmentedHotbar
import me.lizardofoz.inventorio.util.hotbarBlackList
import me.lizardofoz.inventorio.util.toolBeltWhiteList
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
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
        val displayTool = PlayerInventoryAddon.Client.local.mainHandDisplayTool
        var useMainHand = (displayTool.isEmpty || toolBeltWhiteList.invoke(displayTool)) && !hotbarBlackList.invoke(player.getStackInHand(Hand.MAIN_HAND))
        var useOffHand = !hotbarBlackList.invoke(player.getStackInHand(Hand.OFF_HAND))

        if (hasDedicatedUseUtilityButton())
        {
            if (PlayerInventoryAddon.Client.triesToUseUtility)
                useMainHand = false
            else
                useOffHand = false
        }

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
        val addon = PlayerInventoryAddon.Client
        if (addon.selectedHotbarSection == -1)
        {
            if (slotToSelect in 0..2)
                addon.selectedHotbarSection = slotToSelect
            return true
        } else if (slotToSelect in 0..2)
        {
            inventory.selectedSlot = slotToSelect + 3 * addon.selectedHotbarSection
            addon.selectedHotbarSection = -1
            return true
        }
        return false
    }

    private fun openSettings()
    {
        MinecraftClient.getInstance().openScreen(InventorioConfigScreenMenu.get(MinecraftClient.getInstance().currentScreen))
    }

    fun hasDedicatedUseUtilityButton(): Boolean
    {
        return InventorioConfig.dedicatedUseUtilityKey && !MinecraftClient.getInstance().options.keyUse.equals(InventorioControls.keyUseUtility)
    }

    fun tick()
    {
        val client = MinecraftClient.getInstance()!!
        val options = client.options ?: return
        val player = client.player ?: return
        val inventoryAddon = player.inventoryAddon ?: return
        val hasDedicatedUseUtilityButton = hasDedicatedUseUtilityButton()

        //Fabric fix: if you bind the same key to multiple things, Fabric tracks only one of them
        //In our case, keyUseUtility overshadows the vanilla keyUse, and this is how we fix it
        if (!hasDedicatedUseUtilityButton)
        {
            if (client.options.keyUse.equals(InventorioControls.keyUseUtility))
                client.options.keyUse.isPressed = InventorioControls.keyUseUtility.isPressed
        }

        if (InventorioControls.keyNextUtility.wasPressed())
            inventoryAddon.switchToNextUtility(1)
        if (InventorioControls.keyPrevUtility.wasPressed())
            inventoryAddon.switchToNextUtility(-1)
        if (hasDedicatedUseUtilityButton && InventorioControls.keyUseUtility.isPressed && !player.isUsingItem && (client as MinecraftClientAccessor).itemUseCooldown <= 0)
            inventoryAddon.activateSelectedUtility()

        //This code stops the usage of the utility belt item if you have a dedicated "use utility" key
        if (PlayerInventoryAddon.Client.isUsingUtility && hasDedicatedUseUtilityButton && !InventorioControls.keyUseUtility.isPressed)
            client.interactionManager?.stopUsingItem(player)

        //Shoot fireworks with Jump button
        if (InventorioConfig.jumpToRocketBoost && options.keyJump.wasPressed() && player.isFallFlying)
            inventoryAddon.fireRocketFromInventory()

        //Settings. If Cloth Config mod is present, then we don't need to handle various "setting toggle" buttons
        if (!InventorioControls.optionToggleKeysEnabled)
        {
            if (InventorioControls.keyOpenSettingsMenu.wasPressed())
                openSettings()
        }
        else
        {
            if (InventorioControls.keySwitchSegmentedHotbarMode.wasPressed())
                player.sendMessage(TranslatableText("inventorio.settings_key.segmented_hotbar." + InventorioConfig.switchSegmentedHotbarMode().name), true)
            if (InventorioControls.keySwitchJumpToRocketBoostMode.wasPressed())
                player.sendMessage( TranslatableText("inventorio.settings_key.jump_rocket_boost_mode." + InventorioConfig.switchJumpToRocketBoost()), true)
            if (InventorioControls.keyScrollWheelUtilityBeltMode.wasPressed())
                player.sendMessage(TranslatableText("inventorio.settings_key.scroll_wheel_utility_belt." + InventorioConfig.switchScrollWheelUtilityBeltMode()), true)
            if (InventorioControls.keySwitchCanThrowUnloyalTrident.wasPressed())
                player.sendMessage(TranslatableText("inventorio.settings_key.can_throw_unloyal_trident." + InventorioConfig.switchCanThrowUnloyalTrident()), true)
        }
    }
}