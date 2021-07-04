package me.lizardofoz.inventorio.client.control

import me.lizardofoz.inventorio.client.configscreen.GlobalSettingsScreen
import me.lizardofoz.inventorio.client.configscreen.PlayerSettingsScreen
import me.lizardofoz.inventorio.config.PlayerSettings
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.util.ScrollWheelUtilityBeltMode
import me.lizardofoz.inventorio.util.SegmentedHotbar
import me.lizardofoz.inventorio.util.canRMBItem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand

@Environment(EnvType.CLIENT)
object InventorioKeyHandler
{
    fun handleItemUsage(player: PlayerEntity): Array<Hand>
    {
        //Some items which can be used as a tool (e.g. trident) would REPLACE a selected hotbar stack upon use.
        //But something like VANILLA axes and shovels work fine.
        //Some modded stuff breaks it tho.
        var useMainHand = canRMBItem(player.getStackInHand(Hand.MAIN_HAND))
        var useOffHand = canRMBItem(player.getStackInHand(Hand.OFF_HAND))

        if (PlayerInventoryAddon.Client.triesToUseUtility)
            useMainHand = false
        else if (!PlayerSettings.useItemAppliesToOffhand.boolValue && !InventorioControls.keyUseUtility.isUnbound)
            useOffHand = false

        if (useMainHand)
            player.inventoryAddon?.displayTool = ItemStack.EMPTY

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
        if (PlayerSettings.segmentedHotbar.value != SegmentedHotbar.ON)
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

    /**
     * Returns [true] if vanilla hotbar scrolling has to be cancelled.
     */
    fun scrollInHotbar(player: PlayerEntity, scrollAmount: Double): Boolean
    {
        val scrollMode = PlayerSettings.scrollWheelUtilityBelt.value as ScrollWheelUtilityBeltMode
        if (scrollMode != ScrollWheelUtilityBeltMode.OFF)
        {
            val realScrollAmount = if (scrollMode == ScrollWheelUtilityBeltMode.REGULAR) -scrollAmount.toInt() else scrollAmount.toInt()
            player.inventoryAddon?.switchToNextUtility(realScrollAmount, PlayerSettings.skipEmptyUtilitySlots.boolValue)
            return true
        }
        else
            PlayerInventoryAddon.Client.selectedHotbarSection = -1
        return false
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
            inventoryAddon.switchToNextUtility(1, PlayerSettings.skipEmptyUtilitySlots.boolValue)
        if (InventorioControls.keyPrevUtility.wasPressed())
            inventoryAddon.switchToNextUtility(-1, PlayerSettings.skipEmptyUtilitySlots.boolValue)
        if (InventorioControls.keyEmptyUtility.wasPressed())
            inventoryAddon.switchToEmptyUtility(1)

        if (InventorioControls.keyOpenPlayerSettingsMenu.wasPressed())
            client.openScreen(PlayerSettingsScreen.get(client.currentScreen))
        if (InventorioControls.keyOpenGlobalSettingsMenu.wasPressed())
            client.openScreen(GlobalSettingsScreen.get(client.currentScreen))
    }
}