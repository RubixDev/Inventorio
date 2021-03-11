package me.danetnaverno.inventorio.mixin.client;

import me.danetnaverno.inventorio.client.inventory.InventorioScreenAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
@Environment(EnvType.CLIENT)
public class InventoryScreenMixin
{
    @Inject(method = "init", at = @At(value = "HEAD"))
    private void initScreenAddon(CallbackInfo ci)
    {
        InventorioScreenAddon.INSTANCE.init((InventoryScreen)(Object)this);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void postInitScreenAddon(CallbackInfo ci)
    {
        InventorioScreenAddon.INSTANCE.postInit();
    }

    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    public void drawScreenAddon(InventoryScreen inventoryScreen, MatrixStack matrices, int x, int y, int u, int v, int width, int height)
    {
        InventorioScreenAddon.INSTANCE.drawAddon(matrices);
    }
}
