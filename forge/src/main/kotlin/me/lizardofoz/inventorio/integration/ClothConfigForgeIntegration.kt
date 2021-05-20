package me.lizardofoz.inventorio.integration

import me.lizardofoz.inventorio.client.InventorioControls
import me.lizardofoz.inventorio.client.config.InventorioConfigScreenMenu
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.ExtensionPoint
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.loading.FMLEnvironment
import java.util.function.BiFunction

object ClothConfigForgeIntegration : ModIntegration()
{
    override val name = "cloth_config_forge"
    override val displayName = "Cloth Config API (Forge)"

    override fun testForge(): Boolean
    {
        if (FMLEnvironment.dist != Dist.CLIENT)
            return false
        if (ModList.get().isLoaded("cloth-config"))
        {
            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY) {
                BiFunction { minecraft, screen -> InventorioConfigScreenMenu.get(screen) }
            }
            MinecraftClient.getInstance().options.keysAll += InventorioControls.functionalKeys + InventorioControls.keyOpenSettingsMenu
            for (settingsKey in InventorioControls.optionToggleKeys)
                settingsKey.setBoundKey(InputUtil.UNKNOWN_KEY)
            return true
        }
        else
        {
            MinecraftClient.getInstance().options.keysAll += InventorioControls.functionalKeys + InventorioControls.optionToggleKeys
            InventorioControls.keyOpenSettingsMenu.setBoundKey(InputUtil.UNKNOWN_KEY)
            InventorioControls.optionToggleKeysEnabled = true
            return false
        }
    }
}