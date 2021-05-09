package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.util.ScreenHandlerDuck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin
{
    @Inject(method = "onSlotClick", at = @At(value = "RETURN"))
    private void postSlotClick(int slotIndex, int clickData, SlotActionType actionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir)
    {
        if (this instanceof ScreenHandlerDuck)
            ((ScreenHandlerDuck)this).getScreenHandlerAddon().postSlotClick(slotIndex);
    }
}