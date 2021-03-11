package me.danetnaverno.inventorio.mixin;

import me.danetnaverno.inventorio.container.ExternalInventoryHandlerAddon;
import me.danetnaverno.inventorio.duck.HandlerDuck;
import me.danetnaverno.inventorio.player.PlayerScreenHandlerAddon;
import me.danetnaverno.inventorio.util.ScreenHandlerAddon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantConditions")
@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin implements HandlerDuck
{
    @Shadow protected abstract Slot addSlot(Slot slot);

    @Unique public ScreenHandlerAddon addon;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void createHandlerAddon(@Nullable ScreenHandlerType<?> type, int syncId, CallbackInfo ci)
    {
        if (!((Object) this instanceof PlayerScreenHandler))
            addon = new ExternalInventoryHandlerAddon((ScreenHandler) (Object) this);
    }

    @Inject(method = "onSlotClick", at = @At(value = "HEAD"), cancellable = true)
    private void onAddonSlotClick(int slotIndex, int clickData, SlotActionType actionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir)
    {
        ItemStack result = ((HandlerDuck)this).getAddon().onSlotClick(slotIndex, clickData, actionType, playerEntity);
        if (result != null)
            cir.setReturnValue(result);
    }

    @Inject(method = "onSlotClick", at = @At(value = "RETURN"))
    private void postSlotClick(int slotIndex, int clickData, SlotActionType actionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir)
    {
        if ((Object) this instanceof PlayerScreenHandler)
            ((PlayerScreenHandlerAddon)(((HandlerDuck)this).getAddon())).considerCheckingCapacity(slotIndex);
    }

    @Inject(method = "addSlot", at = @At(value = "HEAD"), cancellable = true)
    private void addSlotInject(Slot slot, CallbackInfoReturnable<Slot> cir)
    {
        if (!tryInitAddon(this, slot))
        {
            cir.setReturnValue(slot);
            cir.cancel();
        }
    }

    private boolean tryInitAddon(Object object, Slot slot)
    {
        if (object instanceof PlayerScreenHandler)
            return true;
        return ((HandlerDuck)object).getAddon().tryInitialize(slot);
    }

    @Override
    public ScreenHandlerAddon getAddon()
    {
        return addon;
    }

    @Override
    public void setAddon(ScreenHandlerAddon addon)
    {
        this.addon = addon;
    }
}