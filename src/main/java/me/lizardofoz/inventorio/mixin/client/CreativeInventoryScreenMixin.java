package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.ui.CreativeInventoryUIAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * This mixin attaches {@link CreativeInventoryUIAddon} to {@link CreativeInventoryScreen}
 */
@Mixin(CreativeInventoryScreen.class)
@Environment(EnvType.CLIENT)
public class CreativeInventoryScreenMixin
{
    @Shadow @Nullable private List<Slot> slots;
    @Shadow private float scrollPosition;
    @Shadow private @Nullable Slot deleteItemSlot;

    @Inject(method = "init", at = @At(value = "HEAD"))
    private void init(CallbackInfo ci)
    {
        CreativeInventoryUIAddon.INSTANCE.init((CreativeInventoryScreen) (Object) this);
    }

    @Inject(method = "setSelectedTab", at = @At(value = "RETURN"))
    private void setSelectedTab(ItemGroup group, CallbackInfo ci)
    {
        slots = CreativeInventoryUIAddon.INSTANCE.setSelectedTab(slots, group, deleteItemSlot);
    }

    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0))
    public void drawScreenAddon(CreativeInventoryScreen creativeInventoryScreen, MatrixStack matrices, int x, int y, int u, int v, int width, int height)
    {
        CreativeInventoryUIAddon.INSTANCE.drawAddon(matrices);
    }

    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 1))
    public void drawScrollBar(CreativeInventoryScreen creativeInventoryScreen, MatrixStack matrices, int x, int y, int u, int v, int width, int height)
    {
        CreativeInventoryUIAddon.INSTANCE.drawScrollBar(scrollPosition, matrices);
    }
}
