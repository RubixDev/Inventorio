package me.danetnaverno.inventorio.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntity.class)
public interface PlayerEntityAccessor
{
    @Accessor("playerScreenHandler")
    PlayerScreenHandler getPlayerScreenHandler();

    @Accessor("playerScreenHandler")
    void setPlayerScreenHandler(PlayerScreenHandler handler);
}
