package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.client.InventorioConfig
import me.lizardofoz.inventorio.client.InventorioControls
import me.lizardofoz.inventorio.client.InventorioControlsFabric
import me.lizardofoz.inventorio.client.InventorioKeyHandler
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.extra.InventorioServerConfig
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.packet.InventorioNetworkingFabric
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
        InventorioNetworking.Companion.INSTANCE = InventorioNetworkingFabric
        InventorioControls.INSTANCE = InventorioControlsFabric
        InventorioServerConfig.load(FabricLoader.getInstance().configDir.toFile())

        Registry.register(Registry.ENCHANTMENT, Identifier("inventorio", "deep_pockets"), DeepPocketsEnchantment)

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
        {
            ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { InventorioKeyHandler.tick() })
            InventorioConfig.load(FabricLoader.getInstance().configDir.toFile())
        }
    }
}