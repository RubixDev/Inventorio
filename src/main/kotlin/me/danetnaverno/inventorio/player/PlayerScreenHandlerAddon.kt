package me.danetnaverno.inventorio.player

import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.mixin.PlayerScreenHandlerAccessor
import me.danetnaverno.inventorio.mixin.SlotAccessor
import me.danetnaverno.inventorio.quickbar.QuickBarHandlerWidget
import me.danetnaverno.inventorio.slot.*
import me.danetnaverno.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import java.awt.Rectangle

class PlayerScreenHandlerAddon internal constructor(val handler: PlayerScreenHandler) : ScreenHandlerAddon
{
    private val playerAddon = PlayerAddon[(handler as PlayerScreenHandlerAccessor).owner]
    private val quickBarHandlerWidget = QuickBarHandlerWidget(playerAddon.inventoryAddon)

    //==============================
    //Injects. These functions are either injected or redirected to by a mixin of a [PlayerInventory] class
    //==============================

    override fun tryInitialize(slot: Slot): Boolean
    {
        return true
    }

    override fun initialize(playerAddon: PlayerAddon)
    {
        initialize(playerAddon, 0, 0, 0, 0)
    }

    fun initialize(playerAddon: PlayerAddon,
                            guiOffsetX: Int, guiOffsetY: Int,
                            slotOffsetX: Int, slotOffsetY: Int)
    {
        val player = this.playerAddon.player
        val inventory = this.playerAddon.player.inventory
        val accessor = handler as PlayerScreenHandlerAccessor
        //todo if we spawn with deep pockets, it bugs out
        val rows = GeneralConstants.getExtraRows(player)

        //Because we can't avoid calling a super constructor, we have to delete slots which have been created.
        handler.slots.clear()
        accessor.trackedSlots.clear()

        for (i in MAIN_INVENTORY_RANGE)
            accessor.addASlot(Slot(inventory, i,
                    SLOTS_PLAYER_INVENTORY_ENTIRE_MAIN_PART(rows).x + (i % INVENTORIO_ROW_LENGTH) * INVENTORY_SLOT_SIZE,
                    SLOTS_PLAYER_INVENTORY_ENTIRE_MAIN_PART(rows).y + (i / INVENTORIO_ROW_LENGTH) * INVENTORY_SLOT_SIZE))

        //todo constants
        accessor.addASlot(ArmorSlot(inventory, EquipmentSlot.HEAD, ARMOR_RANGE.first + 3, 8, 8 + 0 * 18))
        accessor.addASlot(ArmorSlot(inventory, EquipmentSlot.CHEST, ARMOR_RANGE.first + 2, 8, 8 + 1 * 18))
        accessor.addASlot(ArmorSlot(inventory, EquipmentSlot.LEGS, ARMOR_RANGE.first + 1, 8, 8 + 2 * 18))
        accessor.addASlot(ArmorSlot(inventory, EquipmentSlot.FEET, ARMOR_RANGE.first + 0, 8, 8 + 3 * 18))

        //Minecraft has hardcoded slot indexes (40 for inventory and 45 for screen handlers) for the offhand.
        //This is a hack to counter-act any potential unaccounted Mojang hardcoding
        accessor.addASlot(DudOffhandSlot(inventory, DUD_OFFHAND_RANGE.first,
                -1000,
                -1000))

        //Extended Section (Deep Pockets Enchantment)
        for (i in EXTENSION_RANGE)
            accessor.addASlot(ExtensionSlot(inventory, i,
                    SLOTS_PLAYER_INVENTORY_EXTENSION_PART(rows).x + (i % INVENTORIO_ROW_LENGTH) * INVENTORY_SLOT_SIZE,
                    SLOTS_PLAYER_INVENTORY_EXTENSION_PART(rows).y + (i / INVENTORIO_ROW_LENGTH) * INVENTORY_SLOT_SIZE))

        //ToolBelt
        for ((absolute, relative) in TOOL_BELT_RANGE.withRelativeIndex())
        {
            val rect = SLOTS_PLAYER_INVENTORY_TOOL_BELT_SLOT(relative)
            accessor.addASlot(ToolBeltSlot(inventory, SlotRestrictionFilters.toolBelt[relative], absolute,
                    rect.x,
                    rect.y))
        }

        //UtilityBar
        for ((absolute, relative) in UTILITY_BELT_RANGE.withRelativeIndex())
        {
            val rect = SLOTS_PLAYER_INVENTORY_UTILITY_BAR_TOTAL
            accessor.addASlot(UtilityBarSlot(inventory, absolute,
                    rect.x + INVENTORY_SLOT_SIZE * (relative / 4),
                    rect.y + INVENTORY_SLOT_SIZE * (relative % 4)))
        }

        quickBarHandlerWidget.createQuickBarSlots(handler, SLOTS_PLAYER_INVENTORY_QUICK_BAR(rows).x, SLOTS_PLAYER_INVENTORY_QUICK_BAR(rows).y, QUICK_BAR_PHYS_RANGE)

        accessor.addASlot(CraftingResultSlot(player, accessor.craftingInput, accessor.craftingResult, 0, 188, 28))
        for (x in 0..1) for (y in 0..1)
            accessor.addASlot(Slot(accessor.craftingInput, x + y * 2,
                    134 + x * 18,
                    18 + y * 18))
    }

