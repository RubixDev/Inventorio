package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.client.control.InventorioKeyHandler;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import me.lizardofoz.inventorio.util.MixinHelpers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerInventory.class, priority = -5000)
public abstract class PlayerInventoryMixin
{
    @Shadow @Final public PlayerEntity player;

    /**
     * This mixin causes inventory addon to get copied when the main inventory is
     * (e.g. playing dying with /gamerule keepInventory true or going from the End to the Overworld)
     */
    @Inject(method = "clone", at = @At(value = "RETURN"))
    private void inventorioClonePlayerInventory(PlayerInventory sourceInventory, CallbackInfo ci)
    {
        MixinHelpers.withInventoryAddon(player, thisAddon -> MixinHelpers.withInventoryAddon(sourceInventory.player, thisAddon::cloneFrom));
    }

    @Inject(method = "getMainHandStack", at = @At(value = "RETURN"), cancellable = true)
    private void inventorioGetMainHandStack(CallbackInfoReturnable<ItemStack> cir)
    {
        MixinHelpers.withInventoryAddon(player, inventorioAddon -> {
            ItemStack mainHandStack = inventorioAddon.getDisplayedMainHandStack();
            if (mainHandStack != null)
                cir.setReturnValue(mainHandStack);
        });
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "RETURN"), cancellable = true)
    private void inventorioGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir)
    {
        MixinHelpers.withInventoryAddon(player, inventorioAddon -> cir.setReturnValue(inventorioAddon.getMiningSpeedMultiplier(block)));
    }

    /**
     * Here's how it works: when an item is getting inserted into player's inventory (e.g. by picking up an item from the ground),
     * it will find a similar incomplete stack in the Deep Pockets and Utility Belt first.
     * If none is present, it will go through vanilla logic, and if the Main Inventory is full, it will find a free slot in the Deep Pockets
     */
    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void inventorioInsertSimilarStackIntoAddon(int slot, ItemStack originalStack, CallbackInfoReturnable<Boolean> cir)
    {
        MixinHelpers.withInventoryAddon(player, inventorioAddon -> {
            if (slot == -1 && inventorioAddon != null && inventorioAddon.insertOnlySimilarStack(originalStack))
                cir.setReturnValue(true);
        });
    }

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At(value = "RETURN"), cancellable = true)
    private void inventorioInsertStackIntoAddon(int slot, ItemStack originalStack, CallbackInfoReturnable<Boolean> cir)
    {
        MixinHelpers.withInventoryAddon(player, inventorioAddon -> {
            if (slot == -1 && !cir.getReturnValue() && inventorioAddon != null && inventorioAddon.insertStackIntoEmptySlot(originalStack))
                cir.setReturnValue(true);
        });
    }

    @Inject(method = "removeOne", at = @At(value = "HEAD"), cancellable = true)
    private void inventorioRemoveOneFromAddon(ItemStack stack, CallbackInfo ci)
    {
        MixinHelpers.withInventoryAddon(player, inventorioAddon -> {
            if (inventorioAddon != null && inventorioAddon.removeOne(stack))
                ci.cancel();
        });
    }

    @Inject(method = "dropAll", at = @At(value = "RETURN"))
    private void inventorioDropAllFromAddon(CallbackInfo ci)
    {
        MixinHelpers.withInventoryAddon(player, PlayerInventoryAddon::dropAll);
    }

    @Inject(method = "clear", at = @At(value = "RETURN"))
    private void inventorioClearAddon(CallbackInfo ci)
    {
        MixinHelpers.withInventoryAddon(player, SimpleInventory::clear);
    }

    /**
     * If player has a "scroll utility belt with a mouse wheel" setting enabled, hijack the vanilla hotbar scrolling
     * and scroll the utility belt instead.
     * Otherwise, set the selected hotbar segment value to -1 in case if a player scrolls a mouse wheel while using Segmented Hotbar
     */
    @Inject(method = "scrollInHotbar", at = @At(value = "HEAD"), cancellable = true)
    @Environment(EnvType.CLIENT)
    private void inventorioScrollInHotbar(double scrollAmount, CallbackInfo ci)
    {
        if (InventorioKeyHandler.INSTANCE.scrollInHotbar(player, scrollAmount))
            ci.cancel();
    }
}