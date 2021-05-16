package me.lizardofoz.inventorio.client

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.ModList

@OnlyIn(Dist.CLIENT)
object InventorioControlsForge : InventorioControls()
{
    init
    {
        var result = functionalKeys
        if (ModList.get().isLoaded("cloth-config"))
        {
            result += keyOpenSettings
            for (settingsKey in settingsKeys)
                settingsKey.setBoundKey(InputUtil.UNKNOWN_KEY)
        }
        else
        {
            result += settingsKeys
            settingsKeysEnabled = true
        }
        MinecraftClient.getInstance().options.keysAll += result
    }
}