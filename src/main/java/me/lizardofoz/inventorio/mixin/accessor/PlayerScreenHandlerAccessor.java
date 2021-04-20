package me.lizardofoz.inventorio.mixin.accessor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerScreenHandler.class)
public interface PlayerScreenHandlerAccessor extends ScreenHandlerAccessor
{
    @Accessor("craftingInput")
    CraftingInventory getCraftingInput();

    @Accessor("craftingResult")
    CraftingResultInventory getCraftingResult();

    @Accessor("owner")
    PlayerEntity getOwner();

    @Accessor("EMPTY_ARMOR_SLOT_TEXTURES")
    static Identifier[] getEmptyArmorSlotTextures()
    {
        throw new IllegalStateException();
    }
}
