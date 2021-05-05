package me.lizardofoz.inventorio.client.config

import me.lizardofoz.inventorio.util.QuickBarSimplified
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