    override fun onSlotClick(slotIndex: Int, clickData: Int, actionType: SlotActionType, player: PlayerEntity): ItemStack?
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
        return quickBarHandlerWidget.onSlotClick(handler, slotIndex, clickData, actionType, player)
    }

    fun transferSlot(player: PlayerEntity, index: Int): ItemStack
    {
        val slot = handler.slots[index]
        if (slot == null || !slot.hasStack())
            return ItemStack.EMPTY

        val accessor = handler as PlayerScreenHandlerAccessor

        val itemStack = slot.stack
        val itemStackCopy = itemStack.copy()
        val equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStackCopy)
        val expansionSlots = GeneralConstants.getAvailableExtensionSlotsRange(player)

        if (equipmentSlot.type == EquipmentSlot.Type.ARMOR)
        {
            if (index in ARMOR_RANGE)
            {
                if (!accessor.insertAnItem(itemStack, MAIN_INVENTORY_RANGE.first, MAIN_INVENTORY_RANGE.last, false))
                    return ItemStack.EMPTY
            }
            else if (!accessor.insertAnItem(itemStack, ARMOR_RANGE.first, ARMOR_RANGE.last, false))
                return ItemStack.EMPTY
            else
                checkCapacity()
        }
        else if (index in MAIN_INVENTORY_RANGE)
        {
            if (!expansionSlots.isEmpty() && !accessor.insertAnItem(itemStack, expansionSlots.first, expansionSlots.last, false))
                return ItemStack.EMPTY
        }
        else if (index in expansionSlots)
        {
            if (!accessor.insertAnItem(itemStack, MAIN_INVENTORY_RANGE.first, MAIN_INVENTORY_RANGE.last, false))
                return ItemStack.EMPTY
        }
        else if (SlotRestrictionFilters.utilityBelt.invoke(itemStack))
        {
            if (!accessor.insertAnItem(itemStack, UTILITY_BELT_RANGE.first, UTILITY_BELT_RANGE.last, false))
                return ItemStack.EMPTY
        }
        else if (index == CRAFTING_GRID_RANGE.first)
        {
            if (!accessor.insertAnItem(itemStack, MAIN_INVENTORY_RANGE.first, MAIN_INVENTORY_RANGE.last, true))
                return ItemStack.EMPTY
            slot.onStackChanged(itemStack, itemStackCopy)
        }
        if (itemStack.isEmpty)
            slot.stack = ItemStack.EMPTY
        else
            slot.markDirty()
        if (itemStack.count == itemStackCopy.count)
            return ItemStack.EMPTY
        val itemStack3 = slot.onTakeItem(player, itemStack)
        if (index == CRAFTING_GRID_RANGE.first)
            player.dropItem(itemStack3, false)
        return itemStackCopy
    }

    //==============================
    //Additional functionality
    //==============================

    fun considerCheckingCapacity(slotIndex: Int)
    {
        if (slotIndex in ARMOR_RANGE)
            checkCapacity()
    }

    fun checkCapacity()
    {
        val player = playerAddon.player
        val range = GeneralConstants.getAvailableExtensionSlotsRange(player)
        for (i in range)
        {
            val slot = handler.getSlot(i) as ExtensionSlot
            slot.canTakeItems = true
        }
        for (i in GeneralConstants.getUnavailableExtensionSlotsRange(player))
        {
            val slot = handler.getSlot(i) as ExtensionSlot
            RobertoGarbagio.LOGGER.info("drop ${slot.stack}")
            player.dropItem(slot.stack, false, true)?.setPickupDelay(0)
            slot.stack = ItemStack.EMPTY
            slot.canTakeItems = false
        }
        for (i in UTILITY_BELT_EXTENSION_RANGE)
        {
            val slot = handler.getSlot(i) as ExtensionSlot
            if (range.isEmpty())
            {
                player.dropItem(slot.stack, false, true)?.setPickupDelay(0)
                slot.stack = ItemStack.EMPTY
                slot.canTakeItems = false
            }
            else
                slot.canTakeItems = true
        }
        if (!handler.onServer)
            refreshSlots()
    }

    @Environment(EnvType.CLIENT)
    fun refreshSlots()
    {
        val player = playerAddon.player
        val rows = GeneralConstants.getExtraRows(player)
        val mainRect = SLOTS_PLAYER_INVENTORY_ENTIRE_MAIN_PART(rows)

        for ((absolute, relative) in MAIN_INVENTORY_RANGE.withRelativeIndex())
            repositionSlot(handler.getSlot(absolute), mainRect, relative)

        val extensionRect = SLOTS_PLAYER_INVENTORY_EXTENSION_PART(rows)
        for ((absolute, relative) in GeneralConstants.getAvailableExtensionSlotsRange(player).withRelativeIndex())
        {
            val slot = handler.getSlot(absolute)
            repositionSlot(slot, extensionRect, relative)
            (slot as ExtensionSlot).canTakeItems = true
        }

        val quickBarRect = SLOTS_PLAYER_INVENTORY_QUICK_BAR(rows)
        for ((absolute, relative) in QUICK_BAR_PHYS_RANGE.withRelativeIndex())
            repositionSlot(handler.getSlot(absolute), quickBarRect, relative)
        for ((absolute, relative) in QUICK_BAR_SHORTCUTS_RANGE.withRelativeIndex())
            repositionSlot(handler.getSlot(absolute), quickBarRect, relative)

        for (i in GeneralConstants.getUnavailableExtensionSlotsRange(player))
        {
            val slot = handler.getSlot(i) as ExtensionSlot
            slot.canTakeItems = false
        }
    }

    private fun repositionSlot(slot: Slot, rect: Rectangle, index: Int)
    {
        val accessor = slot as SlotAccessor
        accessor.x = rect.x + INVENTORY_SLOT_SIZE * (index % INVENTORIO_ROW_LENGTH)
        accessor.y = rect.y + INVENTORY_SLOT_SIZE * (index / INVENTORIO_ROW_LENGTH)
    }
}
