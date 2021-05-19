package me.lizardofoz.inventorio.client

import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import me.lizardofoz.inventorio.client.config.InventorioConfigScreenMenu
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.Screen

@Environment(EnvType.CLIENT)
class InventorioModMenuApi : ModMenuApi
{
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*>
    {
        return ConfigScreenFactory { parent ->
            if (FabricLoader.getInstance().isModLoaded("cloth-config2"))
                InventorioConfigScreenMenu.get(parent)
            else
                null
        }
    }
}