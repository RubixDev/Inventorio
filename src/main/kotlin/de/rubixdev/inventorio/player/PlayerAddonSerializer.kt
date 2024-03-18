package de.rubixdev.inventorio.player

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList

object PlayerAddonSerializer {
    fun serialize(inventoryAddon: PlayerInventoryAddon, inventorioTag: NbtCompound) {
        inventorioTag.putInt("SelectedUtilitySlot", inventoryAddon.selectedUtility)
        inventorioTag.put("DeepPockets", serializeSection(inventoryAddon.deepPockets))
        inventorioTag.put("UtilityBelt", serializeSection(inventoryAddon.utilityBelt))
        inventorioTag.put("ToolBelt", serializeSection(inventoryAddon.toolBelt))
    }

    private fun serializeSection(section: List<ItemStack>): NbtList {
        val resultTag = NbtList()
        for ((slotIndex, itemStack) in section.withIndex()) {
            if (itemStack.isEmpty) {
                continue
            }
            val itemTag = NbtCompound()
            itemTag.putInt("Slot", slotIndex)
            itemStack.writeNbt(itemTag)
            resultTag.add(itemTag)
        }
        return resultTag
    }

    fun deserialize(inventoryAddon: PlayerInventoryAddon, inventorioTag: NbtCompound) {
        inventoryAddon.selectedUtility = inventorioTag.getInt("SelectedUtilitySlot")

        deserializeSection(inventoryAddon, inventoryAddon.utilityBelt, inventorioTag.getList("UtilityBelt", 10))
        deserializeSection(inventoryAddon, inventoryAddon.toolBelt, inventorioTag.getList("ToolBelt", 10))
        deserializeSection(inventoryAddon, inventoryAddon.deepPockets, inventorioTag.getList("DeepPockets", 10))
    }

    private fun deserializeSection(inventoryAddon: PlayerInventoryAddon, inventorySection: MutableList<ItemStack>, sectionTag: NbtList) {
        for (i in inventorySection.indices)
            inventorySection[i] = ItemStack.EMPTY

        for (itemTag in sectionTag) {
            val compoundTag = itemTag as NbtCompound
            val itemStack = ItemStack.fromNbt(compoundTag)
            val slotIndex = compoundTag.getInt("Slot")
            if (slotIndex in inventorySection.indices) {
                inventorySection[slotIndex] = itemStack
            } else {
                inventoryAddon.player.dropItem(itemStack, false)
            }
        }
    }
}
