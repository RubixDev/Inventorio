package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.client.config.InventorioConfig
import me.lizardofoz.inventorio.client.InventorioControls
import me.lizardofoz.inventorio.client.InventorioControlsForge
import me.lizardofoz.inventorio.client.config.InventorioConfigScreenMenu
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.extra.InventorioServerConfig
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.packet.InventorioNetworkingForge
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.enchantment.Enchantment
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.ExtensionPoint
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.loading.FMLPaths
import net.minecraftforge.registries.ForgeRegistries
import java.util.function.BiFunction


@Mod("inventorio")
class InventorioForge
{
    init
    {
        InventorioNetworking.INSTANCE = InventorioNetworkingForge
        InventorioControls.INSTANCE = InventorioControlsForge
        InventorioServerConfig.load(FMLPaths.CONFIGDIR.get().toFile())

        val enchantment = (DeepPocketsEnchantment as Enchantment).setRegistryName(Identifier("inventorio", "deep_pockets"))
        ForgeRegistries.ENCHANTMENTS.register(enchantment)

        InventorioNetworkingForge.initialize()
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(ForgeEvents)
            InventorioConfig.load(FMLPaths.CONFIGDIR.get().toFile())
        }

        if (ModList.get().isLoaded("cloth-config"))
        {
            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY) {
                BiFunction { minecraft: MinecraftClient?, screen: Screen? -> InventorioConfigScreenMenu.get(screen) }
            }
        }
    }
}