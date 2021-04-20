package me.lizardofoz.inventorio.client.config

import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen

@Environment(EnvType.CLIENT)
class InventorioModMenuApi : ModMenuApi
{
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*>?
    {
        return ConfigScreenFactory { parent: Screen? -> InventorioConfigScreenMenu.get(parent) }
    }
}