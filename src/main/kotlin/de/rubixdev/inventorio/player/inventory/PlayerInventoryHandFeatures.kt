package de.rubixdev.inventorio.player.inventory

import de.rubixdev.inventorio.config.GlobalSettings
import de.rubixdev.inventorio.config.PlayerSettings
import de.rubixdev.inventorio.packet.InventorioNetworking
import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import de.rubixdev.inventorio.util.isNotEmpty
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.Util

abstract class PlayerInventoryHandFeatures protected constructor(player: PlayerEntity) : PlayerInventoryInjects(player) {
    var displayToolTimeStamp = 0L
        protected set

    var displayTool = ItemStack.EMPTY!!
        set(value) {
            field = value
            displayToolTimeStamp = if (value.isNotEmpty) Util.getMeasuringTimeMs() + 1000 else 0
        }

    var swappedHands = false
        set(value) {
            field = GlobalSettings.allowSwappedHands.boolValue && value
            if (player.world.isClient) {
                InventorioNetworking.INSTANCE.c2sSetSwappedHandsMode(value)
            }
        }

    init
    {
        if (player.world.isClient) {
            swappedHands = PlayerSettings.swappedHands.boolValue
        }
    }

    /**
     * Returns an item that will be ultimately displayed in the player's main hand.
     *
     * Affected by [swappedHands]
     *
     * Unlike [getActualMainHandItem], will return [displayTool] if present
     */
    fun getDisplayedMainHandStack(): ItemStack? {
        return when {
            displayTool.isNotEmpty -> displayTool
            swappedHands -> getSelectedUtilityStack()
            else -> null
        }
    }

    /**
     * Returns an item that will be ultimately displayed in the player's offhand.
     *
     * Affected by [swappedHands]
     */
    fun getDisplayedOffHandStack(): ItemStack {
        if (!swappedHands) {
            return getSelectedUtilityStack()
        }
        return getSelectedHotbarStack()
    }

    /**
     * Returns the item selected to be placed in the main hand
     *
     * (Hotbar by default, Utility Belt with [swappedHands] enabled)
     *
     * Unlike [getDisplayedMainHandStack], it's not affected by [displayTool]
     */
    fun getActualMainHandItem(): ItemStack {
        return if (swappedHands) {
            getSelectedUtilityStack()
        } else {
            getSelectedHotbarStack()
        }
    }

    /**
     * Returns the item selected on the hotbar, regardless of [swappedHands] or [displayTool]
     */
    fun getSelectedHotbarStack(): ItemStack {
        if (PlayerInventory.isValidHotbarIndex(player.inventory.selectedSlot)) {
            return player.inventory.getStack(player.inventory.selectedSlot)
        }
        return ItemStack.EMPTY
    }

    /**
     * Returns the item selected on the utility belt, regardless of [swappedHands] or [displayTool]
     */
    fun getSelectedUtilityStack(): ItemStack {
        return utilityBelt[selectedUtility]
    }

    /**
     * Replaces the currently selected item on the hotbar, regardless of [swappedHands] or [displayTool]
     */
    fun setSelectedHotbarStack(itemStack: ItemStack) {
        if (PlayerInventory.isValidHotbarIndex(player.inventory.selectedSlot)) {
            player.inventory.setStack(player.inventory.selectedSlot, itemStack)
        }
    }

    /**
     * Replaces the currently selected item on the utility belt, regardless of [swappedHands] or [displayTool]
     */
    fun setSelectedUtilityStack(itemStack: ItemStack) {
        utilityBelt[selectedUtility] = itemStack
    }

    /**
     * Returns 3 utility belt items to display on the HUD
     */
    fun getDisplayedUtilities(): Array<ItemStack> {
        val skipEmptySlots = PlayerSettings.skipEmptyUtilitySlots.boolValue
        return arrayOf(findNextUtility(-1, skipEmptySlots).first, getSelectedUtilityStack(), findNextUtility(1, skipEmptySlots).first)
    }

    fun swapItemsInHands() {
        if (player.inventoryAddon?.displayTool?.isEmpty != true) {
            return
        }
        val offHandStack = getSelectedUtilityStack()
        setSelectedUtilityStack(getSelectedHotbarStack())
        setSelectedHotbarStack(offHandStack)
    }
}
