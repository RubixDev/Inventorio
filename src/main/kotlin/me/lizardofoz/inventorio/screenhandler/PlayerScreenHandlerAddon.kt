package me.lizardofoz.inventorio.screenhandler

import me.lizardofoz.inventorio.RobertoGarbagio
import me.lizardofoz.inventorio.mixin.accessor.PlayerScreenHandlerAccessor
import me.lizardofoz.inventorio.mixin.accessor.SlotAccessor
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.slot.DudOffhandSlot
import me.lizardofoz.inventorio.slot.ExtensionSlot
import me.lizardofoz.inventorio.slot.ToolBeltSlot
import me.lizardofoz.inventorio.slot.UtilityBeltSlot
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.SlotActionType

class PlayerScreenHandlerAddon internal constructor(val screenHandler: PlayerScreenHandler)
{
    private val playerAddon = PlayerAddon[(screenHandler as PlayerScreenHandlerAccessor).owner]

    //==============================
    //Injects. These functions are either injected or redirected to by a mixin of a [PlayerInventory] class
    //==============================

    fun initialize(playerAddon: PlayerAddon)
    {
        val inventory = playerAddon.player.inventory
        val accessor = screenHandler as PlayerScreenHandlerAccessor

        //Shofting the 2x2 Crafting Grid to its new position
        for (i in CRAFTING_GRID_RANGE)
        {
            val craftingSlot = screenHandler.slots[i] as SlotAccessor
            craftingSlot.x += CRAFTING_GRID_OFFSET_X
        }

        //We can't just REMOVE the offhand slot, but we can replace it with a dud slot that doesn't accept any items
        accessor.trackedSlots[VANILLA_OFFHAND_SLOT_INDEX] = ItemStack.EMPTY
        screenHandler.slots[VANILLA_OFFHAND_SLOT_INDEX] = DudOffhandSlot(inventory, DUD_OFFHAND_RANGE.first, -1000, -1000)

        //Extended Inventory Section (Deep Pockets Enchantment)
        for ((absolute, relative) in EXTENSION_RANGE.withRelativeIndex())
            accessor.addASlot(ExtensionSlot(inventory, absolute - HANDLER_TO_INVENTORY_OFFSET,
                    SLOT_INVENTORY_EXTENSION.x + (relative % VANILLA_ROW_LENGTH) * INVENTORY_SLOT_SIZE,
                    SLOT_INVENTORY_EXTENSION.y + (relative / VANILLA_ROW_LENGTH) * INVENTORY_SLOT_SIZE))

        //Tool Belt
        for ((absolute, relative) in TOOL_BELT_RANGE.withRelativeIndex())
            accessor.addASlot(ToolBeltSlot(inventory, SlotRestrictionFilters.toolBelt[relative], absolute - HANDLER_TO_INVENTORY_OFFSET,
                    SLOT_TOOL_BELT.x,
                    SLOT_TOOL_BELT.y +INVENTORY_SLOT_SIZE * relative))

        //Utility Belt
        for ((absolute, relative) in UTILITY_BELT_RANGE.withRelativeIndex())
            accessor.addASlot(UtilityBeltSlot(inventory, absolute - HANDLER_TO_INVENTORY_OFFSET,
                    SLOT_UTILITY_BELT_COLUMN_1.x + INVENTORY_SLOT_SIZE * (relative / 4),
                    SLOT_UTILITY_BELT_COLUMN_1.y + INVENTORY_SLOT_SIZE * (relative % 4)))
    }

    /**
     * Returns null if we want to proceed with vanilla slot behaviour.
     * Returns a value appropriate for vanilla [ScreenHandler.onSlotClick] otherwise
     */
    fun onSlotClick(slotIndex: Int, clickData: Int, actionType: SlotActionType, player: PlayerEntity): ItemStack?
    {
        if (slotIndex in ARMOR_RANGE)
        {
            checkCapacity()
            return null
        }
        else if (slotIndex in UTILITY_BELT_RANGE)
        {
            if (playerAddon.inventoryAddon.utilityBelt[playerAddon.inventoryAddon.selectedUtility].isEmpty)
                playerAddon.inventoryAddon.selectedUtility = slotIndex - UTILITY_BELT_RANGE.first
        }
        return null
    }

    //todo mod compatibility
    fun transferSlot(player: PlayerEntity, sourceIndex: Int): ItemStack
    {
        val slot = screenHandler.slots[sourceIndex]

        val itemStack = slot.stack
        val itemBefore = itemStack.copy()
        val equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemBefore)
        val expansionSlots = playerAddon.getAvailableExtensionSlotsRange()
        val accessor = screenHandler as PlayerScreenHandlerAccessor

