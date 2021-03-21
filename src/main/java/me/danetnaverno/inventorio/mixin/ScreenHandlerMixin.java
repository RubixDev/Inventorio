package me.danetnaverno.inventorio.mixin;

import me.danetnaverno.inventorio.container.ExternalScreenHandlerAddon;
import me.danetnaverno.inventorio.player.PlayerScreenHandlerAddon;
import me.danetnaverno.inventorio.util.HandlerDuck;
import me.danetnaverno.inventorio.util.ScreenHandlerAddon;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
@SuppressWarnings("ConstantConditions")
public class ScreenHandlerMixin implements HandlerDuck
{
    @Unique public ScreenHandlerAddon addon;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void createHandlerAddon(@Nullable ScreenHandlerType<?> type, int syncId, CallbackInfo ci)
    {
        Object thisObject = this;
        if (!(thisObject instanceof PlayerScreenHandler) && !(thisObject instanceof CreativeInventoryScreen.CreativeScreenHandler))
            addon = new ExternalScreenHandlerAddon((ScreenHandler) (Object) this);
    }

    @Inject(method = "onSlotClick", at = @At(value = "HEAD"), cancellable = true)
    private void onAddonSlotClick(int slotIndex, int clickData, SlotActionType actionType, PlayerEntity playerEntity, CallbackInfoReturnable<ItemStack> cir)
    {
        ScreenHandlerAddon addon = ((HandlerDuck)this).getAddon();
        if (addon == null)
            return;
        ItemStack result = addon.onSlotClick(slotIndex, clickData, actionType, playerEntity);
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
        if (object instanceof PlayerScreenHandler || object instanceof CreativeInventoryScreen.CreativeScreenHandler)
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