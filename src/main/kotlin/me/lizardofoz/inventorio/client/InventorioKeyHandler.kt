package me.lizardofoz.inventorio.client

import me.lizardofoz.inventorio.RobertoGarbagio
import me.lizardofoz.inventorio.client.InventorioControls.keyNextUtility
import me.lizardofoz.inventorio.client.InventorioControls.keyOpenConfig
import me.lizardofoz.inventorio.client.InventorioControls.keyPrevUtility
import me.lizardofoz.inventorio.client.InventorioControls.keyQuickBar10
import me.lizardofoz.inventorio.client.InventorioControls.keyQuickBar11
import me.lizardofoz.inventorio.client.InventorioControls.keyQuickBar12
import me.lizardofoz.inventorio.client.InventorioControls.keyUseUtility
import me.lizardofoz.inventorio.client.config.InventorioConfigData
import me.lizardofoz.inventorio.client.config.InventorioConfigScreenMenu
import me.lizardofoz.inventorio.client.config.QuickBarStorage
import me.lizardofoz.inventorio.mixin.client.accessor.HandledScreenAccessor
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.util.QUICK_BAR_RANGE
import me.lizardofoz.inventorio.util.QuickBarSimplified
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import net.minecraft.world.level.LevelProperties

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

    fun handleHotbarKeyPressInUI(screen: HandledScreen<*>, keyCode: Int, scanCode: Int): Boolean
    {
        val client = MinecraftClient.getInstance()!!
        val player = client.player ?: return false
        val focusedSlot = (screen as HandledScreenAccessor).focusedSlot
        if (player.inventory.cursorStack.isEmpty && focusedSlot != null)
        {
            for (index in 0..8)
                if (client.options.keysHotbar[index].matchesKey(keyCode, scanCode))
                {
                    screen.doOnMouseClick(focusedSlot, focusedSlot.id, index + QUICK_BAR_RANGE.first, SlotActionType.SWAP)
                    return true
                }
            for (index in 0..2)
                if (InventorioControls.keysQuickBarExtra[index].matchesKey(keyCode, scanCode))
                {
                    screen.doOnMouseClick(focusedSlot, focusedSlot.id, index + 9 + QUICK_BAR_RANGE.first, SlotActionType.SWAP)
                    return true
                }
        }
        return false
    }

    fun tick(client: MinecraftClient)
    {
        val options = client.options ?: return
        val player = client.player ?: return

        if (keyOpenConfig.wasPressed())
        {
            val name = if (client.server != null)
                (client.server!!.worlds.first().levelProperties as LevelProperties).levelName
            else
                client.networkHandler?.connection?.address
            RobertoGarbagio.LOGGER.info("name=$name")
            client.openScreen(InventorioConfigScreenMenu.get(null))
            InventoryOffsets.init()
        }

        if (keyNextUtility.wasPressed())
            PlayerAddon[player].inventoryAddon.switchToNextUtility(1)
        if (keyPrevUtility.wasPressed())
            PlayerAddon[player].inventoryAddon.switchToNextUtility(-1)
        if (hasDedicatedUseUtilityButton() && keyUseUtility.isPressed && (client as MinecraftClientAccessor).itemUseCooldown <= 0)
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
                    QuickBarStorage.global.setSavedQuickBar(quickBarIndex, PlayerAddon[player].inventoryAddon.shortcutQuickBar.stacks)
                    client.inGameHud.setOverlayMessage(Text.of("saved at $quickBarIndex"), false)
                }
        }
        else if (options.keyLoadToolbarActivator.isPressed)
        {
            for (quickBarIndex in options.keysHotbar.indices)
                if (options.keysHotbar[quickBarIndex].wasPressed())
                {
                    RobertoGarbagio.LOGGER.info("loaded from $quickBarIndex")
                    val bar = QuickBarStorage.global.getSavedQuickBar(quickBarIndex)
                    PlayerAddon[player].setQuickBar(bar)
                    client.inGameHud.setOverlayMessage(Text.of("loaded from $quickBarIndex"), false)
                }
        }
    }
}