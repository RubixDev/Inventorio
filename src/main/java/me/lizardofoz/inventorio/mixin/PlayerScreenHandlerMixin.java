package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon;
import me.lizardofoz.inventorio.util.ScreenHandlerDuck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

/**
 * This mixin fixes shift-clicks and other inventory shortcuts for Player's inventory
 */
@Mixin(PlayerScreenHandler.class)
public class PlayerScreenHandlerMixin implements ScreenHandlerDuck
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
    public void setAddon(PlayerScreenHandlerAddon addon)
    {
        this.addon = addon;
    }
}