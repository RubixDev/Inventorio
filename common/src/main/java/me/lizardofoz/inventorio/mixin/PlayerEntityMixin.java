package me.lizardofoz.inventorio.mixin;

import com.mojang.authlib.GameProfile;
import me.lizardofoz.inventorio.player.InventorioScreenHandler;
import me.lizardofoz.inventorio.player.PlayerAddonSerializer;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import me.lizardofoz.inventorio.util.MixinHelpers;
import me.lizardofoz.inventorio.util.PlayerDuck;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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
public class PlayerEntityMixin implements PlayerDuck
{
    @Shadow @Final public PlayerInventory inventory;
    @Unique public PlayerInventoryAddon inventorioAddon;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void inventorioCreateAddon(World world, BlockPos pos, float yaw, GameProfile profile, CallbackInfo ci)
    {
        PlayerEntity thisPlayer = (PlayerEntity) (Object) this;
        inventorioAddon = new PlayerInventoryAddon(thisPlayer);
    }

    /**
     * This inject causes the selected UtilityBelt item to be displayed in the offhand
     */
    @Inject(method = "getEquippedStack", at = @At(value = "HEAD"), cancellable = true)
    private void inventorioDisplayOffhand(EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir)
    {
        if (slot == EquipmentSlot.OFFHAND)
            cir.setReturnValue(inventorioAddon.getDisplayedOffHandStack());
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
        if (inventorioAddon.getSwappedHands())
            inventorioAddon.setSelectedUtilityStack(itemStack);
        else
            inventory.main.set(inventory.selectedSlot, itemStack);
        return null;
    }

    @Redirect(method = "equipStack",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1))
    private <E> E inventorioEquipOffhand(DefaultedList<E> defaultedList, int index, E stack)
    {
        ItemStack itemStack = (ItemStack) stack;
        if (inventorioAddon.getSwappedHands())
            inventorioAddon.setSelectedHotbarStack(itemStack);
        else
            inventorioAddon.setSelectedUtilityStack(itemStack);
        return null;
    }

    /**
     * This mixin refreshes the available slots when we equip armor through right clicking or a dispenser
     */
    @Inject(method = "equipStack", at = @At(value = "RETURN"))
    private void inventorioOnEquipArmor(EquipmentSlot slot, ItemStack stack, CallbackInfo ci)
    {
        if (slot.getType() == EquipmentSlot.Type.ARMOR)
            MixinHelpers.withScreenHandler((PlayerEntity) (Object) this, InventorioScreenHandler::updateDeepPocketsCapacity);
    }

    /**
     * This mixin allows arrows stored in the addon slots to be used by a bow
     */
    @Inject(method = "getArrowType", at = @At(value = "RETURN"), cancellable = true)
    private void inventorioGetArrowType(ItemStack bowStack, CallbackInfoReturnable<ItemStack> cir)
    {
        if (!cir.getReturnValue().isEmpty())
            return;
        ItemStack arrowStack = inventorioAddon.getActiveArrowType(bowStack);
        if (arrowStack != null)
            cir.setReturnValue(arrowStack);
    }

    /**
     * These 2 injects cause a correct weapon to be automatically selected and withdrawn upon attack
     */
    @Inject(method = "attack", at = @At(value = "HEAD"))
    private void inventorioPreAttack(Entity target, CallbackInfo ci)
    {
        if (target.isAttackable())
            inventorioAddon.prePlayerAttack();
    }

    @Inject(method = "attack", at = @At(value = "RETURN"))
    private void inventorioPostAttack(Entity target, CallbackInfo ci)
    {
        if (target.isAttackable())
            inventorioAddon.postPlayerAttack();
    }

    /**
     * These 2 injects read and write additional data into Player's NBT
     */
    @Inject(method = "readCustomDataFromTag", at = @At(value = "RETURN"))
    private void inventorioDeserializePlayerAddon(CompoundTag tag, CallbackInfo ci)
    {
        PlayerAddonSerializer.INSTANCE.deserialize(inventorioAddon, tag.getCompound("Inventorio"));
    }

    @Inject(method = "writeCustomDataToTag", at = @At(value = "RETURN"))
    private void inventorioSerializePlayerAddon(CompoundTag tag, CallbackInfo ci)
    {
        CompoundTag inventorioTag = new CompoundTag();
        PlayerAddonSerializer.INSTANCE.serialize(inventorioAddon, inventorioTag);
        tag.put("Inventorio", inventorioTag);
    }

    @Inject(method = "tickMovement", at = @At(value = "RETURN"))
    private void inventorioEmptyMainHandDisplayTool(CallbackInfo ci)
    {
        inventorioAddon.tick();
    }

    @Nullable
    @Override
    public PlayerInventoryAddon getInventorioAddon()
    {
        return inventorioAddon;
    }
}