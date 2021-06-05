package me.lizardofoz.inventorio.mixin.optional.totem;

import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
@SuppressWarnings("ConstantConditions")
public abstract class LivingEntityMixin
{
    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    /**
     * This optional mixin allows a Totem of Undying to be used automatically from any Utility Slot
     */
    @Redirect(method = "tryUseTotem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"),
            require = 0)
    private ItemStack inventorioGetTotemFromUtilityBar(LivingEntity livingEntity, Hand hand)
    {
        ItemStack stackInHand = getStackInHand(hand);

        if (stackInHand.getItem() == Items.TOTEM_OF_UNDYING || !((Object) this instanceof PlayerEntity))
            return stackInHand;

        PlayerInventoryAddon addon = PlayerInventoryAddon.Companion.getInventoryAddon((PlayerEntity) (Object) this);

        if (addon != null)
            for (ItemStack itemStack : addon.getUtilityBelt())
            {
                if (itemStack.getItem() == Items.TOTEM_OF_UNDYING)
                    return itemStack;
            }
        return stackInHand;
    }
}