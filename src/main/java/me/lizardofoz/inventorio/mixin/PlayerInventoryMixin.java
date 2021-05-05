package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.mixin.accessor.PlayerInventoryAccessor;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import me.lizardofoz.inventorio.util.InventoryDuck;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This class is genuinely painful to look at and breaks all sorts of OOP conventions,
 *  but hey, this is what you gotta do when you deal with mixins, APIs and mod compatibility.
 */
@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin implements InventoryDuck
{
    @Shadow @Final public PlayerEntity player;
    @Unique public PlayerInventoryAddon addon;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private <E> void createInventoryAddon(PlayerEntity player, CallbackInfo ci)
    {
        addon = new PlayerInventoryAddon((PlayerInventory)(Object)this);
        ((PlayerInventoryAccessor)this).setCombinedInventory(addon.getCombinedInventory());
    }

    @Overwrite
    public int size()
    {
        return addon.size();
    }

    @Overwrite
    public ItemStack getMainHandStack()
    {
        return addon.getMainHandStack();
    }

    @Inject(method = "getEmptySlot", at = @At(value = "RETURN"), cancellable = true)
    public void getEmptySlot(CallbackInfoReturnable<Integer> cir)
    {
        //If no free slot is found in the main inventory, look in the Deep Pockets' extension
        if (cir.getReturnValue() == -1)
            cir.setReturnValue(addon.getEmptyExtensionSlot());
    }

    @Inject(method = "getOccupiedSlotWithRoomForStack", at = @At(value = "RETURN"), cancellable = true)
    public void getOccupiedSlotWithRoomForStack(ItemStack stack, CallbackInfoReturnable<Integer> cir)
    {
        //If no fitting slot is found in the main inventory, look in the Deep Pockets' extension
        if (cir.getReturnValue() == 40 || cir.getReturnValue() == -1)
            cir.setReturnValue(addon.getOccupiedExtensionSlotWithRoomForStack(stack));
    }

    @Overwrite
    public float getBlockBreakingSpeed(BlockState block)
    {
        return addon.getBlockBreakingSpeed(block);
    }

    @Override
    public PlayerInventoryAddon getAddon()
    {
        return addon;
    }
}
