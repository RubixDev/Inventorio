package de.rubixdev.inventorio

import de.rubixdev.inventorio.api.InventorioAPI
import de.rubixdev.inventorio.client.control.InventorioControls
import de.rubixdev.inventorio.client.control.InventorioKeyHandler
import de.rubixdev.inventorio.config.PlayerSettings
import de.rubixdev.inventorio.enchantment.DeepPocketsBookRecipe
import de.rubixdev.inventorio.enchantment.DeepPocketsEnchantment
import de.rubixdev.inventorio.integration.ClumpsIntegration
import de.rubixdev.inventorio.integration.InventorioModIntegration
import de.rubixdev.inventorio.integration.ModIntegration
import de.rubixdev.inventorio.packet.InventorioNetworking
import de.rubixdev.inventorio.packet.InventorioNetworkingFabric
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

open class InventorioFabric : ModInitializer {
    private val fabricModIntegrations = listOf<ModIntegration>(ClumpsIntegration)

    override fun onInitialize() {
        ScreenTypeProvider.INSTANCE = ScreenTypeProviderFabric
        InventorioNetworking.INSTANCE = InventorioNetworkingFabric
        Registry.register(Registries.ENCHANTMENT, Identifier("inventorio", "deep_pockets"), DeepPocketsEnchantment)
        DeepPocketsBookRecipe.SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            Identifier("inventorio", "deep_pockets_book"),
            //#if MC >= 12002
            SpecialRecipeSerializer { category -> DeepPocketsBookRecipe(category) },
            //#else
            //$$ SpecialRecipeSerializer { ident, category -> DeepPocketsBookRecipe(ident, category) },
            //#endif
        )

        initToolBelt()

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { InventorioKeyHandler.tick() })
            InventorioControls.keys.forEach { KeyBindingHelper.registerKeyBinding(it) }
            PlayerSettings.load(FabricLoader.getInstance().configDir.resolve("inventorio.json").toFile())
            ScreenTypeProviderFabric.registerScreen()
        }

        InventorioModIntegration.addModIntegrations(fabricModIntegrations)
        InventorioModIntegration.apply()
    }

    private fun initToolBelt() {
        // What this actually does is loads the [InventorioAPI] which creates the ToolBelt
        // The reason why we do it this way is because we can't guarantee that other mods
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
