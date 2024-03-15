package me.lizardofoz.inventorio.mixin.optional.bowfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.lizardofoz.inventorio.util.RandomStuff;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BowItem.class)
public class BowItemMixin {
    /**
     * This fixes a bug (yes, it's a bug! check out
     * {@link BowItem#onStoppedUsing} method!) that an Infinity Bow requires an
     * arrow to shoot.
     */
    @ModifyExpressionValue(
        method = "use",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z")
    )
    private boolean inventorioFixInfinityBow(boolean original, @Local ItemStack bow) {
        return original || RandomStuff.getEnchantmentLevel(bow, Enchantments.INFINITY) > 0;
    }
}
