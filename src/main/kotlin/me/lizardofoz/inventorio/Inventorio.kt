package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.client.InventorioControls
import me.lizardofoz.inventorio.client.InventorioKeyHandler
import me.lizardofoz.inventorio.client.config.InventorioConfigData
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager

open class Inventorio : ModInitializer
{
    override fun onInitialize()
    {
        Registry.register(Registry.ENCHANTMENT, Identifier("inventorio", "deep_pockets"), DeepPocketsEnchantment)

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
            onInitializeClient()
    }

    private fun onInitializeClient()
    {
        InventorioControls.initialize()
        ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { InventorioKeyHandler.tick(it) })
        AutoConfig.register(InventorioConfigData::class.java) { definition, configClass -> GsonConfigSerializer(definition, configClass) }
    }

    companion object
    {
        val LOGGER = LogManager.getLogger("Inventorio")!!
    }
}