package me.lizardofoz.inventorio.player

import me.lizardofoz.inventorio.util.isNotEmpty
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

    fun deserialize(inventoryAddon: PlayerInventoryAddon, inventorioTag: CompoundTag)
    {
        inventoryAddon.selectedUtility = inventorioTag.getInt("SelectedUtilitySlot")

        deserializeSection(inventoryAddon.utilityBelt, inventorioTag.getList("UtilityBelt", 10))
        deserializeSection(inventoryAddon.toolBelt, inventorioTag.getList("ToolBelt", 10))
        deserializeSection(inventoryAddon.deepPockets, inventorioTag.getList("DeepPockets", 10))
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
}