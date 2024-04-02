package de.rubixdev.inventorio.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.rubixdev.inventorio.duck.RecipeBookLeftOffsetOverride;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetMixin implements RecipeBookLeftOffsetOverride {
    @Shadow
    private int leftOffset;

    @Unique private Integer backgroundWidth = null;

    @ModifyExpressionValue(method = "reset", at = @At(value = "CONSTANT", args = "intValue=86"))
    private int modifyLeftOffset(int original) {
        if (backgroundWidth != null) { return (backgroundWidth + 2) / 2; }
        return original;
    }

    @ModifyReturnValue(method = "isWide", at = @At("RETURN"))
    private boolean correctIsWide(boolean original) {
        return original || leftOffset > 0;
    }

    @Override
    public void inventorio$setBackgroundWidth(@Nullable Integer backgroundWidth) {
        this.backgroundWidth = backgroundWidth;
    }
}
