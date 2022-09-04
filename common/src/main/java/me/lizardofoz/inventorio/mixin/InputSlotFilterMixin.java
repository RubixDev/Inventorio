package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.player.InventorioScreenHandler;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InputSlotFiller.class)
public class InputSlotFilterMixin
{
    @Shadow protected AbstractRecipeScreenHandler<?> craftingScreenHandler;

    @Inject(method = "returnSlot", at = @At(value = "HEAD"), cancellable = true)
    private void inventorioSpaceFindingBugFix(int i, CallbackInfo ci)
    {
        if (craftingScreenHandler instanceof InventorioScreenHandler && i == 0)
            ci.cancel();
    }
}