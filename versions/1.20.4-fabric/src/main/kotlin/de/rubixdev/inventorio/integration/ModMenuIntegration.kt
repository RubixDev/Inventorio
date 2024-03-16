package de.rubixdev.inventorio.integration

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import de.rubixdev.inventorio.client.configscreen.PlayerSettingsScreen
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class ModMenuIntegration : ModMenuApi {
    @Environment(EnvType.CLIENT)
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent -> PlayerSettingsScreen.get(parent) }
    }
}
