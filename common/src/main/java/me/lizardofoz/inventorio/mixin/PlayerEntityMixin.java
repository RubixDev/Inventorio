package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.player.InventorioPlayerSerializer;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon;
import me.lizardofoz.inventorio.util.InventoryDuck;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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
    @Shadow @Final public PlayerInventory inventory;

    /**
     * This inject causes the selected UtilityBelt item to be displayed in the offhand
     */
    @Inject(method = "getEquippedStack", at = @At(value = "HEAD"), cancellable = true)
    private void inventorioDisplayUtilityInOffhand(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir)
    {
        if (slot == EquipmentSlot.OFFHAND && getAddon() != null)
            cir.setReturnValue(getAddon().getOffHandStack());
    }

    /**
     * This mixin refreshes the available slots when we equip armor through right clicking or a dispenser
     */
    @Inject(method = "equipStack", at = @At(value = "RETURN"))
    private void inventorioPostAttack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci)
    {
        if (slot == EquipmentSlot.OFFHAND)
        {
            PlayerInventoryAddon addon = ((InventoryDuck) inventory).getInventorioAddon();
            if (addon != null)
                addon.setOffHandStack(stack);
        }
        if (slot.getType() == EquipmentSlot.Type.ARMOR)
        {
            PlayerScreenHandlerAddon screenHandlerAddon = PlayerScreenHandlerAddon.Companion.getScreenHandlerAddon(inventory.player);
            if (screenHandlerAddon != null)
                screenHandlerAddon.updateDeepPocketsCapacity();
        }
    }

    @Inject(method = "getArrowType", at = @At(value = "RETURN"), cancellable = true)
    private void inventorioGetArrowType(ItemStack bowStack, CallbackInfoReturnable<ItemStack> cir)
    {
        if (getAddon() == null || !cir.getReturnValue().isEmpty())
            return;
        ItemStack arrowStack = getAddon().getArrowType(bowStack);
        if (arrowStack != null)
            cir.setReturnValue(arrowStack);
    }

    /**
     * These 2 injects cause a correct weapon to be automatically selected and withdrawn upon attack
     */
    @Inject(method = "attack", at = @At(value = "HEAD"))
    private void inventorioPreAttack(Entity target, CallbackInfo ci)
    {
        if (target.isAttackable() && getAddon() != null)
            getAddon().prePlayerAttack();
    }

    @Inject(method = "attack", at = @At(value = "RETURN"))
    private void inventorioPostAttack(Entity target, CallbackInfo ci)
    {
        if (target.isAttackable() && getAddon() != null)
            getAddon().postPlayerAttack();
    }

    /**
     * These 2 injects read and write additional data into Player's NBT
     */
    @Inject(method = "readCustomDataFromTag", at = @At(value = "RETURN"))
    private void inventorioDeserializePlayerAddon(CompoundTag tag, CallbackInfo ci)
    {
        if (getAddon() != null)
            InventorioPlayerSerializer.INSTANCE.deserialize(getAddon(), tag.getCompound("Inventorio"));
    }

    @Inject(method = "writeCustomDataToTag", at = @At(value = "RETURN"))
    private void inventorioSerializePlayerAddon(CompoundTag tag, CallbackInfo ci)
    {
        if (getAddon() == null)
            return;
        CompoundTag inventorioTag = new CompoundTag();
        InventorioPlayerSerializer.INSTANCE.serialize(getAddon(), inventorioTag);
        tag.put("Inventorio", inventorioTag);
    }

    @Inject(method = "tickNewAi", at = @At(value = "RETURN"))
    private void inventorioEmptyMainHandDisplayTool(CallbackInfo ci)
    {
        PlayerEntity thisPlayer = ((PlayerEntity)(Object)this);
        if (!thisPlayer.handSwinging)
        {
            PlayerInventoryAddon addon = PlayerInventoryAddon.Companion.getInventoryAddon(thisPlayer);
            if (addon != null)
                addon.setMainHandDisplayTool(ItemStack.EMPTY);
        }
    }

    private PlayerInventoryAddon getAddon()
    {
        return ((InventoryDuck) inventory).getInventorioAddon();
    }
}