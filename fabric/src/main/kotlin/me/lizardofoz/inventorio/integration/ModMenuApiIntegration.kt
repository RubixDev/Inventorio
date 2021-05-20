package me.lizardofoz.inventorio.integration

import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import me.lizardofoz.inventorio.client.config.InventorioConfigScreenMenu
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class ModMenuApiIntegration : ModMenuApi
{
    @Environment(EnvType.CLIENT)
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*>
    {
        return ConfigScreenFactory { parent ->
            if (ClothConfigFabricIntegration.isActive)
                InventorioConfigScreenMenu.get(parent)
            else
                null
        }
    }
}