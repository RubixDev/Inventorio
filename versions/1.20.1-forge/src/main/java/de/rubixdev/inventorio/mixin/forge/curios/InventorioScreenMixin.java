package de.rubixdev.inventorio.mixin.forge.curios;

import de.rubixdev.inventorio.client.ui.InventorioScreen;
import de.rubixdev.inventorio.integration.curios.ICuriosScreen;
import de.rubixdev.inventorio.integration.curios.InventorioScreenMixinHelper;
import de.rubixdev.inventorio.player.InventorioScreenHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnresolvedMixinReference") // the Minecraft Dev plugin
                                              // doesn't seem to like Kotlin
                                              // target classes
@Restriction(require = @Condition("curios"))
@Mixin(InventorioScreen.class)
public abstract class InventorioScreenMixin extends AbstractInventoryScreen<InventorioScreenHandler>
    implements ICuriosScreen {
    public InventorioScreenMixin(InventorioScreenHandler arg, PlayerInventory arg2, Text arg3) {
        super(arg, arg2, arg3);
    }

    @Shadow
    private RecipeBookWidget recipeBook;

    @SuppressWarnings("DataFlowIssue")
    @Unique private final InventorioScreen thiz = (InventorioScreen) (AbstractInventoryScreen<?>) this;

    @Unique private InventorioScreenMixinHelper helper;

    @Override
    public void inventorio$updateRenderButtons() {
        helper.curios$updateRenderButtons(thiz);
    }

    @Override
    public boolean getInventorio$isCuriosOpen() { return helper.isCuriosOpen(); }

    @Inject(method = "init", at = @At("RETURN"))
    private void curios$init(CallbackInfo ci) {
        helper = new InventorioScreenMixinHelper(thiz, recipeBook);
        helper.curios$init(thiz);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void curios$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        helper.curios$render(thiz, context, mouseX, mouseY, delta);
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("RETURN"))
    private void curios$drawMouseoverTooltip(DrawContext context, int x, int y, CallbackInfo ci) {
        helper.drawMouseoverTooltip(context, x, y);
    }

    @Inject(method = "drawBackground", at = @At("RETURN"))
    private void curios$drawBackground(DrawContext drawContext, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        helper.drawBackground(drawContext);
    }

    @Inject(method = "isPointWithinBounds", at = @At("HEAD"), cancellable = true)
    private void curios$isPointWithinBounds(
        int xPosition,
        int yPosition,
        int width,
        int height,
        double pointX,
        double pointY,
        CallbackInfoReturnable<Boolean> cir
    ) {
        helper.isPointWithinBounds(cir);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void curios$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        helper.mouseClicked(mouseX, mouseY, cir);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void curios$mouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        helper.mouseReleased(button, cir);
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void curios$mouseDragged(
        double mouseX,
        double mouseY,
        int button,
        double deltaX,
        double deltaY,
        CallbackInfoReturnable<Boolean> cir
    ) {
        helper.mouseDragged(mouseY, cir);
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void curios$mouseScrolled(
        double mouseX,
        double mouseY,
        double verticalAmount,
        CallbackInfoReturnable<Boolean> cir
    ) {
        helper.mouseScrolled(verticalAmount, cir);
    }

    @Inject(
        method = "init$lambda$0",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeBookWidget;toggleOpen()V",
            shift = At.Shift.AFTER
        )
    )
    private static void curios$hideCuriosWhenOpeningRecipeBook(
        InventorioScreen instance,
        ButtonWidget it,
        CallbackInfo ci
    ) {
        // noinspection DataFlowIssue
        ((InventorioScreenMixin) (HandledScreen<?>) instance).helper.curios$hideCuriosWhenOpeningRecipeBook(instance);
    }
}
