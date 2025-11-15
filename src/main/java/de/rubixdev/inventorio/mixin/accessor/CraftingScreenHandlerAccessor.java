package de.rubixdev.inventorio.mixin.accessor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

//#if MC >= 12101
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import org.jetbrains.annotations.Nullable;
//#endif

@Mixin(CraftingScreenHandler.class)
public interface CraftingScreenHandlerAccessor {
    @Invoker("updateResult")
    static void updateTheResult(
        ScreenHandler handler,
        World world,
        PlayerEntity player,
        RecipeInputInventory craftingInventory,
        CraftingResultInventory resultInventory
        //#if MC >= 12101
        , @Nullable RecipeEntry<CraftingRecipe> recipe
        //#endif
    ) {}
}
