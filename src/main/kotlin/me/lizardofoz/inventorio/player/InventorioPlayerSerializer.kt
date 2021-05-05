package me.lizardofoz.inventorio.player

import me.lizardofoz.inventorio.util.isNotEmpty
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.collection.DefaultedList

object InventorioPlayerSerializer
{
    fun serialize(playerAddon: PlayerAddon, inventorioTag: CompoundTag)
    {
        val inventoryAddon = playerAddon.inventoryAddon

        val extension = ListTag()
        val utilityBelt = ListTag()
        val toolBelt = ListTag()

        serializeSection(inventoryAddon.extension, extension)
        serializeSection(inventoryAddon.utilityBelt, utilityBelt)
        serializeSection(inventoryAddon.toolBelt, toolBelt)

        inventorioTag.putInt("SelectedUtilitySlot", inventoryAddon.selectedUtility)
        inventorioTag.put("Extension", extension)
        inventorioTag.put("UtilityBelt", utilityBelt)
        inventorioTag.put("ToolBelt", toolBelt)
    }

    private fun serializeSection(section: DefaultedList<ItemStack>, tag: ListTag)
    {
        for ((i, itemStack) in section.withIndex())
        {
            if (itemStack.isEmpty)
                continue
            val itemTag = CompoundTag()
            itemTag.putInt("Slot", i)
            itemStack.toTag(itemTag)
            tag.add(itemTag)
        }
    }

    fun deserialize(playerAddon: PlayerAddon, inventorioTag: CompoundTag)
    {
        val inventoryAddon = playerAddon.inventoryAddon
        inventoryAddon.selectedUtility = inventorioTag.getInt("SelectedUtilitySlot")

        deserializeSection(inventoryAddon.utilityBelt, inventorioTag.getList("UtilityBelt", NbtType.COMPOUND))
        deserializeSection(inventoryAddon.toolBelt, inventorioTag.getList("ToolBelt", NbtType.COMPOUND))
        deserializeSection(inventoryAddon.extension, inventorioTag.getList("Extension", NbtType.COMPOUND))
    }

    private fun deserializeSection(section: DefaultedList<ItemStack>, tag: ListTag)
    {
        section.clear()
        for (itemTag in tag)
        {
            val compoundTag = itemTag as CompoundTag
            val i = compoundTag.getInt("Slot")
            val itemStack = ItemStack.fromTag(compoundTag)
            if (itemStack.isNotEmpty)
                section[i] = itemStack
        }
    }
}