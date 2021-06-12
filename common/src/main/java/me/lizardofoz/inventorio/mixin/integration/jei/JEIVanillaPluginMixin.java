package me.lizardofoz.inventorio.mixin.integration.jei;

import mezz.jei.plugins.vanilla.VanillaPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(VanillaPlugin.class)
public class JEIVanillaPluginMixin
{
    /**
     * JEI's API doesn't allow to manipulate existing recipe click areas, so, we're forced to make a mixin.
     * This one moves the "Show Recipes" button (invisible area floating above the crafting grid arrow) according
     * to the Inventorio UI changes
     */
    @ModifyConstant(method = "registerGuiHandlers",
            constant = @Constant(intValue = 137),
            require = 0,
            remap = false)
    private int inventorioSetJEIButtonX(int original)
    {
        return original + 20;
    }
}
