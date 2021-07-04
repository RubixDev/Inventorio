package me.lizardofoz.inventorio.player

import com.google.common.collect.ImmutableList
import me.lizardofoz.inventorio.api.InventorioAddonSection
import me.lizardofoz.inventorio.api.InventorioTickHandler
import me.lizardofoz.inventorio.api.ToolBeltSlotTemplate
import me.lizardofoz.inventorio.client.ui.InventorioScreen
import me.lizardofoz.inventorio.config.GlobalSettings
import me.lizardofoz.inventorio.mixin.client.accessor.MinecraftClientAccessor
import me.lizardofoz.inventorio.player.inventory.PlayerInventoryExtraStuff
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import java.lang.IllegalStateException

/**
 * This class is responsible for the inventory addon itself,
 * while [InventorioScreen] is responsible for the visuals of the Player Screen UI
 * and [InventorioScreenHandler] is responsible for the slots and player interacting with the slots
 */
class PlayerInventoryAddon internal constructor(player: PlayerEntity) : PlayerInventoryExtraStuff(player)
{
    init
    {
        bNoMoreToolBeltSlots = true
    }

    fun tick()
    {
        val ms = Util.getMeasuringTimeMs()
        if (player.handSwinging)
            displayToolTimeStamp = ms + 1000
        if (displayToolTimeStamp <= ms)
            displayTool = ItemStack.EMPTY

        for (tickHandler in tickHandlers.entries)
        {
            for ((index, item) in deepPockets.withIndex())
                tickMe(tickHandler, this, InventorioAddonSection.DEEP_POCKETS, item, index)
            for ((index, item) in toolBelt.withIndex())
                tickMe(tickHandler, this, InventorioAddonSection.TOOLBELT, item, index)
            for ((index, item) in utilityBelt.withIndex())
                tickMe(tickHandler, this, InventorioAddonSection.UTILITY_BELT, item, index)
        }
        if (!player.world.isClient)
            sendUpdateS2C()
    }

    private fun tickMe(tickHandler: MutableMap.MutableEntry<Identifier, InventorioTickHandler>, playerInventoryAddon: PlayerInventoryAddon, deepPockets: InventorioAddonSection, item: ItemStack, index: Int)
    {
        try
        {
            tickHandler.value.tick(playerInventoryAddon, deepPockets, item, index)
        }
        catch (e: Throwable)
        {
            logger.error("Inventory Tick Handler '${tickHandler.key}' has failed: ", e)
        }
    }

    @Environment(EnvType.CLIENT)
    object Client
    {
        val local get() = MinecraftClient.getInstance().player?.inventoryAddon
        @JvmField var selectedHotbarSection = -1
        @JvmField var triesToUseUtility = false
        @JvmField var isUsingUtility = false

        fun activateSelectedUtility()
        {
            triesToUseUtility = true
            isUsingUtility = true
            (MinecraftClient.getInstance() as MinecraftClientAccessor).invokeDoItemUse()
            triesToUseUtility = false
        }
    }

    companion object
    {
        @JvmStatic
        val PlayerEntity.inventoryAddon: PlayerInventoryAddon?
            get() = (this as PlayerDuck).inventorioAddon

        private val tickHandlers = mutableMapOf<Identifier, InventorioTickHandler>()
        internal val toolBeltTemplates = mutableListOf<ToolBeltSlotTemplate>()
        private var bNoMoreToolBeltSlots = false

        @JvmStatic
        fun registerTickHandler(customIdentifier: Identifier, tickHandler: InventorioTickHandler)
        {
            if (tickHandlers.containsKey(customIdentifier))
                throw IllegalStateException("The Identifier '$customIdentifier' has already been taken")
            tickHandlers[customIdentifier] = tickHandler
        }

        @JvmStatic
        fun registerToolBeltTemplateIfNotExists(slotName: String, template: ToolBeltSlotTemplate): ToolBeltSlotTemplate?
        {
            if (GlobalSettings.toolBeltMode.value == ToolBeltMode.DISABLED)
                return null
            if (bNoMoreToolBeltSlots)
                throw IllegalStateException("You can't add any more ToolBelt Slots after a player has already been spawned! Please move the creation of extra ToolBelt Slots earlier.")
            val existing = getToolBeltTemplate(slotName)
            if (existing != null)
                return existing
            toolBeltTemplates.add(template)
            return template
        }

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
    }
}