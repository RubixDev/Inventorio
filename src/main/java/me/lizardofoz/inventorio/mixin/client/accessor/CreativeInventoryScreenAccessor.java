package me.lizardofoz.inventorio.mixin.client.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.inventory.SimpleInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * This accessor gives access to the inventory used by the Creative Menu
 */
@Mixin(CreativeInventoryScreen.class)
@Environment(EnvType.CLIENT)
public interface CreativeInventoryScreenAccessor
{
    @Accessor("INVENTORY")
    static SimpleInventory getCreativeInventory()
    {
        return null;
    }

    @Accessor("INVENTORY")
    static void setCreativeInventory(SimpleInventory inventory)
    {
    }
}
