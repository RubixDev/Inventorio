package me.lizardofoz.inventorio.player

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList

typealias CompoundTag = NbtCompound
typealias ListTag = NbtList

object PlayerAddonSerializer
{
    fun serialize(inventoryAddon: PlayerInventoryAddon, inventorioTag: CompoundTag)
    {
        inventorioTag.putInt("SelectedUtilitySlot", inventoryAddon.selectedUtility)
        inventorioTag.put("DeepPockets", serializeSection(inventoryAddon.deepPockets))
        inventorioTag.put("UtilityBelt", serializeSection(inventoryAddon.utilityBelt))
        inventorioTag.put("ToolBelt", serializeSection(inventoryAddon.toolBelt))
    }

    fun serializeSection(section: List<ItemStack>): ListTag
    {
        val resultTag = ListTag()
        for ((slotIndex, itemStack) in section.withIndex())
        {
            if (itemStack.isEmpty)
                continue
            val itemTag = CompoundTag()
            itemTag.putInt("Slot", slotIndex)
            itemStack.writeNbt(itemTag)
            resultTag.add(itemTag)
        }
        return resultTag
    }

    fun deserialize(inventoryAddon: PlayerInventoryAddon, inventorioTag: CompoundTag, isFirstLaunch: Boolean)
    {
        inventoryAddon.selectedUtility = inventorioTag.getInt("SelectedUtilitySlot")

        deserializeSection(inventoryAddon, inventoryAddon.utilityBelt, inventorioTag.getList("UtilityBelt", 10))
        deserializeSection(inventoryAddon, inventoryAddon.toolBelt, inventorioTag.getList("ToolBelt", 10))
        deserializeSection(inventoryAddon, inventoryAddon.deepPockets, inventorioTag.getList("DeepPockets", 10))

        if (isFirstLaunch)
            ejectVanillaOffhand(inventoryAddon.player)
    }

    fun deserializeSection(inventoryAddon: PlayerInventoryAddon, inventorySection: MutableList<ItemStack>, sectionTag: ListTag)
    {
        for (i in inventorySection.indices)
            inventorySection[i] = ItemStack.EMPTY

        for (itemTag in sectionTag)
        {
            val compoundTag = itemTag as CompoundTag
            val itemStack = ItemStack.fromNbt(compoundTag)
            val slotIndex = compoundTag.getInt("Slot")
            if (slotIndex in inventorySection.indices)
                inventorySection[slotIndex] = itemStack
            else
                inventoryAddon.player.dropItem(itemStack, false)
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