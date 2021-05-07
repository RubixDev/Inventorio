package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.util.GeneralConstantsKt;
import me.lizardofoz.inventorio.util.ScreenHandlerDuck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
@SuppressWarnings("ConstantConditions")
public class ScreenHandlerMixin
{
    @Inject(method = "onSlotClick", at = @At(value = "HEAD"), cancellable = true)
    private void onAddonSlotClick(int slotIndex, int clickData, SlotActionType actionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir)
    {
        if (!((Object) this instanceof PlayerScreenHandler))
            return;
        ItemStack result = ((ScreenHandlerDuck)this).getAddon().onSlotClick(slotIndex, clickData, actionType, playerEntity);
        if (result != null)
            cir.setReturnValue(result);
    }

    @Inject(method = "onSlotClick", at = @At(value = "RETURN"))
    private void postSlotClick(int slotIndex, int clickData, SlotActionType actionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir)
    {
        if (slotIndex >= GeneralConstantsKt.getHANDLER_ARMOR_RANGE().getFirst()
                && slotIndex <= GeneralConstantsKt.getHANDLER_ARMOR_RANGE().getLast()
                && (Object) this instanceof PlayerScreenHandler)
            ((ScreenHandlerDuck)this).getAddon().checkDeepPocketsCapacity();
    }
}