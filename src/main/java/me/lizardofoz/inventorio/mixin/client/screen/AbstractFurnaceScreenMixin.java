package me.lizardofoz.inventorio.mixin.client.screen;

import me.lizardofoz.inventorio.client.ui.ExternalInventoryUIAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceScreen.class)
@Environment(EnvType.CLIENT)
public class AbstractFurnaceScreenMixin
{
    @Inject(method = "render", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/gui/screen/ingame/AbstractFurnaceScreen;drawBackground(Lnet/minecraft/client/util/math/MatrixStack;FII)V", ordinal = 0))
    public void drawAddon(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        ExternalInventoryUIAddon.INSTANCE.drawAddon((HandledScreen)(Object)this, matrices, mouseX, mouseY);
    }
}
