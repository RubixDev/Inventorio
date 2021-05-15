package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin
{
    @Shadow private int amount;

    /**
     * This inject allows items in the ToolBelt to be Mended
     */
    @Inject(method = "onPlayerCollision",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/entity/ExperienceOrbEntity;amount:I",
                    ordinal = 3))
    public void inventorioMendToolBeltItems(PlayerEntity player, CallbackInfo ci)
    {
        this.amount = PlayerInventoryAddon.getInventoryAddon(player).mendToolBeltItems(this.amount);
    }
}