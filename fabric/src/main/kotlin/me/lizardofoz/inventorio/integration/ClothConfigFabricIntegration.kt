package me.lizardofoz.inventorio.integration

import me.lizardofoz.inventorio.client.InventorioControls
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.util.InputUtil

object ClothConfigFabricIntegration : ModIntegration()
{
    override val name = "cloth_config_fabric"
    override val displayName = "Cloth Config API (Fabric)"

    override fun testFabric(): Boolean
    {
        if (FabricLoader.getInstance().environmentType != EnvType.CLIENT)
            return false
        InventorioControls.functionalKeys.forEach { KeyBindingHelper.registerKeyBinding(it) }
        if (FabricLoader.getInstance().isModLoaded("cloth-config2"))
        {
            KeyBindingHelper.registerKeyBinding(InventorioControls.keyOpenSettingsMenu)
            for (settingsKey in InventorioControls.optionToggleKeys)
                settingsKey.setBoundKey(InputUtil.UNKNOWN_KEY)
            return true
        }
        else
        {
            InventorioControls.optionToggleKeys.forEach { KeyBindingHelper.registerKeyBinding(it) }
            InventorioControls.keyOpenSettingsMenu.setBoundKey(InputUtil.UNKNOWN_KEY)
            InventorioControls.optionToggleKeysEnabled = true
            return false
        }
    }
}