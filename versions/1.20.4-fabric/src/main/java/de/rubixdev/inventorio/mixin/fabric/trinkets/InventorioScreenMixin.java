package de.rubixdev.inventorio.mixin.fabric.trinkets;

import de.rubixdev.inventorio.client.ui.InventorioScreen;
import de.rubixdev.inventorio.player.InventorioScreenHandler;
import dev.emi.trinkets.Point;
import dev.emi.trinkets.TrinketPlayerScreenHandler;
import dev.emi.trinkets.TrinketScreen;
import dev.emi.trinkets.TrinketScreenManager;
import dev.emi.trinkets.api.SlotGroup;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnresolvedMixinReference") // the Minecraft Dev plugin
                                              // doesn't seem to like Kotlin
                                              // target classes
@Restriction(require = @Condition("trinkets"))
@Mixin(InventorioScreen.class)
public abstract class InventorioScreenMixin extends AbstractInventoryScreen<InventorioScreenHandler>
    implements RecipeBookProvider, TrinketScreen {
    public InventorioScreenMixin(InventorioScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void trinkets$init(CallbackInfo ci) {
        TrinketScreenManager.init(this);
    }

    @Inject(method = "handledScreenTick", at = @At("TAIL"))
    private void trinkets$tick(CallbackInfo ci) {
        TrinketScreenManager.tick();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void trinkets$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        TrinketScreenManager.update(mouseX, mouseY);
    }

    @Inject(method = "drawBackground", at = @At("RETURN"))
    private void trinkets$drawBackground(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        TrinketScreenManager.drawExtraGroups(context);
    }

    @Inject(method = "drawForeground", at = @At("RETURN"))
    private void trinkets$drawForeground(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        TrinketScreenManager.drawActiveGroup(context);
    }

    @Inject(method = "isClickOutsideBounds", at = @At("HEAD"), cancellable = true)
    private void trinkets$isClickOutsideBounds(
        double mouseX,
        double mouseY,
        int left,
        int top,
        int button,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (TrinketScreenManager.isClickInsideTrinketBounds(mouseX, mouseY)) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public TrinketPlayerScreenHandler trinkets$getHandler() {
        // noinspection DataFlowIssue
        return (TrinketPlayerScreenHandler) (ScreenHandler) handler;
    }

    @Override
    public Rect2i trinkets$getGroupRect(SlotGroup group) {
        Point pos = trinkets$getHandler().trinkets$getGroupPos(group);
        if (pos != null) { return new Rect2i(pos.x() - 1, pos.y() - 1, 17, 17); }
        return new Rect2i(0, 0, 0, 0);
    }

    @Override
    public Slot trinkets$getFocusedSlot() {
        return focusedSlot;
    }

    @Override
    public int trinkets$getX() {
        return x;
    }

    @Override
    public int trinkets$getY() {
        return y;
    }
}
