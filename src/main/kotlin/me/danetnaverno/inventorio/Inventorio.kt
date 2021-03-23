package me.danetnaverno.inventorio

import me.danetnaverno.inventorio.client.InventorioControls
import me.danetnaverno.inventorio.client.InventorioKeyHandler
import me.danetnaverno.inventorio.client.config.InventorioConfigData
import me.danetnaverno.inventorio.enchantment.DeepPocketsEnchantment
import me.danetnaverno.inventorio.mixin.client.CreativeInventoryScreenAccessor
import me.danetnaverno.inventorio.util.INVENTORIO_ROW_LENGTH
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.inventory.SimpleInventory
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.apache.logging.log4j.LogManager

open class Inventorio : ModInitializer
{
    override fun onInitialize()
    {
        Registry.register(Registry.ENCHANTMENT, Identifier("inventorio", "deep_pockets"), DeepPocketsEnchantment)

        ServerLifecycleEvents.SERVER_STARTED.register {
            onInitializeServer()
        }

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
            onInitializeClient()
    }

    private fun onInitializeServer()
    {
    }

    private fun onInitializeClient()
    {
        CreativeInventoryScreenAccessor.setCreativeInventory(SimpleInventory(INVENTORIO_ROW_LENGTH * 5))
        InventorioControls.initialize()
        ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { InventorioKeyHandler.tick(it) })
        AutoConfig.register(InventorioConfigData::class.java) { definition, configClass -> GsonConfigSerializer(definition, configClass) }
    }

    companion object
    {
        val LOGGER = LogManager.getLogger("Inventorio")!!
    }
}