package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.config.GlobalSettings;
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon;
import me.lizardofoz.inventorio.util.ScreenHandlerDuck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin fixes shift-clicks and other inventory shortcuts for Player's inventory
 */
@Mixin(value = PlayerScreenHandler.class, priority = 9999)
public class PlayerScreenHandlerMixin implements ScreenHandlerDuck
{
    @Unique public PlayerScreenHandlerAddon inventorioAddon;

    @SuppressWarnings({"ConstantConditions", "EqualsBetweenInconvertibleTypes"})
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void inventorioCreateScreenHandlerAddon(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci)
    {
        if (GlobalSettings.ignoreModdedHandlers.getBoolValue() && !PlayerScreenHandler.class.equals(this.getClass()))
            return;
        if (owner != null)
            inventorioAddon = new PlayerScreenHandlerAddon((PlayerScreenHandler) (Object) this, owner);
    }

    @Inject(method = "transferSlot", at = @At("HEAD"), cancellable = true)
    private void inventorioTransferSlot(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir)
    {
        if (inventorioAddon == null)
            return;
        ItemStack result = inventorioAddon.transferSlot(index);
        if (result != null)
            cir.setReturnValue(result);
    }

    @Override
    public PlayerScreenHandlerAddon getScreenHandlerAddon()
    {
        return inventorioAddon;
    }

    @Override
    public void setScreenHandlerAddon(PlayerScreenHandlerAddon addon)
    {
        this.inventorioAddon = addon;
    }
}