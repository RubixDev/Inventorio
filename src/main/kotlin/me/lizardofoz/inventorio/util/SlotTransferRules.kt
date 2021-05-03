package me.lizardofoz.inventorio.util

import me.lizardofoz.inventorio.mixin.accessor.ScreenHandlerAccessor
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.screenhandler.PlayerScreenHandlerAddon
import me.lizardofoz.inventorio.slot.QuickBarPhysicalSlot
import me.lizardofoz.inventorio.slot.QuickBarShortcutSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.*
import net.minecraft.screen.slot.CraftingResultSlot
import net.minecraft.screen.slot.FurnaceFuelSlot
import net.minecraft.screen.slot.FurnaceOutputSlot
import net.minecraft.screen.slot.Slot

object SlotTransferRules
{
    private val transferRules = mutableMapOf<Class<out ScreenHandler>, (ScreenHandler, Slot) -> Sequence<Int>>()

    init
    {
        val genericContainer = { screenHandler: ScreenHandler, slot: Slot ->
            sequence {
                if (slot.inventory is PlayerInventory)
                {
                    for (index in screenHandler.slots.indices)
                        if (slot.inventory is PlayerInventory && slot !is QuickBarPhysicalSlot && slot !is QuickBarShortcutSlot)
                            yield(index)
                }
                else
                {
                    for (index in screenHandler.slots.indices.reversed())
                        if (isPlayerInventorySlot(screenHandler.slots[index]))
                            yield(index)
                }
            }
        }

        transferRules[GenericContainerScreenHandler::class.java] = genericContainer
        transferRules[Generic3x3ContainerScreenHandler::class.java] = genericContainer
        transferRules[ShulkerBoxScreenHandler::class.java] = genericContainer
        transferRules[HopperScreenHandler::class.java] = genericContainer

        transferRules[BeaconScreenHandler::class.java] = genericContainer
        transferRules[BrewingStandScreenHandler::class.java] = genericContainer

        transferRules[AbstractFurnaceScreenHandler::class.java] = { screenHandler, slot ->
            sequence {
                if (slot is FurnaceFuelSlot || slot is FurnaceOutputSlot)
                {
                    for (index in screenHandler.slots.indices.reversed())
                        if (isPlayerInventorySlot(screenHandler.slots[index]))
                            yield(index)
                }
                else
                {
                    for ((index, otherSlot) in screenHandler.slots.withIndex())
                        if (otherSlot is FurnaceFuelSlot || otherSlot is FurnaceOutputSlot)
                            yield(index)
                }
            }
        }

        //questionable
        val crafting = { screenHandler: ScreenHandler, slot: Slot ->
            sequence {
                if (slot is CraftingResultSlot || slot.inventory !is PlayerInventory)
                {
                    for (index in screenHandler.slots.indices.reversed())
                        if (isPlayerInventorySlot(screenHandler.slots[index]))
                            yield(index)
                }
                else
                {
                    for ((index, otherSlot) in screenHandler.slots.withIndex())
                        if (!isPlayerInventorySlot(otherSlot))
                            yield(index)
                }
            }
        }

        transferRules[CraftingScreenHandler::class.java] = crafting
        transferRules[ForgingScreenHandler::class.java] = crafting
        transferRules[GrindstoneScreenHandler::class.java] = crafting
        transferRules[StonecutterScreenHandler::class.java] = crafting

        transferRules[EnchantmentScreenHandler::class.java] = genericContainer //todo bugged
        transferRules[LoomScreenHandler::class.java] = genericContainer

        //todo
        //CartographyTableScreenHandler
        //HorseScreenHandler
        //MerchantScreenHandler
    }

    fun transferSlot(screenHandler: ScreenHandler, player: PlayerEntity, sourceIndex: Int): ItemStack
    {
        //if (PlayerAddon[player].isScreenHandlerIgnored(screenHandler))
        //    return screenHandler.transferSlot(player, sourceIndex)
        if (sourceIndex !in screenHandler.slots.indices)
            return ItemStack.EMPTY
        val slot = screenHandler.getSlot(sourceIndex)
        if (slot == null || !slot.hasStack())
            return ItemStack.EMPTY;

        if (screenHandler is PlayerScreenHandler)
            return (PlayerAddon[player].handlerAddon as PlayerScreenHandlerAddon).transferSlot(player, sourceIndex)

        val entry = transferRules.entries.firstOrNull { it.key.isInstance(screenHandler) }
        if (entry == null)
            return ItemStack.EMPTY

        val item = slot.stack
        val itemBefore = item.copy()

        val sequence = entry.value.invoke(screenHandler, slot)
        if (sequence.any { (screenHandler as ScreenHandlerAccessor).insertAnItem(item, it, it + 1, false) })
        {
            if (screenHandler is CraftingScreenHandler
                    || screenHandler is ForgingScreenHandler
                    || screenHandler is GrindstoneScreenHandler
                    || screenHandler is StonecutterScreenHandler
                    || screenHandler is MerchantScreenHandler)
            {
                val itemStack3 = slot.onTakeItem(player, item)
                if (itemStack3.isEmpty)
                    slot.stack = ItemStack.EMPTY
                player.dropItem(itemStack3, false)
            }
            return itemBefore
        }
        return ItemStack.EMPTY
    }

    private fun isPlayerInventorySlot(slot: Slot): Boolean
    {
        return slot.inventory is PlayerInventory && slot !is QuickBarPhysicalSlot && slot !is QuickBarShortcutSlot
    }
}