package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.client.config.InventorioConfig
import me.lizardofoz.inventorio.client.InventorioKeyHandler
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.integration.ClothConfigFabricIntegration
import me.lizardofoz.inventorio.integration.InventorioModIntegration
import me.lizardofoz.inventorio.integration.TrinketsIntegration
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.packet.InventorioNetworkingFabric
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

open class InventorioFabric : ModInitializer
{
    private val fabricModIntegrations = listOf(ClothConfigFabricIntegration, TrinketsIntegration)

    override fun onInitialize()
    {
        InventorioNetworking.INSTANCE = InventorioNetworkingFabric

        Registry.register(Registry.ENCHANTMENT, Identifier("inventorio", "deep_pockets"), DeepPocketsEnchantment)

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
        {
            ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { InventorioKeyHandler.tick() })
            InventorioConfig.load(FabricLoader.getInstance().configDir.toFile())
        }

        InventorioModIntegration.addModIntegrations(fabricModIntegrations)
        InventorioModIntegration.apply()
    }
}