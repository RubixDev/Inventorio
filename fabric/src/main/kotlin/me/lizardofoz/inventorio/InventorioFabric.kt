package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.api.InventorioAPI
import me.lizardofoz.inventorio.client.control.InventorioControls
import me.lizardofoz.inventorio.client.control.InventorioKeyHandler
import me.lizardofoz.inventorio.config.PlayerSettings
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.integration.BetterGravesIntegration
import me.lizardofoz.inventorio.integration.InventorioModIntegration
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.packet.InventorioNetworkingFabric
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

open class InventorioFabric : ModInitializer
{
    private val fabricModIntegrations = listOf(BetterGravesIntegration)

    override fun onInitialize()
    {
        ScreenTypeProvider.INSTANCE = ScreenTypeProviderFabric
        InventorioNetworking.INSTANCE = InventorioNetworkingFabric
        Registry.register(Registry.ENCHANTMENT, Identifier("inventorio", "deep_pockets"), DeepPocketsEnchantment)
        initToolBelt()

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
        {
            ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { InventorioKeyHandler.tick() })
            InventorioControls.keys.forEach { KeyBindingHelper.registerKeyBinding(it) }
            PlayerSettings.load(FabricLoader.getInstance().configDir.resolve("inventorio.json").toFile())
            ScreenTypeProviderFabric.registerScreen()
        }

        InventorioModIntegration.addModIntegrations(fabricModIntegrations)
        InventorioModIntegration.apply()
    }

    private fun initToolBelt()
    {
        //What this actually does is loads the [InventorioAPI] which creates the ToolBelt
        //The reason why we do it this way is because we can't guarantee that other mods
        //  won't call [InventorioAPI] BEFORE [InventorioFabric#onInitialize] has been invoked
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_PICKAXE)
            ?.addAllowingTag(Identifier("fabric", "pickaxes"))
            ?.addAllowingTag(Identifier("fabric", "hammers"))

        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_SWORD)
            ?.addAllowingTag(Identifier("fabric", "swords"))
            ?.addAllowingTag(Identifier("fabric", "tridents"))
            ?.addAllowingTag(Identifier("fabric", "battleaxes"))

        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_AXE)
            ?.addAllowingTag(Identifier("fabric", "axes"))
            ?.addAllowingTag(Identifier("fabric", "battleaxes"))

        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_SHOVEL)
            ?.addAllowingTag(Identifier("fabric", "shovels"))
            ?.addAllowingTag(Identifier("fabric", "mattocks"))

        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_HOE)
            ?.addAllowingTag(Identifier("fabric", "hoes"))
            ?.addAllowingTag(Identifier("fabric", "shears"))
    }
}