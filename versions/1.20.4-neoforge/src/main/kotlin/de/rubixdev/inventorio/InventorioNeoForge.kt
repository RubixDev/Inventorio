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
import de.rubixdev.inventorio.packet.InventorioNetworkingNeoForge
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

    init {
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
        //#if MC >= 12004
        KotlinModLoadingContext.get().getKEventBus().register(InventorioNetworkingNeoForge)
        //#endif

        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(NeoForgeEvents)
            KotlinModLoadingContext.get().getKEventBus().register(NeoForgeModEvents)
            MinecraftClient.getInstance().options.allKeys += InventorioControls.keys
            PlayerSettings.load(FMLPaths.CONFIGDIR.get().resolve("inventorio.json").toFile())
            //#if MC >= 12004
            KotlinModLoadingContext.get().getKEventBus().register(ScreenTypeProviderNeoForge)
            //#else
            //$$ ScreenTypeProviderNeoForge.registerScreen()
            //#endif
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory::class.java) {
                ConfigScreenHandler.ConfigScreenFactory { _, parent -> PlayerSettingsScreen.get(parent) }
            }
        }

        InventorioModIntegration.applyModIntegrations(neoForgeModIntegrations)
    }

    private fun initToolBelt() {
        // What this actually does is loads the [InventorioAPI] which creates the ToolBelt
        // The reason why we do it this way is that we can't guarantee that other mods
        // won't call [InventorioAPI] BEFORE [InventorioNeoForge#onInitialize] has been invoked
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
