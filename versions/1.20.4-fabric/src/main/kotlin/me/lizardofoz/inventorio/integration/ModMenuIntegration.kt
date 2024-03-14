package me.lizardofoz.inventorio.integration

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.lizardofoz.inventorio.client.configscreen.PlayerSettingsScreen
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class ModMenuIntegration : ModMenuApi {
    @Environment(EnvType.CLIENT)
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent -> PlayerSettingsScreen.get(parent) }
    }
}
