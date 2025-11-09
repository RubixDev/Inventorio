package de.rubixdev.inventorio.enchantment

import de.rubixdev.inventorio.config.GlobalSettings
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class DeepPocketsBookRecipe(category: CraftingRecipeCategory) : SpecialCraftingRecipe(category) {
    override fun matches(craftingInventory: RecipeInputInventory, world: World): Boolean {
        if (!GlobalSettings.deepPocketsBookCraft.boolValue) {
            return false
        }
        var shells = 0
        var books = 0

        for (i in 0 until craftingInventory.size()) {
            val itemStack = craftingInventory.getStack(i)
            if (SHULKER_SHELL.test(itemStack)) {
                shells++
            }
            if (BOOKS.test(itemStack)) {
                books++
            }
        }
        return shells == 2 && books == 1
    }

    override fun craft(inventory: RecipeInputInventory, lookup: RegistryWrapper.WrapperLookup): ItemStack =
        EnchantedBookItem.forEnchantment(EnchantmentLevelEntry(DeepPocketsEnchantment, 1))

    override fun fits(width: Int, height: Int): Boolean {
        return width >= 2 && height >= 2
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return SERIALIZER
    }

    override fun isIgnoredInRecipeBook(): Boolean {
        return !GlobalSettings.deepPocketsBookCraft.boolValue
    }

    override fun getIngredients(): DefaultedList<Ingredient> {
        return DefaultedList.copyOf(SHULKER_SHELL, SHULKER_SHELL, BOOKS, SHULKER_SHELL)
    }

    override fun getResult(lookup: RegistryWrapper.WrapperLookup): ItemStack = when (GlobalSettings.deepPocketsBookCraft.boolValue) {
        true -> EnchantedBookItem.forEnchantment(EnchantmentLevelEntry(DeepPocketsEnchantment, 1))
        false -> ItemStack.EMPTY
    }

    companion object {
        private val SHULKER_SHELL = Ingredient.ofItems(Items.SHULKER_SHELL)
        private val BOOKS = Ingredient.ofItems(Items.BOOK, Items.WRITABLE_BOOK)

        @Suppress("ktlint:standard:property-naming")
        lateinit var SERIALIZER: SpecialRecipeSerializer<DeepPocketsBookRecipe>
    }
}
