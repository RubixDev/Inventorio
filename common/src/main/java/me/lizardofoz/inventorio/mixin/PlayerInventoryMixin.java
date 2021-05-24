package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.client.config.InventorioConfig;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import me.lizardofoz.inventorio.util.InventoryDuck;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerInventory.class, priority = -5000)
public abstract class PlayerInventoryMixin implements InventoryDuck
{
    @Unique public PlayerInventoryAddon inventorioAddon;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private <E> void inventorioCreateInventoryAddon(PlayerEntity player, CallbackInfo ci)
    {
        if (player != null)
            inventorioAddon = new PlayerInventoryAddon(player);
    }

    @Inject(method = "clone", at = @At(value = "RETURN"))
    public void inventorioClonePlayerInventory(PlayerInventory other, CallbackInfo ci)
    {
        if (inventorioAddon == null)
            return;
        PlayerInventoryAddon oldAddon = PlayerInventoryAddon.Companion.getInventoryAddon(other.player);
        if (oldAddon == null)
            return;
        inventorioAddon.cloneFrom(oldAddon);
    }

    @Inject(method = "getMainHandStack", at = @At(value = "RETURN"), cancellable = true)
    public void inventorioGetMainHandStack(CallbackInfoReturnable<ItemStack> cir)
    {
        if (inventorioAddon == null)
            return;
        ItemStack displayTool = inventorioAddon.getMainHandStack();
        if (displayTool != null)
            cir.setReturnValue(displayTool);
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "RETURN"), cancellable = true)
    public void inventorioGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir)
    {
        if (inventorioAddon == null)
            return;
        float addonValue = inventorioAddon.getMiningSpeedMultiplier(block);
        if (cir.getReturnValue() < addonValue)
            cir.setReturnValue(addonValue);
    }

    /**
     * Here's how it works: when an item is getting inserted into player's inventory (e.g. by picking up an item from the ground),
     * it will find a similar incomplete stack in the Deep Pockets and Utility Belt first.
     * If none is present, it will go through vanilla logic, and if the Main Inventory is full, it will find a free slot in the Deep Pockets
     */
    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At(value = "HEAD"), cancellable = true)
    public void inventorioInsertSimilarStackIntoAddon(int slot, ItemStack originalStack, CallbackInfoReturnable<Boolean> cir)
    {
        if (slot == -1 && inventorioAddon != null && inventorioAddon.insertOnlySimilarStack(originalStack))
            cir.setReturnValue(true);
    }

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At(value = "RETURN"), cancellable = true)
    public void inventorioInsertStackIntoAddon(int slot, ItemStack originalStack, CallbackInfoReturnable<Boolean> cir)
    {
        if (slot == -1 && !cir.getReturnValue() && inventorioAddon != null && inventorioAddon.insertStackIntoEmptySlot(originalStack))
            cir.setReturnValue(true);
    }

    @Inject(method = "removeOne", at = @At(value = "HEAD"), cancellable = true)
    public void inventorioRemoveOneFromAddon(ItemStack stack, CallbackInfo ci)
    {
        if (inventorioAddon != null && inventorioAddon.removeOne(stack))
            ci.cancel();
    }

    @Inject(method = "dropAll", at = @At(value = "RETURN"), cancellable = true)
    public void inventorioDropAllFromAddon(CallbackInfo ci)
    {
        if (inventorioAddon != null)
            inventorioAddon.dropAll();
    }

    /**
     * If player has a "scroll utility belt with a mouse wheel" setting enabled, hijack the vanilla hotbar scrolling
     *   and scroll the utility belt instead.
     * Otherwise, set the selected hotbar segment value to -1 in case if a player scrolls a mouse wheel while using Segmented Hotbar
     */
    @Inject(method = "scrollInHotbar", at = @At(value = "HEAD"), cancellable = true)
    @Environment(EnvType.CLIENT)
    public void inventorioScrollInHotbar(double scrollAmount, CallbackInfo ci)
    {
        if (InventorioConfig.INSTANCE.getScrollWheelUtilityBelt() && inventorioAddon != null)
        {
            inventorioAddon.switchToNextUtility((int) scrollAmount);
            ci.cancel();
        }
        else
            PlayerInventoryAddon.Client.INSTANCE.setSelectedHotbarSection(-1);
    }

    @Override
    public PlayerInventoryAddon getInventorioAddon()
    {
        return inventorioAddon;
    }
}