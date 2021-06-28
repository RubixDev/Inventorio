package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.ui.PlayerInventoryUIAddon;
import me.lizardofoz.inventorio.mixin.client.accessor.ScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin attaches {@link PlayerInventoryUIAddon} to {@link InventoryScreen} (Player's inventory screen)
 */
@Mixin(value = InventoryScreen.class, priority = 2000)
@Environment(EnvType.CLIENT)
public abstract class InventoryScreenMixin implements ScreenAccessor
{
    @Shadow @Final private RecipeBookWidget recipeBook;

    @Inject(method = "init", at = @At(value = "HEAD"))
    private void inventorioInitScreenAddon(CallbackInfo ci)
    {
        PlayerInventoryUIAddon.INSTANCE.init((InventoryScreen) (Object) this, recipeBook);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void inventorioPostInitScreenAddon(CallbackInfo ci)
    {
        PlayerInventoryUIAddon.INSTANCE.postInit(getButtons());
    }

    /**
     * This inject draws the modified player inventory interface. We use an @Inject instead of @Redirect for the mod compatibility sake.
     */
    @Inject(method = "drawBackground",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
            shift = At.Shift.AFTER))
    private void inventorioDrawScreenAddon(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
    {
        PlayerInventoryUIAddon.INSTANCE.drawAddon(matrices);
    }
}
