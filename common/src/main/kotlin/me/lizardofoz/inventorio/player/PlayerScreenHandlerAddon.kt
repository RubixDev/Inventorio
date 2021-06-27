package me.lizardofoz.inventorio.player

import com.google.common.collect.ImmutableList
import me.lizardofoz.inventorio.api.ToolBeltSlotTemplate
import me.lizardofoz.inventorio.client.ui.PlayerInventoryUIAddon
import me.lizardofoz.inventorio.mixin.accessor.ScreenHandlerAccessor
import me.lizardofoz.inventorio.mixin.accessor.SlotAccessor
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.slot.DeepPocketsSlot
import me.lizardofoz.inventorio.slot.DudOffhandSlot
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
import net.minecraft.screen.slot.Slot

/**
 * This class is an addon to the Player Screen Handler. It's a thing that manages the DISPLAYED SLOTS in the Player Inventory UI.
 * Don't confuse with [PlayerInventoryAddon] that's responsible for handling the actual inventory,
 *   and with [PlayerInventoryUIAddon] that's responsible for the client visuals.
 */
class PlayerScreenHandlerAddon internal constructor(val screenHandler: PlayerScreenHandler, player: PlayerEntity)
{
    private val inventoryAddon = player.inventoryAddon!!

    //Other mods can add additional slots into player's inventory.
    // Thus, we can't attach the slot numbers of our slots to numeric constants,
    // and we calculate it dynamically when we create a handler addon
    /**
     * Note: returns ALL Deep Pocket Slots, regardless if they're available to the player or not
     */
    @JvmField val deepPocketsRange: IntRange
    @JvmField val utilityBeltRange: IntRange
    @JvmField val toolBeltRange: IntRange

    //==============================
    //Injects. These functions are either injected or redirected to by a mixin of a [PlayerScreenHandler] class
    //==============================

    init
    {
        bNoMoreToolBeltSlots = true
        val screenHandlerAccessor = screenHandler as ScreenHandlerAccessor
        val deepPocketsRowCount = inventoryAddon.getDeepPocketsRowCount()

        //Shifting the 2x2 Crafting Grid to its new position
        for (i in HANDLER_ADDON_CRAFTING_GRID_RANGE)
            (screenHandler.slots[i] as SlotAccessor).x += CRAFTING_GRID_OFFSET_X

        //We can't just REMOVE the offhand slot, but we can replace it with a dud slot that doesn't accept any items
        screenHandlerAccessor.trackedSlots[HANDLER_ADDON_DUD_OFFHAND_RANGE] = ItemStack.EMPTY
        screenHandler.slots[HANDLER_ADDON_DUD_OFFHAND_RANGE] = DudOffhandSlot(player.inventory, HANDLER_ADDON_DUD_OFFHAND_RANGE, -1000, -1000)

        val addonSlotsIndexShift = screenHandler.slots.size
        deepPocketsRange = addonSlotsIndexShift expandBy DEEP_POCKETS_MAX_SIZE
        utilityBeltRange = deepPocketsRange.last + 1 expandBy UTILITY_BELT_FULL_SIZE
        toolBeltRange = utilityBeltRange.last + 1 expandBy toolBeltTemplates.size


        //Extended Inventory Section (Deep Pockets Enchantment)
        for ((absoluteIndex, relativeIndex) in INVENTORY_ADDON_DEEP_POCKETS_RANGE.withRelativeIndex())
            screenHandlerAccessor.addASlot(DeepPocketsSlot(
                inventoryAddon, absoluteIndex,
                SLOT_INVENTORY_DEEP_POCKETS.x + (relativeIndex % VANILLA_ROW_LENGTH) * SLOT_UI_SIZE,
                SLOT_INVENTORY_DEEP_POCKETS.y + (relativeIndex / VANILLA_ROW_LENGTH) * SLOT_UI_SIZE
            ))

        //Utility Belt
        for ((absoluteIndex, relativeIndex) in INVENTORY_ADDON_UTILITY_BELT_RANGE.withRelativeIndex())
            screenHandlerAccessor.addASlot(DeepPocketsSlot(
                inventoryAddon, absoluteIndex,
                SLOT_UTILITY_BELT_COLUMN_1.x + SLOT_UI_SIZE * (relativeIndex / UTILITY_BELT_SMALL_SIZE),
                SLOT_UTILITY_BELT_COLUMN_1.y + SLOT_UI_SIZE * (relativeIndex % UTILITY_BELT_SMALL_SIZE)
            ))

        //Tool Belt
        for ((relativeIndex, toolBeltTemplate) in toolBeltTemplates.withIndex())
            screenHandlerAccessor.addASlot(ToolBeltSlot(
                toolBeltTemplate, inventoryAddon, relativeIndex + INVENTORY_ADDON_TOOL_BELT_INDEX_OFFSET,
                ToolBeltSlot.getSlotPosition(deepPocketsRowCount, relativeIndex, toolBeltTemplates.size).x,
                ToolBeltSlot.getSlotPosition(deepPocketsRowCount, relativeIndex, toolBeltTemplates.size).y
            ))
    }

