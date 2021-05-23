package me.lizardofoz.inventorio.player

import me.lizardofoz.inventorio.util.isNotEmpty
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag

object InventorioPlayerSerializer
{
    fun serialize(inventoryAddon: PlayerInventoryAddon, inventorioTag: CompoundTag)
    {
        inventorioTag.putInt("SelectedUtilitySlot", inventoryAddon.selectedUtility)
        inventorioTag.put("DeepPockets", serializeSection(inventoryAddon.deepPockets))
        inventorioTag.put("UtilityBelt", serializeSection(inventoryAddon.utilityBelt))
        inventorioTag.put("ToolBelt", serializeSection(inventoryAddon.toolBelt))
    }

    private fun serializeSection(section: List<ItemStack>): ListTag
    {
        val resultTag = ListTag()
        for ((i, itemStack) in section.withIndex())
        {
            if (itemStack.isEmpty)
                continue
            val itemTag = CompoundTag()
            itemTag.putInt("Slot", i)
            itemStack.toTag(itemTag)
            resultTag.add(itemTag)
        }
        return resultTag
    }

    fun deserialize(inventoryAddon: PlayerInventoryAddon, inventorioTag: CompoundTag, isFirstLaunch: Boolean)
    {
        inventoryAddon.selectedUtility = inventorioTag.getInt("SelectedUtilitySlot")

        deserializeSection(inventoryAddon.utilityBelt, inventorioTag.getList("UtilityBelt", 10))
        deserializeSection(inventoryAddon.toolBelt, inventorioTag.getList("ToolBelt", 10))
        deserializeSection(inventoryAddon.deepPockets, inventorioTag.getList("DeepPockets", 10))

        if (isFirstLaunch)
            ejectVanillaOffhand(inventoryAddon.player)
    }

    private fun deserializeSection(inventorySection: MutableList<ItemStack>, sectionTag: ListTag)
    {
        for (i in inventorySection.indices)
            inventorySection[i] = ItemStack.EMPTY

        for (itemTag in sectionTag)
        {
            val compoundTag = itemTag as CompoundTag
            val i = compoundTag.getInt("Slot")
            val itemStack = ItemStack.fromTag(compoundTag)
            if (itemStack.isNotEmpty)
                inventorySection[i] = itemStack
        }
    }

    /**
     * If a player somehow has items in their offhand (the vanilla offhand, not the Utility Belt), eject the items
     * And yes, it has to be here, because when PlayerInventoryAddon is created, player's inventory is not fully loaded
     */
    private fun ejectVanillaOffhand(player: PlayerEntity)
    {
        val offHandItems = ArrayList(player.inventory.offHand)
        player.inventory.offHand.clear()
        for (offHandStack in offHandItems)
            player.dropStack(offHandStack)
    }
}