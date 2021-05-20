package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.client.config.InventorioConfig
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.integration.ClothConfigForgeIntegration
import me.lizardofoz.inventorio.integration.InventorioModIntegration
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
class InventorioForge
{
    private val forgeModIntegrations = listOf(ClothConfigForgeIntegration)
    init
    {
        InventorioNetworking.INSTANCE = InventorioNetworkingForge

        val enchantment = (DeepPocketsEnchantment as Enchantment).setRegistryName(Identifier("inventorio", "deep_pockets"))
        ForgeRegistries.ENCHANTMENTS.register(enchantment)

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(ForgeEvents)
            InventorioConfig.load(FMLPaths.CONFIGDIR.get().toFile())
        }

        InventorioModIntegration.addModIntegrations(forgeModIntegrations)
        InventorioModIntegration.apply()
    }
}