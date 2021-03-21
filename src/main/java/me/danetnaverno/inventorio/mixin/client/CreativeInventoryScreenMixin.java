package me.danetnaverno.inventorio.mixin.client;

import me.danetnaverno.inventorio.client.inventory.CreativeInventoryScreenAddon;
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

@Mixin(CreativeInventoryScreen.class)
@Environment(EnvType.CLIENT)
public class CreativeInventoryScreenMixin
{
    @Shadow @Nullable private List<Slot> slots;

    @Shadow private float scrollPosition;

    @Inject(method = "init", at = @At(value = "HEAD"))
    private void init(CallbackInfo ci)
    {
        CreativeInventoryScreenAddon.INSTANCE.init((CreativeInventoryScreen) (Object) this);
    }

    @Inject(method = "setSelectedTab", at = @At(value = "RETURN"))
    private void setSelectedTab(ItemGroup group, CallbackInfo ci)
    {
        slots = CreativeInventoryScreenAddon.INSTANCE.setSelectedTab(slots, group);
    }

    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0))
    public void drawScreenAddon(CreativeInventoryScreen creativeInventoryScreen, MatrixStack matrices, int x, int y, int u, int v, int width, int height)
    {
        CreativeInventoryScreenAddon.INSTANCE.drawAddon(matrices);
    }

    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 1))
    public void drawScrollBar(CreativeInventoryScreen creativeInventoryScreen, MatrixStack matrices, int x, int y, int u, int v, int width, int height)
    {
        CreativeInventoryScreenAddon.INSTANCE.drawScrollBar(scrollPosition, matrices);
    }
}
