package me.lizardofoz.inventorio.player

import me.lizardofoz.inventorio.api.InventorioAddonSection
import me.lizardofoz.inventorio.api.InventorioTickHandler
import me.lizardofoz.inventorio.client.ui.PlayerInventoryUIAddon
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
import org.apache.logging.log4j.LogManager
import java.lang.IllegalStateException

/**
 * This class is responsible for the inventory addon itself,
 * while [PlayerInventoryUIAddon] is responsible for the visuals of the Player Screen UI
 * and [PlayerScreenHandlerAddon] is responsible for the slots and player interacting with the slots
 */
class PlayerInventoryAddon internal constructor(player: PlayerEntity) : PlayerInventoryExtraStuff(player)
{
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
    }

    private fun tickMe(tickHandler: MutableMap.MutableEntry<Identifier, InventorioTickHandler>, playerInventoryAddon: PlayerInventoryAddon, deepPockets: InventorioAddonSection, item: ItemStack, index: Int)
    {
        try
        {
            tickHandler.value.tick(playerInventoryAddon, deepPockets, item, index)
        }
        catch (e: Throwable)
        {
            LogManager.getLogger("Inventorio Tick Handler")!!.error("Inventory Tick Handler '${tickHandler.key}' has failed: ", e)
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
            get() = (this.inventory as InventoryDuck).inventorioAddon

        private val tickHandlers = mutableMapOf<Identifier, InventorioTickHandler>()

        @JvmStatic
        fun registerTickHandler(customIdentifier: Identifier, tickHandler: InventorioTickHandler)
        {
            if (tickHandlers.containsKey(customIdentifier))
                throw IllegalStateException("The Identifier '$customIdentifier' has already been taken")
            tickHandlers[customIdentifier] = tickHandler
        }
    }
}