package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.player.PlayerAddon;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin allows items in the ToolBelt to be Mended
 */
@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin
{
    @Redirect(method = "onPlayerCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExperience(I)V"))
    public void mendToolBeltItems(PlayerEntity playerEntity, int experience)
    {
        int xpLeft = PlayerAddon.get(playerEntity).getInventoryAddon().mendToolBeltItems(experience);
        if (xpLeft > 0)
            playerEntity.addExperience(xpLeft);
    }
}