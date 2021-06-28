package me.lizardofoz.inventorio.mixin.optional.totem;

import me.lizardofoz.inventorio.util.MixinHelpers;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 500)
public class LivingEntityMixin
{
    /**
     * This optional mixin allows a Totem of Undying to be used automatically from any Utility Slot.
     * Yes, it copies vanilla code, but it's done for the sake of mod compatibility (Charm)
     */
    @Inject(method = "tryUseTotem",
            at = @At(value = "RETURN"),
            cancellable = true,
            require = 0)
    private void inventorioGetTotemFromUtilityBar(DamageSource source, CallbackInfoReturnable<Boolean> cir)
    {
        //noinspection ConstantConditions
        if (cir.getReturnValue() || source.isOutOfWorld() || !((Object) this instanceof PlayerEntity))
            return;

        MixinHelpers.withInventoryAddon((PlayerEntity) (Object) this , addon -> {
            for (ItemStack itemStack : addon.utilityBelt)
                if (itemStack.getItem() == Items.TOTEM_OF_UNDYING)
                {
                    itemStack.decrement(1);
                    if (addon.getPlayer() instanceof ServerPlayerEntity serverPlayerEntity)
                    {
                        serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
                        Criteria.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
                    }

                    addon.getPlayer().setHealth(1.0F);
                    addon.getPlayer().clearStatusEffects();
                    addon.getPlayer().addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                    addon.getPlayer().addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                    addon.getPlayer().addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
                    addon.getPlayer().world.sendEntityStatus(addon.getPlayer(), (byte) 35);
                    cir.setReturnValue(true);
                    return;
                }
        });
    }
}