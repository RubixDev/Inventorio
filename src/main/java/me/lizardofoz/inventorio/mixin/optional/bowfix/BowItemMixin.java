package me.lizardofoz.inventorio.mixin.optional.bowfix;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BowItem.class, priority = 500)
public class BowItemMixin {
    /**
     * This fixes a bug (yes, it's a bug! check out BowItem#onStoppedUsing
     * method!) that Infinity Bow requires an arrow to shoot.
     */
    @Inject(method = "use", at = @At(value = "RETURN"), cancellable = true, require = 0)
    private void inventorioFixInfinityBow(
        World world,
        PlayerEntity user,
        Hand hand,
        CallbackInfoReturnable<TypedActionResult<ItemStack>> cir
    ) {
        if (cir.getReturnValue().getResult() != ActionResult.FAIL) return;
        ItemStack bowStack = user.getStackInHand(hand);
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, bowStack) > 0) {
            user.setCurrentHand(hand);
            cir.setReturnValue(TypedActionResult.consume(bowStack));
        }
    }
}
