package me.lizardofoz.inventorio.client.config

import me.lizardofoz.inventorio.util.QuickBarMode
import me.lizardofoz.inventorio.util.QuickBarSimplified
import me.lizardofoz.inventorio.util.UtilityBeltMode
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.ConfigHolder
import me.shedaniel.autoconfig.annotation.Config
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
@Config(name = "inventorio")
class InventorioConfigData : ConfigData
{
    //Global
    var quickBarSimplified = QuickBarSimplified.OFF
    var ignoredScreens = listOf<String>()
    var inventoryTextOffsets = listOf<String>()

    //Defaults
    //QuickBar Mode and Utility Belt Mode are assigned to player's entity on both sides,
    // because this setting changes how the inventory works physically.
    var quickBarModeDefault = QuickBarMode.DEFAULT
    var utilityBeltModeDefault = UtilityBeltMode.FILTERED

    companion object
    {
        fun holder(): ConfigHolder<InventorioConfigData>
        {
            return AutoConfig.getConfigHolder(InventorioConfigData::class.java)
        }

        fun config(): InventorioConfigData
        {
            return holder().config
        }
    }
}