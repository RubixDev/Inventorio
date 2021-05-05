package me.lizardofoz.inventorio.mixin.accessor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerScreenHandler.class)
public interface PlayerScreenHandlerAccessor extends ScreenHandlerAccessor
{
    @Accessor("owner")
    PlayerEntity getOwner();
}
