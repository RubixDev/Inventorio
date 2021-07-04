package me.lizardofoz.inventorio.player

import me.lizardofoz.inventorio.ScreenTypeProvider
import me.lizardofoz.inventorio.client.ui.InventorioScreen
import me.lizardofoz.inventorio.mixin.accessor.CraftingScreenHandlerAccessor
import me.lizardofoz.inventorio.mixin.accessor.SlotAccessor
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.toolBeltTemplates
import me.lizardofoz.inventorio.slot.ArmorSlot
import me.lizardofoz.inventorio.slot.DeepPocketsSlot
import me.lizardofoz.inventorio.slot.ToolBeltSlot
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeFinder
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import java.util.function.Consumer

class InventorioScreenHandler(syncId: Int, val inventory: PlayerInventory)
    : AbstractRecipeScreenHandler<CraftingInventory?>(ScreenTypeProvider.INSTANCE.getScreenHandlerType(), syncId)
{
    private val inventoryAddon = inventory.player.inventoryAddon!!

    @JvmField val craftingGridRange: IntRange
    @JvmField val armorSlotsRange: IntRange
    @JvmField val mainInventoryRange: IntRange
    @JvmField val deepPocketsRange: IntRange
    @JvmField val utilityBeltRange: IntRange
    @JvmField val toolBeltRange: IntRange

    @JvmField val mainInventoryWithoutHotbarRange: IntRange
    @JvmField val hotbarRange: IntRange

    private val craftingInput = CraftingInventory(this, 2, 2)
    private val craftingResult = CraftingResultInventory()
    //===================================================
    //Modified methods
    //===================================================
    init
    {
        craftingGridRange = 0 expandBy CRAFTING_GRID_SIZE
        armorSlotsRange = craftingGridRange.last + 1 expandBy ARMOR_SIZE
        mainInventoryRange = armorSlotsRange.last + 1 expandBy MAIN_INVENTORY_SIZE
        deepPocketsRange = mainInventoryRange.last + 1 expandBy DEEP_POCKETS_MAX_SIZE
        utilityBeltRange = deepPocketsRange.last + 1 expandBy UTILITY_BELT_FULL_SIZE
        toolBeltRange = utilityBeltRange.last + 1 expandBy toolBeltTemplates.size

        mainInventoryWithoutHotbarRange = mainInventoryRange.first..mainInventoryRange.last - 9
        hotbarRange = mainInventoryWithoutHotbarRange.last + 1..mainInventoryRange.last

        //Crafting Grid
        addSlot(CraftingResultSlot(inventory.player, craftingInput, craftingResult, 0, 174, 28))
        for (i in 0..3)
            addSlot(Slot(craftingInput, i, 118 + i % 2 * 18, 18 + i / 2 * 18))

        //Armor
        for ((absoluteIndex, relativeIndex) in armorSlotsRange.withRelativeIndex())
            addSlot(ArmorSlot(inventory, 39 - relativeIndex, 8, 8 + relativeIndex * 18, armorSlots[relativeIndex]))

        //Main Inventory
        for ((absoluteIndex, relativeIndex) in mainInventoryWithoutHotbarRange.withRelativeIndex())
            addSlot(Slot(inventory, relativeIndex + 9, 8 + (relativeIndex % 9) * 18, 84 + (relativeIndex / 9) * 18))

        //Hotbar
        for ((absoluteIndex, relativeIndex) in hotbarRange.withRelativeIndex())
            addSlot(Slot(inventory, relativeIndex, 8 + relativeIndex * 18, 142))

        //Extended Inventory Section (Deep Pockets Enchantment)
        for ((absoluteIndex, relativeIndex) in INVENTORY_ADDON_DEEP_POCKETS_RANGE.withRelativeIndex())
            addSlot(DeepPocketsSlot(
                inventoryAddon, absoluteIndex,
                SLOT_INVENTORY_DEEP_POCKETS.x + (relativeIndex % VANILLA_ROW_LENGTH) * SLOT_UI_SIZE,
                SLOT_INVENTORY_DEEP_POCKETS.y + (relativeIndex / VANILLA_ROW_LENGTH) * SLOT_UI_SIZE
            ))

        //Utility Belt
        for ((absoluteIndex, relativeIndex) in INVENTORY_ADDON_UTILITY_BELT_RANGE.withRelativeIndex())
            addSlot(DeepPocketsSlot(
                inventoryAddon, absoluteIndex,
                SLOT_UTILITY_BELT_COLUMN_1.x + SLOT_UI_SIZE * (relativeIndex / UTILITY_BELT_SMALL_SIZE),
                SLOT_UTILITY_BELT_COLUMN_1.y + SLOT_UI_SIZE * (relativeIndex % UTILITY_BELT_SMALL_SIZE)
            ))

        //Tool Belt
        val deepPocketsRowCount = inventoryAddon.getDeepPocketsRowCount()
        for ((relativeIndex, toolBeltTemplate) in toolBeltTemplates.withIndex())
            addSlot(ToolBeltSlot(
                toolBeltTemplate, inventoryAddon, relativeIndex + INVENTORY_ADDON_TOOL_BELT_INDEX_OFFSET,
                ToolBeltSlot.getSlotPosition(deepPocketsRowCount, relativeIndex, toolBeltTemplates.size).x,
                ToolBeltSlot.getSlotPosition(deepPocketsRowCount, relativeIndex, toolBeltTemplates.size).y
            ))

        updateDeepPocketsCapacity()

        openConsumers.forEach {
            try
            {
                it.value.accept(this)
            }
            catch (e: Throwable)
            {
                logger.error("Inventory Screen Handler Open Consumer '${it.key}' has failed: ", e)
            }
        }
    }

    override fun transferSlot(player: PlayerEntity, sourceIndex: Int): ItemStack
    {
        val sourceSlot = slots[sourceIndex]
        val itemStackDynamic = sourceSlot.stack
        val itemStackStaticCopy = transferSlotInner(sourceIndex)
        if (itemStackStaticCopy.isNotEmpty)
        {
            if (itemStackDynamic.isEmpty)
                sourceSlot.stack = ItemStack.EMPTY
            else
                sourceSlot.markDirty()

            if (itemStackDynamic.count == itemStackStaticCopy.count)
                return ItemStack.EMPTY

            slots[sourceIndex].onTakeItem(player, itemStackDynamic)
        }
        return itemStackStaticCopy
    }

    private fun transferSlotInner(sourceIndex: Int): ItemStack
    {
        val sourceSlot = slots[sourceIndex]
        val itemStackDynamic = sourceSlot.stack
        val itemStackStaticCopy = itemStackDynamic.copy()
        val availableDeepPocketsRange = getAvailableDeepPocketsRange()

        //First, we want to transfer armor or tools into their respective slots from any other section
        if (sourceIndex in mainInventoryRange || sourceIndex in hotbarRange || sourceIndex in availableDeepPocketsRange)
        {
            //Try to send an item into the armor slots
            if (MobEntity.getPreferredEquipmentSlot(itemStackStaticCopy).type == EquipmentSlot.Type.ARMOR
                && insertItem(itemStackDynamic, armorSlotsRange.first, armorSlotsRange.last + 1, false))
            {
                updateDeepPocketsCapacity()
                return itemStackStaticCopy
            }
            //Try to send an item into the Tool Belt
            if (insertItem(itemStackDynamic, toolBeltRange.first, toolBeltRange.last + 1, false))
                return itemStackStaticCopy
        }
        //If we're here, an item can't be moved to neither tool belt nor armor slots

        //When we shift-click an item that's in the hotbar, we try to move it to main section and then into deep pockets
        if (sourceIndex in hotbarRange)
        {
            if (insertItem(itemStackDynamic, mainInventoryRange.first, mainInventoryRange.last + 1, false))
                return itemStackStaticCopy
            if (!availableDeepPocketsRange.isEmpty() && insertItem(itemStackDynamic, availableDeepPocketsRange.first, availableDeepPocketsRange.last + 1, false))
                return itemStackStaticCopy
        }
        //When we shift-click an item that's in the main inventory, we try to move it into deep pockets and then into hotbar (that's what vanilla does)
        else if (sourceIndex in mainInventoryRange)
        {
            if (!availableDeepPocketsRange.isEmpty() && insertItem(itemStackDynamic, availableDeepPocketsRange.first, availableDeepPocketsRange.last + 1, false))
                return itemStackStaticCopy
            if (insertItem(itemStackDynamic, hotbarRange.first, hotbarRange.last + 1, false))
                return itemStackStaticCopy
        }
        //When we shift-click an item that's in the deep pockets, we try to move it into the main inventory
        else if (sourceIndex in availableDeepPocketsRange)
        {
            if (insertItem(itemStackDynamic, mainInventoryRange.first, mainInventoryRange.last + 1, false))
                return itemStackStaticCopy
        }
        //When we shift-click an item in armor slots, tool belt or utility belt, try to move it into the main inventory
        //If the main inventory is full, move it into the deep pockets
        else if ((insertItem(itemStackDynamic, mainInventoryRange.first, mainInventoryRange.last + 1, false))
            || (!availableDeepPocketsRange.isEmpty() && insertItem(itemStackDynamic, availableDeepPocketsRange.first, availableDeepPocketsRange.last + 1, false)))
        {
            if (sourceIndex in craftingGridRange)
            {
                onContentChanged(craftingInput)
                onContentChanged(craftingResult)
            }
            return itemStackStaticCopy
        }

        return ItemStack.EMPTY
    }

    override fun onSlotClick(slotIndex: Int, clickData: Int, actionType: SlotActionType, playerEntity: PlayerEntity): ItemStack
    {
        val result = super.onSlotClick(slotIndex, clickData, actionType, playerEntity)
        if (slotIndex in armorSlotsRange)
            updateDeepPocketsCapacity()
        else if (slotIndex in utilityBeltRange && inventoryAddon.getSelectedUtilityStack().isEmpty)
            inventoryAddon.selectedUtility = slotIndex - utilityBeltRange.first
        return result
    }

    //==============================
    //Additional functionality
    //==============================
    /**
     * This is called when the player presses "Swap Item With Offhand" (F by default) in the player's inventory screen
     */
    fun tryTransferToUtilityBeltSlot(sourceSlot: Slot?): Boolean
    {
        if (sourceSlot == null)
            return false
        val itemStackDynamic = sourceSlot.stack
        val beltRange = getAvailableUtilityBeltRange()
        //If this is true, we send an item to the utility belt
        if (sourceSlot.id !in beltRange)
        {
            if (insertItem(itemStackDynamic, beltRange.first, beltRange.last + 1, true))
            {
                if (inventoryAddon.player.world.isClient)
                    InventorioNetworking.INSTANCE.c2sMoveItemToUtilityBelt(sourceSlot.id)
                return true
            }
            return false
        }
        //If we're here, we're sending an item FROM the utility belt to the rest of the inventory
        val deepPocketsRange = getAvailableDeepPocketsRange()
        if (insertItem(itemStackDynamic, mainInventoryRange.first, mainInventoryRange.last + 1, true)
            || insertItem(itemStackDynamic, deepPocketsRange.first, deepPocketsRange.last + 1, true))
        {
            if (inventoryAddon.player.world.isClient)
                InventorioNetworking.INSTANCE.c2sMoveItemToUtilityBelt(sourceSlot.id)
            return true
        }
        return false
    }

    /**
     * Updates slots position and availability depending on the current level of Deep Pockets Enchantment,
     * and drops items from newly locked slots
     */
    fun updateDeepPocketsCapacity()
    {
        val player = inventoryAddon.player

        for (i in getAvailableDeepPocketsRange())
            (getSlot(i) as DeepPocketsSlot).canTakeItems = true
        for (i in getAvailableUtilityBeltRange())
            (getSlot(i) as DeepPocketsSlot).canTakeItems = true

        for (i in getUnavailableDeepPocketsRange())
        {
            val slot = getSlot(i) as DeepPocketsSlot
            player.dropItem(slot.stack, false, true)
            slot.stack = ItemStack.EMPTY
            slot.canTakeItems = false
        }
        for (i in getUnavailableUtilityBeltRange())
        {
            val slot = getSlot(i) as DeepPocketsSlot
            player.dropItem(slot.stack, false, true)
            slot.stack = ItemStack.EMPTY
            slot.canTakeItems = false
            if (inventoryAddon.selectedUtility >= UTILITY_BELT_SMALL_SIZE)
                inventoryAddon.selectedUtility -= UTILITY_BELT_SMALL_SIZE
        }
        if (inventoryAddon.player.world.isClient)
            refreshSlotPositions()
    }

    @Environment(EnvType.CLIENT)
    private fun refreshSlotPositions()
    {
        (MinecraftClient.getInstance().currentScreen as? InventorioScreen)?.onRefresh()
        val deepPocketsRowCount = inventoryAddon.getDeepPocketsRowCount()

        for ((absoluteIndex, relativeIndex) in mainInventoryWithoutHotbarRange.withRelativeIndex())
        {
            val slot = getSlot(absoluteIndex) as SlotAccessor
            slot.x = SLOTS_INVENTORY_MAIN(deepPocketsRowCount).x + SLOT_UI_SIZE * (relativeIndex % VANILLA_ROW_LENGTH)
            slot.y = SLOTS_INVENTORY_MAIN(deepPocketsRowCount).y + SLOT_UI_SIZE * (relativeIndex / VANILLA_ROW_LENGTH)
        }
        for ((absoluteIndex, relativeIndex) in hotbarRange.withRelativeIndex())
        {
            val slot = getSlot(absoluteIndex) as SlotAccessor
            slot.x = SLOTS_INVENTORY_HOTBAR(deepPocketsRowCount).x + SLOT_UI_SIZE * relativeIndex
            slot.y = SLOTS_INVENTORY_HOTBAR(deepPocketsRowCount).y
        }
        for ((absoluteIndex, relativeIndex) in toolBeltRange.withRelativeIndex())
        {
            val slot = getSlot(absoluteIndex) as SlotAccessor
            slot.x = ToolBeltSlot.getSlotPosition(deepPocketsRowCount, relativeIndex, toolBeltRange.count()).x
            slot.y = ToolBeltSlot.getSlotPosition(deepPocketsRowCount, relativeIndex, toolBeltRange.count()).y
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

    //===================================================
    //Unmodified methods lifted from InventoryScreen
    //===================================================
    override fun populateRecipeFinder(finder: RecipeFinder)
    {
        craftingInput.provideRecipeInputs(finder)
    }

    override fun clearCraftingSlots()
    {
        craftingResult.clear()
        craftingInput.clear()
    }

    override fun matches(recipe: Recipe<in CraftingInventory?>): Boolean
    {
        return recipe.matches(craftingInput, inventory.player.world)
    }

    override fun onContentChanged(inventory: Inventory)
    {
        CraftingScreenHandlerAccessor.updateTheResult(this.syncId, this.inventory.player.world, this.inventory.player, this.craftingInput, this.craftingResult)
    }

    override fun close(player: PlayerEntity)
    {
        super.close(player)
        craftingResult.clear()
        if (!player.world.isClient)
            dropInventory(player, player.world, craftingInput)
    }

    override fun canUse(player: PlayerEntity): Boolean
    {
        return true
    }

    override fun canInsertIntoSlot(stack: ItemStack, slot: Slot): Boolean
    {
        return slot.inventory !== craftingResult && super.canInsertIntoSlot(stack, slot)
    }

    override fun getCraftingResultSlotIndex(): Int
    {
        return 0
    }

    override fun getCraftingWidth(): Int
    {
        return craftingInput.width
    }

    override fun getCraftingHeight(): Int
    {
        return craftingInput.height
    }

    @Environment(EnvType.CLIENT)
    override fun getCraftingSlotCount(): Int
    {
        return 5
    }

    @Environment(EnvType.CLIENT)
    override fun getCategory(): RecipeBookCategory
    {
        return RecipeBookCategory.CRAFTING
    }

    //===================================================
    //Companion Object
    //===================================================
    companion object
    {
        @JvmStatic
        val PlayerEntity.inventorioScreenHandler: InventorioScreenHandler?
            get() = currentScreenHandler as? InventorioScreenHandler

        private val openConsumers = mutableMapOf<Identifier, Consumer<InventorioScreenHandler>>()
        private val armorSlots = arrayOf(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)

        @JvmStatic
        fun registerOpenConsumer(customIdentifier: Identifier, screenHandlerConsumer: Consumer<InventorioScreenHandler>)
        {
            if (openConsumers.containsKey(customIdentifier))
                throw IllegalStateException("The Identifier '$customIdentifier' has already been taken")
            openConsumers[customIdentifier] = screenHandlerConsumer
        }

        @JvmStatic
        fun open(player: PlayerEntity)
        {
            player.openHandledScreen(SimpleNamedScreenHandlerFactory(
                { syncId, playerInventory, playerEntity -> InventorioScreenHandler(syncId, playerInventory!!) }, TranslatableText("container.crafting")))
        }
    }
}