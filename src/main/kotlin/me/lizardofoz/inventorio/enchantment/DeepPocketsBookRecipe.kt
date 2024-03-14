package me.lizardofoz.inventorio.enchantment

import me.lizardofoz.inventorio.config.GlobalSettings
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
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

//#if MC >= 12002
class DeepPocketsBookRecipe(category: CraftingRecipeCategory) : SpecialCraftingRecipe(category) {
//#else
//$$ class DeepPocketsBookRecipe(identifier: net.minecraft.util.Identifier, category: CraftingRecipeCategory) : SpecialCraftingRecipe(identifier, category) {
//#endif
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

    override fun craft(inventory: RecipeInputInventory, registryManager: DynamicRegistryManager): ItemStack {
        val bookItem = ItemStack(Items.ENCHANTED_BOOK, 1)
        EnchantedBookItem.addEnchantment(bookItem, EnchantmentLevelEntry(DeepPocketsEnchantment, 1))
        return bookItem
    }

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

    override fun getResult(registryManager: DynamicRegistryManager): ItemStack {
        if (!GlobalSettings.deepPocketsBookCraft.boolValue) {
            ItemStack.EMPTY
        }
        val bookItem = ItemStack(Items.ENCHANTED_BOOK, 1)
        EnchantedBookItem.addEnchantment(bookItem, EnchantmentLevelEntry(DeepPocketsEnchantment, 1))
        return bookItem
    }

    companion object {
        private val SHULKER_SHELL = Ingredient.ofItems(Items.SHULKER_SHELL)
        private val BOOKS = Ingredient.ofItems(Items.BOOK, Items.WRITABLE_BOOK)

        @Suppress("ktlint:standard:property-naming")
        lateinit var SERIALIZER: SpecialRecipeSerializer<DeepPocketsBookRecipe>
    }
}
