package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.client.InventorioConfig
import me.lizardofoz.inventorio.client.InventorioControls
import me.lizardofoz.inventorio.client.InventorioControlsForge
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.extra.InventorioServerConfig
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.packet.InventorioNetworkingForge
import net.minecraft.enchantment.Enchantment
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.loading.FMLPaths
import net.minecraftforge.registries.ForgeRegistries

@Mod("inventorio")
class Inventorio
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
    }
}