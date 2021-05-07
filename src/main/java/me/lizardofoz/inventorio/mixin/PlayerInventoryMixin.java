package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import me.lizardofoz.inventorio.util.InventoryDuck;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerInventory.class, priority = -9999)
public abstract class PlayerInventoryMixin implements InventoryDuck
{
    @Shadow @Final public PlayerEntity player;
    @Unique public PlayerInventoryAddon addon;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private <E> void createInventoryAddon(PlayerEntity player, CallbackInfo ci)
    {
        addon = new PlayerInventoryAddon(this.player);
    }

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
        if (cir.getReturnValue() == 40 || cir.getReturnValue() == -1)
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

    /**
     * When we scroll in hotbar, we want to reset the "simplified hotbar" thing and select a slot directly.
     */
    @Inject(method = "scrollInHotbar", at = @At(value = "RETURN"), cancellable = true)
    public void getBlockBreakingSpeed(double scrollAmount, CallbackInfo ci)
    {
        PlayerInventoryAddon.Client.INSTANCE.setSelectedHotBarSection(-1);
    }

    @Override
    public PlayerInventoryAddon getAddon()
    {
        return addon;
    }
}