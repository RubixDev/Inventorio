package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.util.InventoryDuck;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This class is genuinely painful to look at and breaks all sorts of OOP conventions,
 *  but hey, this is what you gotta do when you deal with mixins, APIs and mod compatibility.
 */
@Mixin(value = PlayerInventory.class, priority = -9999)
public abstract class PlayerInventoryMixin2 implements InventoryDuck
{
    @Inject(method = "getMainHandStack", at = @At(value = "RETURN"), cancellable = true)
    public void getMainHandStack(CallbackInfoReturnable<ItemStack> cir)
    {
        ItemStack displayTool = getAddon().getMainHandStack();
        if (displayTool != null)
            cir.setReturnValue(displayTool);
    }

    @Inject(method = "getEmptySlot", at = @At(value = "RETURN"), cancellable = true)
    public void getEmptySlot(CallbackInfoReturnable<Integer> cir)
    {
        //If no free slot is found in the main inventory, look in the Deep Pockets' extension
        if (cir.getReturnValue() == -1)
            cir.setReturnValue(getAddon().getEmptyExtensionSlot());
    }

    @Inject(method = "getOccupiedSlotWithRoomForStack", at = @At(value = "RETURN"), cancellable = true)
    public void getOccupiedSlotWithRoomForStack(ItemStack stack, CallbackInfoReturnable<Integer> cir)
    {
        //If no fitting slot is found in the main inventory, look in the Deep Pockets' extension
        if (cir.getReturnValue() == 40 || cir.getReturnValue() == -1)
            cir.setReturnValue(getAddon().getOccupiedExtensionSlotWithRoomForStack(stack));
    }

    @Inject(method = "getBlockBreakingSpeed", at = @At(value = "RETURN"), cancellable = true)
    public void getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir)
    {
        float addonValue = getAddon().getBlockBreakingSpeed(block);
        if (cir.getReturnValue() < addonValue)
            cir.setReturnValue(addonValue);
    }
}