        if (sourceIndex in MAIN_INVENTORY_RANGE || sourceIndex in expansionSlots)
        {
            if (equipmentSlot.type == EquipmentSlot.Type.ARMOR && accessor.insertAnItem(itemStack, ARMOR_RANGE.first, ARMOR_RANGE.last, false))
            {
                checkCapacity()
                return itemBefore
            }
            for ((index, function) in SlotRestrictionFilters.toolBelt.withIndex())
            {
                val absoluteIndex = TOOL_BELT_RANGE.first + index
                if (function(itemStack) && accessor.insertAnItem(itemStack, absoluteIndex, absoluteIndex + 1, false))
                    return itemBefore
            }
        }
        if (sourceIndex in MAIN_INVENTORY_RANGE)
        {
            if (!expansionSlots.isEmpty() && accessor.insertAnItem(itemStack, expansionSlots.first, expansionSlots.last, false))
                return itemBefore
        }
        else if (sourceIndex in expansionSlots)
        {
            if (accessor.insertAnItem(itemStack, MAIN_INVENTORY_RANGE.first, MAIN_INVENTORY_RANGE.last, false))
                return itemBefore
        }
        else if (sourceIndex in ARMOR_RANGE || sourceIndex in UTILITY_BELT_RANGE || sourceIndex in TOOL_BELT_RANGE)
        {
            if (accessor.insertAnItem(itemStack, MAIN_INVENTORY_RANGE.first, MAIN_INVENTORY_RANGE.last, false)
                    || (!expansionSlots.isEmpty() && accessor.insertAnItem(itemStack, expansionSlots.first, expansionSlots.last, false)))
                return itemBefore
        }
        else if (sourceIndex in CRAFTING_GRID_RANGE)
        {
            if (accessor.insertAnItem(itemStack, MAIN_INVENTORY_RANGE.first, MAIN_INVENTORY_RANGE.last, false)
                    || (!expansionSlots.isEmpty() && accessor.insertAnItem(itemStack, expansionSlots.first, expansionSlots.last, false)))
                return itemBefore
            slot.onStackChanged(itemStack, itemBefore)
        }
        return ItemStack.EMPTY
    }

    //==============================
    //Additional functionality
    //==============================

    fun checkCapacity()
    {
        val player = playerAddon.player
        val extensionRange = playerAddon.getAvailableExtensionSlotsRange()
        for (i in extensionRange)
        {
            val slot = screenHandler.getSlot(i) as ExtensionSlot
            slot.canTakeItems = true
        }
        for (i in playerAddon.getUnavailableExtensionSlotsRange())
        {
            val slot = screenHandler.getSlot(i) as ExtensionSlot
            RobertoGarbagio.LOGGER.info("drop ${slot.stack}")
            player.dropItem(slot.stack, false, true)?.setPickupDelay(0)
            slot.stack = ItemStack.EMPTY
            slot.canTakeItems = false
        }
        for (i in UTILITY_BELT_EXTENSION_RANGE)
        {
            val slot = screenHandler.getSlot(i) as ExtensionSlot
            if (extensionRange.isEmpty())
            {
                player.dropItem(slot.stack, false, true)?.setPickupDelay(0)
                slot.stack = ItemStack.EMPTY
                slot.canTakeItems = false
            }
            else
                slot.canTakeItems = true
        }
        if (!screenHandler.onServer)
            refreshSlots()
    }

    @Environment(EnvType.CLIENT)
    fun refreshSlots()
    {
        val rows = playerAddon.getExtensionRows()

        for ((absolute, relative) in MAIN_INVENTORY_NO_HOTBAR_RANGE.withRelativeIndex())
        {
            val slot = screenHandler.getSlot(absolute) as SlotAccessor
            slot.x = SLOTS_INVENTORY_MAIN(rows).x + INVENTORY_SLOT_SIZE * (relative % VANILLA_ROW_LENGTH)
            slot.y = SLOTS_INVENTORY_MAIN(rows).y + INVENTORY_SLOT_SIZE * (relative / VANILLA_ROW_LENGTH)
        }
        for ((absolute, relative) in HOTBAR_INVENTORY_RANGE.withRelativeIndex())
        {
            val slot = screenHandler.getSlot(absolute) as SlotAccessor
            slot.x = SLOTS_INVENTORY_HOTBAR(rows).x + INVENTORY_SLOT_SIZE * relative
            slot.y = SLOTS_INVENTORY_HOTBAR(rows).y
        }

        for (i in playerAddon.getAvailableExtensionSlotsRange())
        {
            val slot = screenHandler.getSlot(i) as ExtensionSlot
            slot.canTakeItems = true
        }

        for (i in playerAddon.getUnavailableExtensionSlotsRange())
        {
            val slot = screenHandler.getSlot(i) as ExtensionSlot
            slot.canTakeItems = false
        }
    }

}
