package me.lizardofoz.inventorio.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = HandledScreen.class, priority = 9999)
@Environment(EnvType.CLIENT)
public class HandledScreenMixin
{
    /**
     * This is one of several redirects to remove the offhand swap hotkey
     */
    @Redirect(method = "method_30107", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", ordinal = 0))
    private void removeOffhandSwap1(HandledScreen<?> handledScreen, Slot slot, int invSlot, int clickData, SlotActionType actionType)
    {
    }

    /**
     * This is one of several redirects to remove the offhand swap hotkey
     */
    @Redirect(method = "handleHotbarKeyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", ordinal = 0))
    private void removeOffhandSwap2(HandledScreen<?> handledScreen, Slot slot, int invSlot, int clickData, SlotActionType actionType)
    {
    }
}