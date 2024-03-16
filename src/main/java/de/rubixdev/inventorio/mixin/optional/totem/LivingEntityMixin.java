package de.rubixdev.inventorio.mixin.optional.totem;

import de.rubixdev.inventorio.util.MixinHelpers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * This optional mixin allows a Totem of Undying to be used automatically
     * from any Utility Slot.
     */
    @SuppressWarnings("InvalidInjectorMethodSignature")
//    @ModifyVariable(
//            method = "tryUseTotem",
//            at = @At(value = "JUMP", ordinal = 2, shift = At.Shift.BEFORE),
//            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V")),
//            ordinal = 0
//    )
    @ModifyVariable(
        method = "tryUseTotem",
        at = @At(value = "JUMP", opcode = Opcodes.IFNULL, shift = At.Shift.BEFORE),
        slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V")),
        ordinal = 0
    )
    private ItemStack inventorioGetTotemFromUtilityBar(ItemStack original) {
        // noinspection ConstantValue
        if (original != null || !((Entity) this instanceof PlayerEntity player)) return original;
        return MixinHelpers.withInventoryAddonReturning(
            player,
            addon -> addon.utilityBelt.stream()
                .filter(stack -> stack.isOf(Items.TOTEM_OF_UNDYING))
                .findFirst()
                .map(totem -> {
                    ItemStack copy = totem.copy();
                    totem.decrement(1);
                    return copy;
                })
                .orElse(original)
        );
    }
}