    fun postSlotClick(slotIndex: Int)
    {
        if (slotIndex in HANDLER_ADDON_ARMOR_RANGE)
            updateDeepPocketsCapacity()
        else if (slotIndex in utilityBeltRange && inventoryAddon.getSelectedUtilityStack().isEmpty)
            inventoryAddon.selectedUtility = slotIndex - utilityBeltRange.first
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
        val deepPocketsSlots = getAvailableDeepPocketsRange()
        val screenHandlerAccessor = screenHandler as ScreenHandlerAccessor

        //When we shift-click an item that's in the main inventory or deep pockets
        if (sourceIndex in HANDLER_ADDON_MAIN_INVENTORY_RANGE || sourceIndex in deepPocketsSlots)
        {
            //Try to send an item into the armor slots
            if (MobEntity.getPreferredEquipmentSlot(itemStackStaticCopy).type == EquipmentSlot.Type.ARMOR
                && screenHandlerAccessor.insertAnItem(itemStackDynamic, HANDLER_ADDON_ARMOR_RANGE.first, HANDLER_ADDON_ARMOR_RANGE.last + 1, false))
            {
                updateDeepPocketsCapacity()
                return itemStackStaticCopy
            }
            //Try to send an item into the Tool Belt
            if (screenHandlerAccessor.insertAnItem(itemStackDynamic, toolBeltRange.first, toolBeltRange.last + 1, false))
                return itemStackStaticCopy
        }
        //If we're here, an item can't be moved to neither tool belt nor armor slots

        //When we shift-click an item in hotbar, we want vanilla behavior to kick in.
        //Otherwise it'd move it to Deep Pockets instead of Main Inventory
        if (sourceIndex in HANDLER_ADDON_HOTBAR_RANGE)
        {
            return null
        }
        //When we shift-click an item that's in the main inventory, we try to move it into deep pockets
        else if (sourceIndex in HANDLER_ADDON_MAIN_INVENTORY_RANGE)
        {
            if (!deepPocketsSlots.isEmpty() && screenHandlerAccessor.insertAnItem(itemStackDynamic, deepPocketsSlots.first, deepPocketsSlots.last + 1, false))
                return itemStackStaticCopy
        }
        //When we shift-click an item that's in the deep pockets, we try to move it into the main inventory
        else if (sourceIndex in deepPocketsSlots)
        {
            if (screenHandlerAccessor.insertAnItem(itemStackDynamic, HANDLER_ADDON_MAIN_INVENTORY_RANGE.first, HANDLER_ADDON_MAIN_INVENTORY_RANGE.last + 1, false))
                return itemStackStaticCopy
        }
        //When we shift-click an item in armor slots, tool belt or utility belt, try to move it into the main inventory
        //If the main inventory is full, move it into the deep pockets
        else if (sourceIndex in HANDLER_ADDON_ARMOR_RANGE || sourceIndex in utilityBeltRange || sourceIndex in toolBeltRange)
        {
            if (screenHandlerAccessor.insertAnItem(itemStackDynamic, HANDLER_ADDON_MAIN_INVENTORY_RANGE.first, HANDLER_ADDON_MAIN_INVENTORY_RANGE.last + 1, false)
                || (!deepPocketsSlots.isEmpty() && screenHandlerAccessor.insertAnItem(itemStackDynamic, deepPocketsSlots.first, deepPocketsSlots.last + 1, false)))
                return itemStackStaticCopy
        }
        return null
    }

