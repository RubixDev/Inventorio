package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.api.InventorioAPI
import me.lizardofoz.inventorio.client.configscreen.PlayerSettingsScreen
import me.lizardofoz.inventorio.client.control.InventorioControls
import me.lizardofoz.inventorio.config.PlayerSettings
import me.lizardofoz.inventorio.enchantment.DeepPocketsBookRecipe
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.integration.ClumpsIntegration
import me.lizardofoz.inventorio.integration.InventorioModIntegration
import me.lizardofoz.inventorio.integration.ModIntegration
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.packet.InventorioNetworkingNeoForge
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.registry.Registries
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.neoforge.client.ConfigScreenHandler
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.common.ToolAction
import net.neoforged.neoforge.common.ToolActions
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.KotlinModLoadingContext

@Mod("inventorio")
class InventorioNeoForge {
    private val neoForgeModIntegrations = listOf<ModIntegration>(ClumpsIntegration)

    init
    {
        ScreenTypeProvider.INSTANCE = ScreenTypeProviderNeoForge
        InventorioNetworking.INSTANCE = InventorioNetworkingNeoForge

        val enchantmentRegistry = DeferredRegister.create(Registries.ENCHANTMENT, "inventorio")
        enchantmentRegistry.register(KotlinModLoadingContext.get().getKEventBus())
        enchantmentRegistry.register("deep_pockets") { -> DeepPocketsEnchantment }

        val recipeRegistry = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "inventorio")
        recipeRegistry.register(KotlinModLoadingContext.get().getKEventBus())
        val serializer = SpecialRecipeSerializer { category -> DeepPocketsBookRecipe(category) }
        DeepPocketsBookRecipe.SERIALIZER = serializer
        recipeRegistry.register("deep_pockets_book") { -> serializer }

        initToolBelt()
        KotlinModLoadingContext.get().getKEventBus().register(InventorioNetworkingNeoForge)

        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(NeoForgeEvents)
            MinecraftClient.getInstance().options.allKeys += InventorioControls.keys
            PlayerSettings.load(FMLPaths.CONFIGDIR.get().resolve("inventorio.json").toFile())
            ScreenTypeProviderNeoForge.registerScreen()
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory::class.java) {
                ConfigScreenHandler.ConfigScreenFactory { _, parent -> PlayerSettingsScreen.get(parent) }
            }
        }

        InventorioModIntegration.addModIntegrations(neoForgeModIntegrations)
        InventorioModIntegration.apply()
    }

    private fun initToolBelt() {
        // What this actually does is loads the [InventorioAPI] which creates the ToolBelt
        // The reason why we do it this way is because we can't guarantee that other mods
        //  won't call [InventorioAPI] BEFORE [InventorioNeoForge#onInitialize] has been invoked
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
