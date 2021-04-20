package me.lizardofoz.inventorio.mixin;

import com.mojang.authlib.GameProfile;
import me.lizardofoz.inventorio.mixin.accessor.SimpleInventoryAccessor;
import me.lizardofoz.inventorio.player.InventorioPlayerSerializer;
import me.lizardofoz.inventorio.player.PlayerAddon;
import me.lizardofoz.inventorio.screenhandler.PlayerScreenHandlerAddon;
import me.lizardofoz.inventorio.util.GeneralConstantsKt;
import me.lizardofoz.inventorio.util.HandlerDuck;
import me.lizardofoz.inventorio.util.PlayerDuck;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerDuck
{
    @Unique public PlayerAddon addon;
    @Shadow @Final public PlayerInventory inventory;
    @Shadow public abstract EnderChestInventory getEnderChestInventory();

    /**
     * This inject attaches a {@link PlayerAddon} to a {@link PlayerEntity}
     * and {@link PlayerScreenHandlerAddon} to a {@link PlayerScreenHandler}
     */
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void createPlayerAddon(World world, BlockPos pos, float yaw, GameProfile profile, CallbackInfo ci)
    {
        PlayerEntity thisPlayer = (PlayerEntity)(Object)this;
        addon = PlayerAddon.Companion.create(thisPlayer);
        PlayerScreenHandlerAddon screenAddon = new PlayerScreenHandlerAddon(thisPlayer.playerScreenHandler);
        ((HandlerDuck)thisPlayer.playerScreenHandler).setAddon(screenAddon);
        screenAddon.initialize(addon);
    }

    /**
     * This inject enlarges the Ender Chest's capacity to 6 rows.
     */
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    protected void resizeEnderChest(World world, BlockPos pos, float yaw, GameProfile profile, CallbackInfo ci)
    {
        SimpleInventoryAccessor accessor = ((SimpleInventoryAccessor) getEnderChestInventory());
        accessor.setSize(GeneralConstantsKt.VANILLA_ROW_LENGTH * 6);
        accessor.setStacks(DefaultedList.ofSize(GeneralConstantsKt.VANILLA_ROW_LENGTH * 6, ItemStack.EMPTY));
    }

    /**
     * This inject causes the selected QuickBar item to be displayed in the main hand
     */
    @Inject(method = "equipStack", at = @At(value = "JUMP"), cancellable = true)
    private void makeMainHandWorkWithQuickBar(EquipmentSlot slot, ItemStack stack, CallbackInfo ci)
    {
        if (slot == EquipmentSlot.MAINHAND)
            addon.getInventoryAddon().getShortcutQuickBar().setStack(inventory.selectedSlot, stack);
        else if (slot == EquipmentSlot.OFFHAND)
            ci.cancel();
    }

    /**
     * This inject causes the selected UtilityBelt item to be displayed in the offhand
     */
    @Inject(method = "getEquippedStack", at = @At(value = "HEAD"), cancellable = true)
    private void displayUtilityInOffhand(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir)
    {
        if (slot == EquipmentSlot.OFFHAND)
        {
            int index = GeneralConstantsKt.getUTILITY_BELT_RANGE().getFirst() + addon.getInventoryAddon().getSelectedUtility();
            ItemStack stack = inventory.getStack(index);
            cir.setReturnValue(stack);
        }
    }

    @Redirect(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0))
    private boolean removeBadCheck(ItemStack itemStack)
    {
        return false;
    }

    /**
     * These 2 injects cause a correct weapon to be automatically selected and withdrawn upon attack
     */
    @Inject(method = "attack", at = @At(value = "HEAD"))
    private void preAttack(Entity target, CallbackInfo ci)
    {
        if (target.isAttackable())
            addon.getInventoryAddon().prePlayerAttack(target);
    }

    @Inject(method = "attack", at = @At(value = "RETURN"))
    private void postAttack(Entity target, CallbackInfo ci)
    {
        if (target.isAttackable())
            addon.getInventoryAddon().postPlayerAttack(target);
    }

    /**
     * These 2 injects read and write additional data into Player's NBT
     */
    @Inject(method = "readCustomDataFromTag", at = @At(value = "RETURN"))
    private void deserializePlayerAddon(CompoundTag tag, CallbackInfo ci)
    {
        InventorioPlayerSerializer.INSTANCE.deserialize(addon, tag.getCompound("Inventorio"));
    }

    @Inject(method = "writeCustomDataToTag", at = @At(value = "RETURN"))
    private void serializePlayerAddon(CompoundTag tag, CallbackInfo ci)
    {
        CompoundTag inventorioTag = new CompoundTag();
        InventorioPlayerSerializer.INSTANCE.serialize(addon, inventorioTag);
        tag.put("Inventorio", inventorioTag);
    }

    public PlayerAddon getAddon()
    {
        return addon;
    }
}