package me.danetnaverno.inventorio.client

import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.client.InventorioControls.keyNextUtility
import me.danetnaverno.inventorio.client.InventorioControls.keyOpenConfig
import me.danetnaverno.inventorio.client.InventorioControls.keyPrevUtility
import me.danetnaverno.inventorio.client.InventorioControls.keyQuickBar10
import me.danetnaverno.inventorio.client.InventorioControls.keyQuickBar11
import me.danetnaverno.inventorio.client.InventorioControls.keyQuickBar12
import me.danetnaverno.inventorio.client.InventorioControls.keyUseUtility
import me.danetnaverno.inventorio.client.config.InventorioConfigData
import me.danetnaverno.inventorio.client.config.InventorioConfigScreenMenu
import me.danetnaverno.inventorio.client.config.QuickBarStorage
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.util.QuickBarSimplified
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
object InventorioKeyHandler
{
    fun hasDedicatedUseUtilityButton() : Boolean
    {
        return !MinecraftClient.getInstance().options.keyUse.equals(keyUseUtility)
    }

    fun handleInputEvents(inventory: PlayerInventory, selectedSlot: Int)
    {
        if (InventorioConfigData.config().quickBarSimplifiedGlobal != QuickBarSimplified.ON)
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
            client.openScreen(InventorioConfigScreenMenu.get(null))

        if (keyNextUtility.wasPressed())
            PlayerAddon[player].inventoryAddon.switchToNextUtility(1)
        if (keyPrevUtility.wasPressed())
            PlayerAddon[player].inventoryAddon.switchToNextUtility(-1)
        if (hasDedicatedUseUtilityButton() && keyUseUtility.isPressed)
            PlayerAddon[player].inventoryAddon.activateSelectedUtility()

        if (keyQuickBar10.wasPressed())
            player.inventory?.selectedSlot = 9
        if (keyQuickBar11.wasPressed())
            player.inventory?.selectedSlot = 10
        if (keyQuickBar12.wasPressed())
            player.inventory?.selectedSlot = 11

        //Shoot fireworks with Jump button
        if (options.keyJump.wasPressed() && player.isFallFlying)
            PlayerAddon[player].fireRocketFromInventory()

        //todo костыль
        if (options.keySaveToolbarActivator.isPressed)
        {
            for (quickBarIndex in options.keysHotbar.indices)
                if (options.keysHotbar[quickBarIndex].wasPressed()) //todo wasPressed would be better but it doesn't work here
                {
                    RobertoGarbagio.LOGGER.info("saving in $quickBarIndex")
                    QuickBarStorage.default.setSavedQuickBar(quickBarIndex, PlayerAddon[player].inventoryAddon.shortcutQuickBar.stacks)
                    client.inGameHud.setOverlayMessage(Text.of("saved at $quickBarIndex"), false)
                }
        }
        else if (options.keyLoadToolbarActivator.isPressed)
        {
            for (quickBarIndex in options.keysHotbar.indices)
                if (options.keysHotbar[quickBarIndex].wasPressed())
                {
                    RobertoGarbagio.LOGGER.info("loaded from $quickBarIndex")
                    val bar = QuickBarStorage.default.getSavedQuickBar(quickBarIndex)
                    PlayerAddon[player].setQuickBar(bar)
                    client.inGameHud.setOverlayMessage(Text.of("loaded from $quickBarIndex"), false)
                }
        }
    }
}