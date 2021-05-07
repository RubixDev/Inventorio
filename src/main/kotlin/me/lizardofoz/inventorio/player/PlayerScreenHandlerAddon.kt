package me.lizardofoz.inventorio.player

import me.lizardofoz.inventorio.mixin.accessor.ScreenHandlerAccessor
import me.lizardofoz.inventorio.mixin.accessor.SlotAccessor
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.slot.DudOffhandSlot
import me.lizardofoz.inventorio.slot.ExtensionSlot
import me.lizardofoz.inventorio.slot.ToolBeltSlot
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
    private lateinit var inventoryAddon : PlayerInventoryAddon

    //==============================
    //Injects. These functions are either injected or redirected to by a mixin of a [PlayerInventory] class
    //==============================

    fun initialize(player: PlayerEntity)
    {
        inventoryAddon = player.inventoryAddon
        val screenHandlerAccessor = screenHandler as ScreenHandlerAccessor
        val rows = inventoryAddon.getExtensionRows()

        //Shifting the 2x2 Crafting Grid to its new position
        for (i in HANDLER_CRAFTING_GRID_RANGE)
            (screenHandler.slots[i] as SlotAccessor).x += CRAFTING_GRID_OFFSET_X

        //We can't just REMOVE the offhand slot, but we can replace it with a dud slot that doesn't accept any items
        screenHandlerAccessor.trackedSlots[HANDLER_DUD_OFFHAND_RANGE.first] = ItemStack.EMPTY
        screenHandler.slots[HANDLER_DUD_OFFHAND_RANGE.first] = DudOffhandSlot(player.inventory, HANDLER_DUD_OFFHAND_RANGE.first, -1000, -1000)

        //Extended Inventory Section (Deep Pockets Enchantment)
        for ((absolute, relative) in INVENTORY_ADDON_DEEP_POCKETS_RANGE.withRelativeIndex())
            screenHandlerAccessor.addASlot(ExtensionSlot(inventoryAddon, absolute,
                    SLOT_INVENTORY_EXTENSION.x + (relative % VANILLA_ROW_LENGTH) * SLOT_UI_SIZE,
                    SLOT_INVENTORY_EXTENSION.y + (relative / VANILLA_ROW_LENGTH) * SLOT_UI_SIZE))

        //Tool Belt
        for ((absolute, relative) in INVENTORY_ADDON_TOOL_BELT_RANGE.withRelativeIndex())
            screenHandlerAccessor.addASlot(ToolBeltSlot(inventoryAddon, toolBelt[relative], absolute,
                    SLOT_TOOL_BELT(rows).x,
                    SLOT_TOOL_BELT(rows).y + SLOT_UI_SIZE * relative))

        //Utility Belt
        for ((absolute, relative) in INVENTORY_ADDON_UTILITY_BELT_RANGE.withRelativeIndex())
            screenHandlerAccessor.addASlot(ExtensionSlot(inventoryAddon, absolute,
                    SLOT_UTILITY_BELT_COLUMN_1.x + SLOT_UI_SIZE * (relative / 4),
                    SLOT_UTILITY_BELT_COLUMN_1.y + SLOT_UI_SIZE * (relative % 4)))
    }

    /**
     * Returns null if we want to proceed with vanilla behaviour.
     * Returns a value appropriate for vanilla [ScreenHandler.onSlotClick] otherwise
     */
    fun onSlotClick(slotIndex: Int, clickData: Int, actionType: SlotActionType, player: PlayerEntity): ItemStack?
    {
        if (slotIndex in HANDLER_ARMOR_RANGE)
        {
            checkDeepPocketsCapacity()
            return null
        }
        else if (slotIndex in HANDLER_UTILITY_BELT_RANGE)
        {
            if (inventoryAddon.getSelectedUtilityStack().isEmpty)
                inventoryAddon.selectedUtility = slotIndex - HANDLER_UTILITY_BELT_RANGE.first
        }
        return null
    }

    /**
     * Returns null if we want to proceed with vanilla behaviour.
     * Returns a value appropriate for vanilla [ScreenHandler.transferSlot] otherwise
     */
    fun transferSlot(sourceIndex: Int): ItemStack?
    {
        val sourceSlot = screenHandler.slots[sourceIndex]
        val itemStackDynamic = sourceSlot.stack
        val itemStackStaticCopy = itemStackDynamic.copy()
        val preferredEquipSlotType = MobEntity.getPreferredEquipmentSlot(itemStackStaticCopy)
        val deepPocketsSlots = getAvailableDeepPocketsRange()
        val screenHandlerAccessor = screenHandler as ScreenHandlerAccessor

        //When we shift-click an item that's in the main inventory or deep pockets
        if (sourceIndex in HANDLER_MAIN_INVENTORY_RANGE || sourceIndex in deepPocketsSlots)
        {
            //Try to send an item into the armor slots
            if (preferredEquipSlotType.type == EquipmentSlot.Type.ARMOR && screenHandlerAccessor.insertAnItem(itemStackDynamic, HANDLER_ARMOR_RANGE.first, HANDLER_ARMOR_RANGE.last, false))
            {
                checkDeepPocketsCapacity()
                return itemStackStaticCopy
            }
            //Try to send an item into the toolbelt
            for ((index, predicate) in toolBelt.withIndex())
            {
                val absoluteIndex = HANDLER_TOOL_BELT_RANGE.first + index
                //Yes, we ultimately invoke this predicate twice (here and within the ToolBeltSlot),
                //  but have you seen the body of ScreenHandler#insertItem method?
                if (predicate(itemStackDynamic) && screenHandlerAccessor.insertAnItem(itemStackDynamic, absoluteIndex, absoluteIndex + 1, false))
                    return itemStackStaticCopy
            }
        }
        //If we're here, an item can't be moved to neither tool belt nor armor slots


        //When we shift-click an item that's in the main inventory, we try to move it into deep pockets
        if (sourceIndex in HANDLER_MAIN_INVENTORY_RANGE)
        {
            if (!deepPocketsSlots.isEmpty() && screenHandlerAccessor.insertAnItem(itemStackDynamic, deepPocketsSlots.first, deepPocketsSlots.last, false))
                return itemStackStaticCopy
        }
        //When we shift-click an item that's in the deep pockets, we try to move it into the main inventory
        else if (sourceIndex in deepPocketsSlots)
        {
            if (screenHandlerAccessor.insertAnItem(itemStackDynamic, HANDLER_MAIN_INVENTORY_RANGE.first, HANDLER_MAIN_INVENTORY_RANGE.last, false))
                return itemStackStaticCopy
        }
        //When we shift-click an item in armor slots, tool belt or utility belt, try to move it into the main inventory
        //If the main inventory is full, move it into the deep pockets
        else if (sourceIndex in HANDLER_ARMOR_RANGE || sourceIndex in HANDLER_UTILITY_BELT_RANGE || sourceIndex in HANDLER_TOOL_BELT_RANGE)
        {
            if (screenHandlerAccessor.insertAnItem(itemStackDynamic, HANDLER_MAIN_INVENTORY_RANGE.first, HANDLER_MAIN_INVENTORY_RANGE.last, false)
                    || (!deepPocketsSlots.isEmpty() && screenHandlerAccessor.insertAnItem(itemStackDynamic, deepPocketsSlots.first, deepPocketsSlots.last, false)))
                return itemStackStaticCopy
        }
        //Same with the crafting grid, but we also need to call onStackChanged or else the crafting grid will bug out
        else if (sourceIndex in HANDLER_CRAFTING_GRID_RANGE)
        {
            if (screenHandlerAccessor.insertAnItem(itemStackDynamic, HANDLER_MAIN_INVENTORY_RANGE.first, HANDLER_MAIN_INVENTORY_RANGE.last, false)
                    || (!deepPocketsSlots.isEmpty() && screenHandlerAccessor.insertAnItem(itemStackDynamic, deepPocketsSlots.first, deepPocketsSlots.last, false)))
                return itemStackStaticCopy
            sourceSlot.onStackChanged(itemStackDynamic, itemStackStaticCopy)
        }
        return null
    }

    //==============================
    //Additional functionality
    //==============================

    fun checkDeepPocketsCapacity()
    {
        val player = inventoryAddon.player
        val extensionRange = getAvailableDeepPocketsRange()
        for (i in extensionRange)
        {
            val slot = screenHandler.getSlot(i) as ExtensionSlot
            slot.canTakeItems = true
        }
        for (i in getUnavailableDeepPocketsRange())
        {
            val slot = screenHandler.getSlot(i) as ExtensionSlot
            if (slot.stack.isNotEmpty)
            {
                player.dropItem(slot.stack, false, true)?.setPickupDelay(0)
                slot.stack = ItemStack.EMPTY
            }
            slot.canTakeItems = false
        }
        for (i in HANDLER_UTILITY_BELT_EXTENSION_RANGE)
        {
            val slot = screenHandler.getSlot(i) as ExtensionSlot
            if (extensionRange.isEmpty()) //If we don't have Deep Pockets on us, we need to drop items within the extended utility belt
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
        val rows = inventoryAddon.getExtensionRows()

        for ((absolute, relative) in HANDLER_MAIN_WITHOUT_HOTBAR_RANGE.withRelativeIndex())
        {
            val slot = screenHandler.getSlot(absolute) as SlotAccessor
            slot.x = SLOTS_INVENTORY_MAIN(rows).x + SLOT_UI_SIZE * (relative % VANILLA_ROW_LENGTH)
            slot.y = SLOTS_INVENTORY_MAIN(rows).y + SLOT_UI_SIZE * (relative / VANILLA_ROW_LENGTH)
        }
        for ((absolute, relative) in HANDLER_HOTBAR_RANGE.withRelativeIndex())
        {
            val slot = screenHandler.getSlot(absolute) as SlotAccessor
            slot.x = SLOTS_INVENTORY_HOTBAR(rows).x + SLOT_UI_SIZE * relative
            slot.y = SLOTS_INVENTORY_HOTBAR(rows).y
        }
        for ((absolute, relative) in HANDLER_TOOL_BELT_RANGE.withRelativeIndex())
        {
            val slot = screenHandler.getSlot(absolute) as SlotAccessor
            slot.x = SLOT_TOOL_BELT(rows).x
            slot.y = SLOT_TOOL_BELT(rows).y + SLOT_UI_SIZE * relative
        }
        for (i in getAvailableDeepPocketsRange())
        {
            val slot = screenHandler.getSlot(i) as ExtensionSlot
            slot.canTakeItems = true
        }
        for (i in getUnavailableDeepPocketsRange())
        {
            val slot = screenHandler.getSlot(i) as ExtensionSlot
            slot.canTakeItems = false
        }
    }

    fun getAvailableDeepPocketsRange(): IntRange
    {
        return HANDLER_DEEP_POCKETS_RANGE.first until
                HANDLER_DEEP_POCKETS_RANGE.first + inventoryAddon.getExtensionRows() * VANILLA_ROW_LENGTH
    }

    fun getUnavailableDeepPocketsRange(): IntRange
    {
        return getAvailableDeepPocketsRange().last + 1 .. HANDLER_DEEP_POCKETS_RANGE.last
    }

    companion object
    {
        val PlayerEntity.screenHandlerAddon: PlayerScreenHandlerAddon
            get() = (this.playerScreenHandler as ScreenHandlerDuck).addon
    }
}
