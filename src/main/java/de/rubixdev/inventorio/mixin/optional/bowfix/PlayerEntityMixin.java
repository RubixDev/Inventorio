package de.rubixdev.inventorio.mixin.optional.bowfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.rubixdev.inventorio.util.BowTester;
import de.rubixdev.inventorio.util.RandomStuff;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(type = Condition.Type.TESTER, tester = BowTester.class))
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    /**
     * This fixes a bug that an Infinity Bow requires an arrow to shoot.
     */
    @ModifyExpressionValue(
        method = "getProjectileType",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z")
    )
    private boolean inventorioFixInfinityBow(boolean original, ItemStack bow) {
        return original || RandomStuff.getLevelOn(Enchantments.INFINITY, bow) > 0;
    }
}
