package de.rubixdev.inventorio.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import de.rubixdev.inventorio.player.InventorioScreenHandler;
import de.rubixdev.inventorio.player.PlayerAddonSerializer;
import de.rubixdev.inventorio.player.PlayerInventoryAddon;
import de.rubixdev.inventorio.util.MixinHelpers;
import de.rubixdev.inventorio.util.PlayerDuck;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerDuck {
    @Shadow
    public abstract PlayerInventory getInventory();

    @Unique public PlayerInventoryAddon inventorioAddon;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void inventorioCreateAddon(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        PlayerEntity thisPlayer = (PlayerEntity) (Object) this;
        inventorioAddon = new PlayerInventoryAddon(thisPlayer);
    }

    /**
     * This injection causes the selected UtilityBelt item to be displayed in
     * the offhand
     */
    @Inject(method = "getEquippedStack", at = @At(value = "HEAD"), cancellable = true)
    private void inventorioDisplayOffhand(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
        if (slot == EquipmentSlot.OFFHAND) cir.setReturnValue(inventorioAddon.getDisplayedOffHandStack());
    }

    /**
     * These 2 mixins govern the custom behavior of displaying items in both
     * hands. First, the offhand is attached to the utility belt, rather than a
     * vanilla slot. Second, a player can swap the main hand and the offhand.
     */
    @SuppressWarnings({ "unchecked", "MixinExtrasOperationParameters" })
    @WrapOperation(
        method = "equipStack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;",
            ordinal = 0
        )
    )
    private <E> E inventorioEquipMainHand(DefaultedList<E> instance, int index, E element, Operation<E> original) {
        if (inventorioAddon.getSwappedHands()) {
            inventorioAddon.setSelectedUtilityStack((ItemStack) element);
            // TODO: should this return something else?
            return (E) ItemStack.EMPTY;
        }
        return original.call(instance, index, element);
    }

    @SuppressWarnings("unchecked")
    @Redirect(
        method = "equipStack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;",
            ordinal = 1
        )
    )
    private <E> E inventorioEquipOffhand(DefaultedList<E> defaultedList, int index, E stack) {
        ItemStack itemStack = (ItemStack) stack;
        if (inventorioAddon.getSwappedHands()) inventorioAddon.setSelectedHotbarStack(itemStack);
        else inventorioAddon.setSelectedUtilityStack(itemStack);
        // TODO: should this return something else?
        return (E) ItemStack.EMPTY;
    }

    /**
     * This mixin refreshes the available slots when we equip armor through
     * right-clicking or a dispenser
     */
    @Inject(method = "equipStack", at = @At(value = "RETURN"))
    private void inventorioOnEquipArmor(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR) MixinHelpers
            .withScreenHandler((PlayerEntity) (Object) this, InventorioScreenHandler::updateDeepPocketsCapacity);
    }

    /**
     * This mixin allows arrows stored in the addon slots to be used by a bow
     */
    @ModifyReturnValue(method = "getProjectileType", at = @At("RETURN"))
    private ItemStack inventorioGetArrowType(ItemStack original, ItemStack bowStack) {
        if (!original.isEmpty()) return original;
        ItemStack arrowStack = inventorioAddon.getActiveArrowType(bowStack);
        return arrowStack != null ? arrowStack : original;
    }

    /**
     * These 2 injections cause a correct weapon to be automatically selected
     * and withdrawn upon attack
     */
    @Inject(method = "attack", at = @At(value = "HEAD"))
    private void inventorioPreAttack(Entity target, CallbackInfo ci) {
        if (target.isAttackable()) inventorioAddon.prePlayerAttack();
    }

    @Inject(method = "attack", at = @At(value = "RETURN"))
    private void inventorioPostAttack(Entity target, CallbackInfo ci) {
        if (target.isAttackable()) inventorioAddon.postPlayerAttack();
    }

    /**
     * These 2 injections read and write additional data into Player's NBT
     */
    @Inject(method = "readCustomDataFromNbt", at = @At(value = "RETURN"))
    private void inventorioDeserializePlayerAddon(NbtCompound tag, CallbackInfo ci) {
        PlayerAddonSerializer.INSTANCE.deserialize(inventorioAddon, tag.getCompound("Inventorio"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "RETURN"))
    private void inventorioSerializePlayerAddon(NbtCompound tag, CallbackInfo ci) {
        NbtCompound inventorioTag = new NbtCompound();
        PlayerAddonSerializer.INSTANCE.serialize(inventorioAddon, inventorioTag);
        tag.put("Inventorio", inventorioTag);
    }

    @Inject(method = "tickMovement", at = @At(value = "RETURN"))
    private void inventorioTick(CallbackInfo ci) {
        inventorioAddon.tick();
    }

    @Nullable @Override
    public PlayerInventoryAddon inventorio$getInventorioAddon() {
        return inventorioAddon;
    }
}
