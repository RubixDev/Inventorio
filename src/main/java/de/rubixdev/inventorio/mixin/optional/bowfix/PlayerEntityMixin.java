package de.rubixdev.inventorio.mixin.optional.bowfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.rubixdev.inventorio.util.BowTester;
import de.rubixdev.inventorio.util.RandomStuff;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(type = Condition.Type.TESTER, tester = BowTester.class))
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * This fixes a bug that an Infinity Bow requires an arrow to shoot.
     */
    @ModifyExpressionValue(
        method = "getProjectileType",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z",
            opcode = Opcodes.GETFIELD
        )
    )
    private boolean inventorioFixInfinityBow(boolean original, ItemStack bow) {
        //#if MC >= 12101
        var infinity = RandomStuff.getEnchantment(this, Enchantments.INFINITY);
        //#else
        //$$ var infinity = Enchantments.INFINITY;
        //#endif
        return original || RandomStuff.getLevelOn(infinity, bow) > 0;
    }
}
