package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.client.config.InventorioConfig
import me.lizardofoz.inventorio.client.InventorioControls
import me.lizardofoz.inventorio.client.InventorioControlsFabric
import me.lizardofoz.inventorio.client.InventorioKeyHandler
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.extra.InventorioServerConfig
import me.lizardofoz.inventorio.modcomp.InventorioModCompatibility
import me.lizardofoz.inventorio.modcomp.TrinketsComp
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
    private val fabricModComps = listOf(TrinketsComp)

    override fun onInitialize()
    {
        InventorioNetworking.INSTANCE = InventorioNetworkingFabric

        Registry.register(Registry.ENCHANTMENT, Identifier("inventorio", "deep_pockets"), DeepPocketsEnchantment)

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
        {
            InventorioControls.INSTANCE = InventorioControlsFabric
            ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { InventorioKeyHandler.tick() })
            InventorioConfig.load(FabricLoader.getInstance().configDir.toFile())
        }

        InventorioModCompatibility.addModComps(fabricModComps)
        InventorioModCompatibility.apply()
    }
}