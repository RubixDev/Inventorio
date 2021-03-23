package me.danetnaverno.inventorio;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;

public class MixinHelper
{
    public static boolean isThisCreativeScreenHandler(Object object)
    {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
            return false;
        else
            return object instanceof CreativeInventoryScreen.CreativeScreenHandler;
    }
}
