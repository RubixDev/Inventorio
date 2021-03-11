package me.danetnaverno.inventorio.mixin;

import me.danetnaverno.inventorio.duck.HandlerDuck;
import me.danetnaverno.inventorio.player.PlayerScreenHandlerAddon;
import me.danetnaverno.inventorio.util.ScreenHandlerAddon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerScreenHandler.class)
public class PlayerScreenHandlerMixin implements HandlerDuck
{
    @Unique public PlayerScreenHandlerAddon addon;

    @Overwrite
    public ItemStack transferSlot(PlayerEntity player, int index)
    {
        return addon.transferSlot(player, index);
    }

    @Override
    public PlayerScreenHandlerAddon getAddon()
    {
        return addon;
    }

    @Override
    public void setAddon(ScreenHandlerAddon addon)
    {
        this.addon = (PlayerScreenHandlerAddon) addon;
    }
}