    /**
     * This is called when the player presses "Swap Item With Offhand" (F by default) in the player's inventory screen
     */
    fun tryTransferToUtilityBeltSlot(sourceSlot: Slot?): Boolean
    {
        if (sourceSlot == null)
            return false
        val itemStackDynamic = sourceSlot.stack
        val screenHandlerAccessor = screenHandler as ScreenHandlerAccessor
        val beltRange = getAvailableUtilityBeltRange()
        //If this is true, we send an item to the utility belt
        if (sourceSlot.id !in beltRange)
        {
            if (screenHandlerAccessor.insertAnItem(itemStackDynamic, beltRange.first, beltRange.last + 1, true))
            {
                if (!screenHandler.onServer)
                    InventorioNetworking.INSTANCE.c2sMoveItemToUtilityBelt(sourceSlot.id)
                return true
            }
            return false
        }
        //If we're here, we're sending an item FROM the utility belt to the rest of the inventory
        val mainInvetoryRange = HANDLER_ADDON_MAIN_INVENTORY_RANGE
        val deepPocketsRange = getAvailableDeepPocketsRange()
        if (screenHandlerAccessor.insertAnItem(itemStackDynamic, mainInvetoryRange.first, mainInvetoryRange.last + 1, true)
            || screenHandlerAccessor.insertAnItem(itemStackDynamic, deepPocketsRange.first, deepPocketsRange.last + 1, true)
        )
        {
            if (!screenHandler.onServer)
                InventorioNetworking.INSTANCE.c2sMoveItemToUtilityBelt(sourceSlot.id)
            return true
        }
        return false
    }

    //==============================
    //Additional functionality
    //==============================

    /**
     * Updates slots position and availability depending on the current level of Deep Pockets Enchantment,
     * and drops items from newly locked slots
     */
    fun updateDeepPocketsCapacity()
    {
        val player = inventoryAddon.player

        for (i in getAvailableDeepPocketsRange())
            (screenHandler.getSlot(i) as DeepPocketsSlot).canTakeItems = true
        for (i in getAvailableUtilityBeltRange())
            (screenHandler.getSlot(i) as DeepPocketsSlot).canTakeItems = true

        for (i in getUnavailableDeepPocketsRange())
        {
            val slot = screenHandler.getSlot(i) as DeepPocketsSlot
            player.dropItem(slot.stack, false, true)
            slot.stack = ItemStack.EMPTY
            slot.canTakeItems = false
        }
        for (i in getUnavailableUtilityBeltRange())
        {
            val slot = screenHandler.getSlot(i) as DeepPocketsSlot
            player.dropItem(slot.stack, false, true)
            slot.stack = ItemStack.EMPTY
            slot.canTakeItems = false
            if (inventoryAddon.selectedUtility >= UTILITY_BELT_SMALL_SIZE)
                inventoryAddon.selectedUtility -= UTILITY_BELT_SMALL_SIZE
        }
        if (!screenHandler.onServer)
            refreshSlotPositions()
    }

