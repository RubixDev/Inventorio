package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.ui.HotbarHUDRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 500)
@Environment(EnvType.CLIENT)
public class InGameHudMixinLP
{
    /**
     * This mixin redirects rendering the hotbar itself in case if Segmented Hotbar is selected.
     *
     * Forge has something going on with the "InGameHud#render" method that excludes the ability to inject into it.
     * While injecting there is better for mod compatibility than this solution, Forge forces my hand to do THIS.
     * P.S. Forge's event for hotbar rendering is uncancellable, thus, not an option.
     */
    @Inject(method = "renderHotbar", at = @At(value = "HEAD"), cancellable = true, require = 0)
    private void inventorioRenderSegmentedHotbar(float tickDelta, MatrixStack matrixStack, CallbackInfo ci)
    {
        if (HotbarHUDRenderer.INSTANCE.renderSegmentedHotbar(matrixStack))
            ci.cancel();
    }
}