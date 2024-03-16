package de.rubixdev.inventorio

import de.rubixdev.inventorio.api.InventorioAPI
import de.rubixdev.inventorio.client.configscreen.PlayerSettingsScreen
import de.rubixdev.inventorio.client.control.InventorioControls
import de.rubixdev.inventorio.config.PlayerSettings
import de.rubixdev.inventorio.enchantment.DeepPocketsBookRecipe
import de.rubixdev.inventorio.enchantment.DeepPocketsEnchantment
import de.rubixdev.inventorio.integration.ClumpsIntegration
import de.rubixdev.inventorio.integration.InventorioModIntegration
import de.rubixdev.inventorio.integration.ModIntegration
import de.rubixdev.inventorio.packet.InventorioNetworking
import de.rubixdev.inventorio.packet.InventorioNetworkingForge
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ConfigScreenHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.ToolAction
import net.minecraftforge.common.ToolActions
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.loading.FMLPaths
import net.minecraftforge.registries.ForgeRegistries

@Mod("inventorio")
class InventorioForge {
    private val forgeModIntegrations = listOf<ModIntegration>(ClumpsIntegration)

    init
    {
        ScreenTypeProvider.INSTANCE = ScreenTypeProviderForge
        InventorioNetworking.INSTANCE = InventorioNetworkingForge
        ForgeRegistries.ENCHANTMENTS.register(Identifier("inventorio", "deep_pockets"), DeepPocketsEnchantment)
        val serializer = SpecialRecipeSerializer { identifier, category -> DeepPocketsBookRecipe(identifier, category) }
        DeepPocketsBookRecipe.SERIALIZER = serializer
        ForgeRegistries.RECIPE_SERIALIZERS.register(Identifier("inventorio", "deep_pockets_book"), serializer)
        initToolBelt()

        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.register(ForgeEvents)
            MinecraftClient.getInstance().options.allKeys += InventorioControls.keys
            PlayerSettings.load(FMLPaths.CONFIGDIR.get().resolve("inventorio.json").toFile())
            ScreenTypeProviderForge.registerScreen()
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory::class.java) {
                ConfigScreenHandler.ConfigScreenFactory { _, parent -> PlayerSettingsScreen.get(parent) }
            }
        }

        InventorioModIntegration.addModIntegrations(forgeModIntegrations)
        InventorioModIntegration.apply()
    }

    private fun initToolBelt() {
        // What this actually does is loads the [InventorioAPI] which creates the ToolBelt
        // The reason why we do it this way is because we can't guarantee that other mods
        //  won't call [InventorioAPI] BEFORE [InventorioForge#onInitialize] has been invoked
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_PICKAXE)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ToolActions.PICKAXE_DIG)
        }
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_SWORD)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ToolActions.SWORD_SWEEP) // todo sword
        }
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_AXE)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ToolActions.AXE_DIG)
        }
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_SHOVEL)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ToolActions.SHOVEL_DIG)
        }
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_HOE)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ToolActions.HOE_DIG)
        }
    }
    private fun testToolType(itemStack: ItemStack, vararg toolActions: ToolAction): Boolean {
        return toolActions.any { itemStack.canPerformAction(it) }
    }
}
