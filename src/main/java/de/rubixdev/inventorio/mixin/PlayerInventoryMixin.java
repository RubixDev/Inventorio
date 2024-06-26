package de.rubixdev.inventorio.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.rubixdev.inventorio.client.control.InventorioKeyHandler;
import de.rubixdev.inventorio.player.PlayerInventoryAddon;
import de.rubixdev.inventorio.util.MixinHelpers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerInventory.class, priority = -5000)
public abstract class PlayerInventoryMixin {
    @Shadow
    @Final
    public PlayerEntity player;

    /**
     * This mixin also copies inventory addon when the main inventory is copied
     * (e.g. when playing with `/gamerule keepInventory true` or going from the
     * End to the Overworld)
     */
    @Inject(method = "clone", at = @At(value = "RETURN"))
    private void inventorioClonePlayerInventory(PlayerInventory sourceInventory, CallbackInfo ci) {
        MixinHelpers.withInventoryAddon(
            player,
            thisAddon -> MixinHelpers.withInventoryAddon(sourceInventory.player, thisAddon::cloneFrom)
        );
    }

    @Inject(method = "getMainHandStack", at = @At(value = "RETURN"), cancellable = true)
    private void inventorioGetMainHandStack(CallbackInfoReturnable<ItemStack> cir) {
        MixinHelpers.withInventoryAddon(player, inventorioAddon -> {
            ItemStack mainHandStack = inventorioAddon.getDisplayedMainHandStack();
            if (mainHandStack != null) cir.setReturnValue(mainHandStack);
        });
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "RETURN"), cancellable = true)
    private void inventorioGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        MixinHelpers.withInventoryAddon(
            player,
            inventorioAddon -> cir.setReturnValue(inventorioAddon.getMiningSpeedMultiplier(block))
        );
    }

    /**
     * Here's how it works: when an item is getting inserted into a player's
     * inventory (e.g. by picking up an item from the ground), it will find a
     * similar incomplete stack in the Deep Pockets and Utility Belt first. If
     * none is present, it will go through vanilla logic, and if the Main
     * Inventory is full, it will find a free slot in the Deep Pockets.
     */
    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void inventorioInsertSimilarStackIntoAddon(
        int slot,
        ItemStack originalStack,
        CallbackInfoReturnable<Boolean> cir
    ) {
        MixinHelpers.withInventoryAddon(player, inventorioAddon -> {
            if (slot == -1 && inventorioAddon != null && inventorioAddon.insertOnlySimilarStack(originalStack))
                cir.setReturnValue(true);
        });
    }

    @ModifyReturnValue(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"))
    private boolean inventorioInsertStackIntoAddon(boolean original, int slot, ItemStack originalStack) {
        return original
            || Boolean.TRUE.equals(
                MixinHelpers.withInventoryAddonReturning(
                    player,
                    addon -> (slot == -1 && addon.insertStackIntoEmptySlot(originalStack))
                )
            );
    }

    @Inject(method = "removeOne", at = @At(value = "HEAD"), cancellable = true)
    private void inventorioRemoveOneFromAddon(ItemStack stack, CallbackInfo ci) {
        MixinHelpers.withInventoryAddon(player, inventorioAddon -> {
            if (inventorioAddon != null && inventorioAddon.removeOne(stack)) ci.cancel();
        });
    }

    @Inject(method = "dropAll", at = @At(value = "RETURN"))
    private void inventorioDropAllFromAddon(CallbackInfo ci) {
        MixinHelpers.withInventoryAddon(player, PlayerInventoryAddon::dropAll);
    }

    @Inject(method = "clear", at = @At(value = "RETURN"))
    private void inventorioClearAddon(CallbackInfo ci) {
        MixinHelpers.withInventoryAddon(player, SimpleInventory::clear);
    }

    /**
     * If player has a "scroll utility belt with a mouse wheel" setting enabled,
     * hijack the vanilla hotbar scrolling and scroll the utility belt instead.
     * Otherwise, set the selected hotbar segment value to -1 in case a player
     * scrolls a mouse wheel while using Segmented Hotbar
     */
    @Inject(method = "scrollInHotbar", at = @At(value = "HEAD"), cancellable = true)
    @Environment(EnvType.CLIENT)
    private void inventorioScrollInHotbar(double scrollAmount, CallbackInfo ci) {
        if (InventorioKeyHandler.INSTANCE.scrollInHotbar(player, scrollAmount)) ci.cancel();
    }

    @ModifyReturnValue(method = "contains(Lnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"))
    private boolean searchAddon(boolean original, ItemStack stack) {
        return original
            || Boolean.TRUE.equals(MixinHelpers.withInventoryAddonReturning(player, addon -> addon.contains(stack)));
    }

    @ModifyReturnValue(method = "contains(Lnet/minecraft/registry/tag/TagKey;)Z", at = @At("RETURN"))
    private boolean searchAddon(boolean original, TagKey<Item> tag) {
        return original
            || Boolean.TRUE.equals(MixinHelpers.withInventoryAddonReturning(player, addon -> addon.contains(tag)));
    }
}
