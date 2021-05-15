package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.extra.InventorioServerConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TridentItem.class, priority = 500)
public class TridentItemMixin
{
    /**
     * [This option is disabled by default]
     * This prevents tridents without Loyalty being thrown.
     * Why? Because why would you throw a Trident that doesn't come back and will despawn in just 1-2 minutes?
     */
    @Inject(method = "use", at = @At(value = "HEAD"), cancellable = true)
    private void inventorioDisplayUtilityInOffhand(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir)
    {
        if (InventorioServerConfig.INSTANCE.getUnloyalTridentCannotBeThrown())
        {
            ItemStack itemStack = user.getStackInHand(hand);
            if (EnchantmentHelper.getLevel(Enchantments.LOYALTY, itemStack) <= 0 && EnchantmentHelper.getLevel(Enchantments.RIPTIDE, itemStack) <= 0)
                cir.setReturnValue(TypedActionResult.fail(itemStack));
        }
    }
}
