package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.util.MixinHelpers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HandledScreen.class)
@Environment(EnvType.CLIENT)
public class HandledScreenMixin
{
    @Shadow @Nullable protected Slot focusedSlot;

    /**
     * This inject allows to use the vanilla Swap Offhand key (F by default)
     * to move items to the Utility Belt in the inventory
     * (the injected method is an edge-case handler when you have "swap offhand" on mouse buttons)
     */
    @Inject(method = "method_30107",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
                    ordinal = 0),
            cancellable = true)
    private void inventorioOffhandSwapWithMouse(int i, CallbackInfo ci)
    {
        MixinHelpers.withScreenHandler(MinecraftClient.getInstance().player, screenHandler -> screenHandler.tryTransferToUtilityBeltSlot(focusedSlot));
        ci.cancel();
    }

    /**
     * This inject allows to use the vanilla Swap Offhand key (F by default)
     * to move items to the Utility Belt in the inventory
     */
    @Inject(method = "handleHotbarKeyPressed",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
                    ordinal = 0),
            cancellable = true)
    private void inventorioOffhandSwapWithKeyboard(int keyCode, int scanCode, CallbackInfoReturnable<Boolean> cir)
    {
        boolean[] result = new boolean[1];
        MixinHelpers.withScreenHandler(MinecraftClient.getInstance().player, screenHandler -> result[0] = screenHandler.tryTransferToUtilityBeltSlot(focusedSlot));
        cir.setReturnValue(result[0]);
    }
}