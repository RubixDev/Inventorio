package me.lizardofoz.inventorio.mixin.client.screen;

import me.lizardofoz.inventorio.client.ui.ExternalInventoryUIAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
@Environment(EnvType.CLIENT)
public class HandledScreenMixin
{
    @Shadow protected int backgroundWidth;
    @Shadow protected int backgroundHeight;

    @Inject(method = "render", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawBackground(Lnet/minecraft/client/util/math/MatrixStack;FII)V", ordinal = 0))
    public void drawAddon(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        HandledScreen thisScreen = (HandledScreen)(Object)this;
        if (!(thisScreen instanceof InventoryScreen) && !(thisScreen instanceof CreativeInventoryScreen))
            ExternalInventoryUIAddon.INSTANCE.drawAddon(thisScreen, matrices, mouseX, mouseY);
    }

    @Overwrite
    public boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button)
    {
        //todo
        return mouseX < (double) left - 100
                || mouseY < (double) top
                || mouseX >= (double) (left + this.backgroundWidth) + 100
                || mouseY >= (double) (top + this.backgroundHeight);
    }
}
