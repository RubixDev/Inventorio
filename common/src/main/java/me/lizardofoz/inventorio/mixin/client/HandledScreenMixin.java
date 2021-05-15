package me.lizardofoz.inventorio.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HandledScreen.class)
@Environment(EnvType.CLIENT)
public class HandledScreenMixin
{
    /**
     * This is one of several injects to remove the offhand swap hotkey
     */
    @Inject(method = "method_30107",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
                    ordinal = 0),
            cancellable = true)
    private void inventorioRemoveOffhandSwap(int i, CallbackInfo ci)
    {
        ci.cancel();
    }

    /**
     * This is one of several injects to remove the offhand swap hotkey
     */
    @Inject(method = "handleHotbarKeyPressed",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
                    ordinal = 0),
            cancellable = true)
    private void inventorioRemoveOffhandSwap(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(false);
    }
}