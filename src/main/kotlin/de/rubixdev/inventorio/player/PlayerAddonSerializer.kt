package de.rubixdev.inventorio.player

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.registry.RegistryWrapper

object PlayerAddonSerializer {
    fun serialize(
        registries: RegistryWrapper.WrapperLookup,
        inventoryAddon: PlayerInventoryAddon,
        inventorioTag: NbtCompound,
    ) {
        inventorioTag.putInt("SelectedUtilitySlot", inventoryAddon.selectedUtility)
        inventorioTag.put("DeepPockets", serializeSection(registries, inventoryAddon.deepPockets))
        inventorioTag.put("UtilityBelt", serializeSection(registries, inventoryAddon.utilityBelt))
        inventorioTag.put("ToolBelt", serializeSection(registries, inventoryAddon.toolBelt))
    }

    private fun serializeSection(registries: RegistryWrapper.WrapperLookup, section: List<ItemStack>): NbtList {
        val resultTag = NbtList()
        for ((slotIndex, itemStack) in section.withIndex()) {
            if (itemStack.isEmpty) {
                continue
            }
            var itemTag = NbtCompound()
            itemTag.putInt("Slot", slotIndex)
            itemTag = itemStack.encode(registries, itemTag) as NbtCompound
            resultTag.add(itemTag)
        }
        return resultTag
    }

    fun deserialize(
        registries: RegistryWrapper.WrapperLookup,
        inventoryAddon: PlayerInventoryAddon,
        inventorioTag: NbtCompound,
    ) {
        inventoryAddon.selectedUtility = inventorioTag.getInt("SelectedUtilitySlot")

        deserializeSection(registries, inventoryAddon, inventoryAddon.utilityBelt, inventorioTag.getList("UtilityBelt", 10))
        deserializeSection(registries, inventoryAddon, inventoryAddon.toolBelt, inventorioTag.getList("ToolBelt", 10))
        deserializeSection(registries, inventoryAddon, inventoryAddon.deepPockets, inventorioTag.getList("DeepPockets", 10))
    }

    private fun deserializeSection(
        registries: RegistryWrapper.WrapperLookup,
        inventoryAddon: PlayerInventoryAddon,
        inventorySection: MutableList<ItemStack>,
        sectionTag: NbtList,
    ) {
        for (i in inventorySection.indices)
            inventorySection[i] = ItemStack.EMPTY

        for (itemTag in sectionTag) {
            val compoundTag = itemTag as NbtCompound
            val itemStack = ItemStack.fromNbtOrEmpty(registries, compoundTag)
            val slotIndex = compoundTag.getInt("Slot")
            if (slotIndex in inventorySection.indices) {
                inventorySection[slotIndex] = itemStack
            } else {
                inventoryAddon.player.dropItem(itemStack, false)
            }
        }
    }
}
