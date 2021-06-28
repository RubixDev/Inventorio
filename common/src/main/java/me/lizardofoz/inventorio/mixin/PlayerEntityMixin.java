package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.player.PlayerAddonSerializer;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon;
import me.lizardofoz.inventorio.util.InventoryDuck;
import me.lizardofoz.inventorio.util.MixinHelpers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin
{
    @Shadow public abstract PlayerInventory getInventory();

    /**
     * This inject causes the selected UtilityBelt item to be displayed in the offhand
     */
    @Inject(method = "getEquippedStack", at = @At(value = "HEAD"), cancellable = true)
    private void inventorioDisplayOffhand(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir)
    {
        if (slot == EquipmentSlot.OFFHAND && getAddon() != null)
            cir.setReturnValue(getAddon().getDisplayedOffHandStack());
    }

    /**
     * These 2 mixins govern the custom behavior of displaying items in both hands.
     * First, the offhand is attached to the utility belt, rather than a vanilla slot.
     * Second, a player can swap the main hand and the offhand.
     */
    @Redirect(method = "equipStack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;",
            ordinal = 0))
    private <E> E inventorioEquipMainHand(DefaultedList<E> defaultedList, int index, E stack)
    {
        ItemStack itemStack = (ItemStack) stack;
        PlayerInventoryAddon addon = getAddon();
        if (addon != null && addon.getSwappedHands())
            addon.setSelectedUtilityStack(itemStack);
        else
            getInventory().main.set(getInventory().selectedSlot, itemStack);
        return null;
    }

    @Redirect(method = "equipStack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1))
    private <E> E inventorioEquipOffhand(DefaultedList<E> defaultedList, int index, E stack)
    {
        ItemStack itemStack = (ItemStack) stack;
        PlayerInventoryAddon addon = getAddon();
        if (addon == null)
            getInventory().offHand.set(0, itemStack);
        else if (addon.getSwappedHands())
            addon.setSelectedHotbarStack(itemStack);
        else
            addon.setSelectedUtilityStack(itemStack);
        return null;
    }

    /**
     * This mixin refreshes the available slots when we equip armor through right clicking or a dispenser
     */
    @Inject(method = "equipStack", at = @At(value = "RETURN"))
    private void inventorioOnEquipArmor(EquipmentSlot slot, ItemStack stack, CallbackInfo ci)
    {
        if (slot.getType() == EquipmentSlot.Type.ARMOR)
            MixinHelpers.withScreenHandlerAddon(getInventory().player, PlayerScreenHandlerAddon::updateDeepPocketsCapacity);
    }

    /**
     * This mixin allows arrows stored in the addon slots to be used by a bow
     */
    @Inject(method = "getArrowType", at = @At(value = "RETURN"), cancellable = true)
    private void inventorioGetArrowType(ItemStack bowStack, CallbackInfoReturnable<ItemStack> cir)
    {
        if (getAddon() == null || !cir.getReturnValue().isEmpty())
            return;
        ItemStack arrowStack = getAddon().getActiveArrowType(bowStack);
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
    @Inject(method = "readCustomDataFromNbt", at = @At(value = "RETURN"))
    private void inventorioDeserializePlayerAddon(NbtCompound tag, CallbackInfo ci)
    {
        if (getAddon() != null)
        {
            boolean isFirstLaunch = !tag.contains("Inventorio");
            PlayerAddonSerializer.INSTANCE.deserialize(getAddon(), tag.getCompound("Inventorio"), isFirstLaunch);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "RETURN"))
    private void inventorioSerializePlayerAddon(NbtCompound tag, CallbackInfo ci)
    {
        if (getAddon() == null)
            return;
        NbtCompound inventorioTag = new NbtCompound();
        PlayerAddonSerializer.INSTANCE.serialize(getAddon(), inventorioTag);
        tag.put("Inventorio", inventorioTag);
    }

    @Inject(method = "tickMovement", at = @At(value = "RETURN"))
    private void inventorioEmptyMainHandDisplayTool(CallbackInfo ci)
    {
        PlayerInventoryAddon addon = getAddon();
        if (addon != null)
            addon.tick();
    }

    private PlayerInventoryAddon getAddon()
    {
        return ((InventoryDuck) getInventory()).getInventorioAddon();
    }
}