    @Environment(EnvType.CLIENT)
    private fun refreshSlotPositions()
    {
        PlayerInventoryUIAddon.onRefresh()
        val deepPocketsRowCount = inventoryAddon.getDeepPocketsRowCount()

        for ((absoluteIndex, relativeIndex) in HANDLER_ADDON_MAIN_INVENTORY_WITHOUT_HOTBAR_RANGE.withRelativeIndex())
        {
            val slot = screenHandler.getSlot(absoluteIndex) as SlotAccessor
            slot.x = SLOTS_INVENTORY_MAIN(deepPocketsRowCount).x + SLOT_UI_SIZE * (relativeIndex % VANILLA_ROW_LENGTH)
            slot.y = SLOTS_INVENTORY_MAIN(deepPocketsRowCount).y + SLOT_UI_SIZE * (relativeIndex / VANILLA_ROW_LENGTH)
        }
        for ((absoluteIndex, relativeIndex) in HANDLER_ADDON_HOTBAR_RANGE.withRelativeIndex())
        {
            val slot = screenHandler.getSlot(absoluteIndex) as SlotAccessor
            slot.x = SLOTS_INVENTORY_HOTBAR(deepPocketsRowCount).x + SLOT_UI_SIZE * relativeIndex
            slot.y = SLOTS_INVENTORY_HOTBAR(deepPocketsRowCount).y
        }
        for ((absoluteIndex, relativeIndex) in toolBeltRange.withRelativeIndex())
        {
            val slot = screenHandler.getSlot(absoluteIndex) as SlotAccessor
            slot.x = ToolBeltSlot.getSlotPosition(deepPocketsRowCount, relativeIndex, toolBeltRange.count()).x
            slot.y = ToolBeltSlot.getSlotPosition(deepPocketsRowCount, relativeIndex, toolBeltRange.count()).y
        }
        for (absoluteIndex in getAvailableDeepPocketsRange())
        {
            val slot = screenHandler.getSlot(absoluteIndex) as DeepPocketsSlot
            slot.canTakeItems = true
        }
        for (absoluteIndex in getUnavailableDeepPocketsRange())
        {
            val slot = screenHandler.getSlot(absoluteIndex) as DeepPocketsSlot
            slot.canTakeItems = false
        }
    }

    //Note: this class returns the range within the SCREEN HANDLER, which is different from the range within the inventory
    fun getAvailableUtilityBeltRange(): IntRange
    {
        return utilityBeltRange.first expandBy inventoryAddon.getAvailableUtilityBeltSize()
    }

    //Note: this class returns the range within the SCREEN HANDLER, which is different from the range within the inventory
    fun getUnavailableUtilityBeltRange(): IntRange
    {
        return getAvailableUtilityBeltRange().last + 1..utilityBeltRange.last
    }

    //Note: this class returns the range within the SCREEN HANDLER, which is different from the range within the inventory
    fun getAvailableDeepPocketsRange(): IntRange
    {
        return deepPocketsRange.first expandBy inventoryAddon.getDeepPocketsRowCount() * VANILLA_ROW_LENGTH
    }

    //Note: this class returns the range within the SCREEN HANDLER, which is different from the range within the inventory
    fun getUnavailableDeepPocketsRange(): IntRange
    {
        return getAvailableDeepPocketsRange().last + 1..deepPocketsRange.last
    }

    companion object
    {
        @JvmStatic
        val PlayerEntity.screenHandlerAddon: PlayerScreenHandlerAddon?
            get() = (this.playerScreenHandler as ScreenHandlerDuck).screenHandlerAddon

        internal val toolBeltTemplates = mutableListOf<ToolBeltSlotTemplate>()
        private var bNoMoreToolBeltSlots = false

        @JvmStatic
        fun getToolBeltTemplate(slotName: String): ToolBeltSlotTemplate?
        {
            return toolBeltTemplates.firstOrNull { it.name == slotName }
        }

        @JvmStatic
        fun getToolBeltTemplates(): ImmutableList<ToolBeltSlotTemplate>
        {
            return ImmutableList.copyOf(toolBeltTemplates)
        }

        @JvmStatic
        fun registerToolBeltTemplateIfNotExists(slotName: String, template: ToolBeltSlotTemplate): ToolBeltSlotTemplate
        {
            if (bNoMoreToolBeltSlots)
                throw IllegalStateException("You can't add any more ToolBelt Slots after a player has already been spawned! Please move the creation of extra ToolBelt Slots earlier.")
            val existing = getToolBeltTemplate(slotName)
            if (existing != null)
                return existing
            toolBeltTemplates.add(template)
            return template
        }
    }
}
