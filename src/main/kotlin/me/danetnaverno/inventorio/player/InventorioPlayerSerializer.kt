package me.danetnaverno.inventorio.player

import me.danetnaverno.inventorio.QuickBarMode
import me.danetnaverno.inventorio.UtilityBeltMode
import me.danetnaverno.inventorio.isNotEmpty
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

        inventorioTag.putInt("SelectedUtilitySlot", inventoryAddon.selectedUtility)
        inventorioTag.putString("QuickBarMode", playerAddon.quickBarMode.toString())
        inventorioTag.putString("UtilityBeltMode", playerAddon.utilityBeltMode.toString())

        //Inventory
        val extension = ListTag()
        val utilityBelt = ListTag()
        val toolBelt = ListTag()
        val physBar = ListTag()
        val quickBar = ListTag()

        serializeSection(inventoryAddon.extension, extension)
        serializeSection(inventoryAddon.utilityBelt, utilityBelt)
        serializeSection(inventoryAddon.toolBelt, toolBelt)
        serializeSection(inventoryAddon.physicalQuickQar, physBar)
        serializeSection(inventoryAddon.shortcutQuickBar.stacks, quickBar)

        inventorioTag.put("Extension", extension)
        inventorioTag.put("UtilityBelt", utilityBelt)
        inventorioTag.put("ToolBelt", toolBelt)
        inventorioTag.put("PhysBar", physBar)
        inventorioTag.put("QuickBar", quickBar)
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
        if (inventorioTag.contains("QuickBarMode"))
            playerAddon.quickBarMode = QuickBarMode.valueOf(inventorioTag.getString("QuickBarMode"))
        else
            playerAddon.quickBarMode = QuickBarMode.NOT_SELECTED
        if (inventorioTag.contains("UtilityBeltMode"))
            playerAddon.utilityBeltMode = UtilityBeltMode.valueOf(inventorioTag.getString("UtilityBeltMode"))

        //Inventory
        deserializeSection(inventoryAddon.utilityBelt, inventorioTag.getList("UtilityBelt", NbtType.COMPOUND))
        deserializeSection(inventoryAddon.toolBelt, inventorioTag.getList("ToolBelt", NbtType.COMPOUND))
        deserializeSection(inventoryAddon.extension, inventorioTag.getList("Extension", NbtType.COMPOUND))
        deserializeSection(inventoryAddon.physicalQuickQar, inventorioTag.getList("PhysBar", NbtType.COMPOUND))
        deserializeSection(inventoryAddon.shortcutQuickBar.stacks, inventorioTag.getList("QuickBar", NbtType.COMPOUND))
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