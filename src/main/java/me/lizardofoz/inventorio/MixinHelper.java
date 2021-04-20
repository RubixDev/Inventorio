package me.lizardofoz.inventorio;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;

public class MixinHelper
{
    /**
     * Creative screen as well as its handler doesn't exist on a dedicated server,
     * but some shared logic needs to check for a creative screen.
     */
    public static boolean isThisCreativeScreenHandler(Object object)
    {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
            return false;
        else
            return object instanceof CreativeInventoryScreen.CreativeScreenHandler;
    }
}
