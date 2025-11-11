package de.rubixdev.inventorio.api

import de.rubixdev.inventorio.player.PlayerInventoryAddon
import java.util.function.BiPredicate
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

class ToolBeltSlotTemplate(val name: String, val emptyIcon: Identifier) {
    private val isAllowed: MutableList<BiPredicate<ItemStack, PlayerInventoryAddon>> = ArrayList()
    private val isDenied: MutableList<BiPredicate<ItemStack, PlayerInventoryAddon>> = ArrayList()

    fun test(itemStack: ItemStack, playerInventoryAddon: PlayerInventoryAddon): Boolean {
        return isAllowed.any { it.test(itemStack, playerInventoryAddon) } && isDenied.none { it.test(itemStack, playerInventoryAddon) }
    }

    fun addAllowingCondition(predicate: BiPredicate<ItemStack, PlayerInventoryAddon>): ToolBeltSlotTemplate {
        isAllowed.add(predicate)
        return this
    }

    fun addAllowingTag(tag: Identifier): ToolBeltSlotTemplate {
        isAllowed.add { itemStack, _ -> testTag(itemStack, tag) }
        return this
    }

    fun addDenyingCondition(predicate: BiPredicate<ItemStack, PlayerInventoryAddon>): ToolBeltSlotTemplate {
        isDenied.add(predicate)
        return this
    }

    fun addDenyingTag(tag: Identifier): ToolBeltSlotTemplate {
        isDenied.add { itemStack, _ -> testTag(itemStack, tag) }
        return this
    }

    private fun testTag(itemStack: ItemStack, identifier: Identifier): Boolean {
        return itemStack.streamTags().anyMatch { it.id == identifier }
    }
}
