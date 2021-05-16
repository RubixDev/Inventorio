package me.lizardofoz.inventorio.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.util.InputUtil

@Environment(EnvType.CLIENT)
object InventorioControlsFabric : InventorioControls()
{
    init
    {
        functionalKeys.forEach { KeyBindingHelper.registerKeyBinding(it) }
        if (FabricLoader.getInstance().isModLoaded("cloth-config2"))
        {
            KeyBindingHelper.registerKeyBinding(keyOpenSettings)
            for (settingsKey in settingsKeys)
                settingsKey.setBoundKey(InputUtil.UNKNOWN_KEY)
        }
        else
        {
            settingsKeys.forEach { KeyBindingHelper.registerKeyBinding(it) }
            settingsKeysEnabled = true
        }
    }
}