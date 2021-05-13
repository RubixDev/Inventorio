package me.lizardofoz.inventorio.mixin;

import com.mojang.authlib.GameProfile;
import me.lizardofoz.inventorio.mixin.accessor.SimpleInventoryAccessor;
import me.lizardofoz.inventorio.player.InventorioPlayerSerializer;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon;
import me.lizardofoz.inventorio.util.GeneralConstantsKt;
import me.lizardofoz.inventorio.util.InventoryDuck;
import me.lizardofoz.inventorio.util.ScreenHandlerDuck;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin
{
    @Shadow public abstract EnderChestInventory getEnderChestInventory();
    @Shadow @Final public PlayerInventory inventory;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void createPlayerAddon(World world, BlockPos pos, float yaw, GameProfile profile, CallbackInfo ci)
    {
        PlayerEntity thisPlayer = (PlayerEntity) (Object) this;
        PlayerScreenHandlerAddon screenAddon = new PlayerScreenHandlerAddon(thisPlayer.playerScreenHandler);
        ((ScreenHandlerDuck) thisPlayer.playerScreenHandler).setScreenHandlerAddon(screenAddon);
        screenAddon.initialize(thisPlayer);
    }

    /**
     * This inject enlarges the Ender Chest's capacity to 6 rows.
     */
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void resizeEnderChest(World world, BlockPos pos, float yaw, GameProfile profile, CallbackInfo ci)
    {
        SimpleInventoryAccessor accessor = ((SimpleInventoryAccessor) getEnderChestInventory());
        accessor.setSize(GeneralConstantsKt.VANILLA_ROW_LENGTH * 6);
        accessor.setStacks(DefaultedList.ofSize(GeneralConstantsKt.VANILLA_ROW_LENGTH * 6, ItemStack.EMPTY));
    }

    /**
     * This inject causes the selected UtilityBelt item to be displayed in the offhand
     */
    @Inject(method = "getEquippedStack", at = @At(value = "HEAD"), cancellable = true)
    private void displayUtilityInOffhand(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir)
    {
        if (slot == EquipmentSlot.OFFHAND)
            cir.setReturnValue(getAddon().getOffHandStack());
    }

    /**
     * This mixin refreshes the available slots when we equip armor through right clicking or a dispenser
     */
    @Inject(method = "equipStack", at = @At(value = "RETURN"))
    private void postAttack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci)
    {
        if (slot.getType() == EquipmentSlot.Type.ARMOR)
            PlayerScreenHandlerAddon.Companion.getScreenHandlerAddon(inventory.player).updateDeepPocketsCapacity();
    }

    /**
     * These 2 injects cause a correct weapon to be automatically selected and withdrawn upon attack
     */
    @Inject(method = "attack", at = @At(value = "HEAD"))
    private void preAttack(Entity target, CallbackInfo ci)
    {
        if (target.isAttackable())
            getAddon().prePlayerAttack();
    }

    @Inject(method = "attack", at = @At(value = "RETURN"))
    private void postAttack(Entity target, CallbackInfo ci)
    {
        if (target.isAttackable())
            getAddon().postPlayerAttack();
    }

    /**
     * These 2 injects read and write additional data into Player's NBT
     */
    @Inject(method = "readCustomDataFromTag", at = @At(value = "RETURN"))
    private void deserializePlayerAddon(CompoundTag tag, CallbackInfo ci)
    {
        InventorioPlayerSerializer.INSTANCE.deserialize(getAddon(), tag.getCompound("Inventorio"));
    }

    @Inject(method = "writeCustomDataToTag", at = @At(value = "RETURN"))
    private void serializePlayerAddon(CompoundTag tag, CallbackInfo ci)
    {
        CompoundTag inventorioTag = new CompoundTag();
        InventorioPlayerSerializer.INSTANCE.serialize(getAddon(), inventorioTag);
        tag.put("Inventorio", inventorioTag);
    }

    @Inject(method = "tickNewAi", at = @At(value = "RETURN"))
    private void emptyMainHandDisplayTool(CallbackInfo ci)
    {
        PlayerEntity thisPlayer = ((PlayerEntity)(Object)this);
        if (!thisPlayer.handSwinging)
            PlayerInventoryAddon.getInventoryAddon(thisPlayer).setMainHandDisplayTool(ItemStack.EMPTY);
    }

    private PlayerInventoryAddon getAddon()
    {
        return ((InventoryDuck) inventory).getInventorioAddon();
    }
}