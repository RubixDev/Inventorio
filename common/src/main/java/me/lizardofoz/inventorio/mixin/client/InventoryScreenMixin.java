package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.ui.PlayerInventoryUIAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin attaches {@link PlayerInventoryUIAddon} to {@link InventoryScreen} (Player's inventory screen)
 */
@Mixin(InventoryScreen.class)
@Environment(EnvType.CLIENT)
public class InventoryScreenMixin
{
    @Shadow @Final private RecipeBookWidget recipeBook;
    @Shadow private boolean narrow;

    @Inject(method = "init", at = @At(value = "HEAD"))
    private void inventorioInitScreenAddon(CallbackInfo ci)
    {
        PlayerInventoryUIAddon.INSTANCE.init((InventoryScreen) (Object) this);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void inventorioPostInitScreenAddon(CallbackInfo ci)
    {
        PlayerInventoryUIAddon.INSTANCE.postInit();
    }

    /**
     * This mixin redirects the creation of a recipe book widget button
     */
    @Redirect(method = "init",
            at = @At(value = "NEW",
                    target = "net/minecraft/client/gui/widget/TexturedButtonWidget"))
    public TexturedButtonWidget inventorioRedirectAddButton(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, ButtonWidget.PressAction pressAction)
    {
        return PlayerInventoryUIAddon.INSTANCE.makeWidgetButton((InventoryScreen) (Object) this, this.recipeBook, this.narrow);
    }

    /**
     * This inject draws the modified player interface. We use an @Inject instead of @Redirect for the mod compatibility sake.
     */
    @Inject(method = "drawBackground",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawEntity(IIIFFLnet/minecraft/entity/LivingEntity;)V"))
    public void inventorioDrawScreenAddon(MatrixStack matrices, float delta, int mouseX, int mouseY, CallbackInfo ci)
    {
        PlayerInventoryUIAddon.INSTANCE.drawAddon(matrices);
    }
}
