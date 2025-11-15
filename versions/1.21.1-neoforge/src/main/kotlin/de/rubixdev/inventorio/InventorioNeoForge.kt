package de.rubixdev.inventorio

import de.rubixdev.inventorio.api.InventorioAPI
import de.rubixdev.inventorio.client.configscreen.PlayerSettingsScreen
import de.rubixdev.inventorio.client.control.InventorioControls
import de.rubixdev.inventorio.config.PlayerSettings
import de.rubixdev.inventorio.enchantment.DeepPocketsBookRecipe
import de.rubixdev.inventorio.integration.ClumpsIntegration
import de.rubixdev.inventorio.integration.CuriosIntegration
import de.rubixdev.inventorio.integration.InventorioModIntegration
import de.rubixdev.inventorio.packet.InventorioNetworking
import de.rubixdev.inventorio.packet.InventorioNetworkingNeoForge
import de.rubixdev.inventorio.util.MOD_ID
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.registry.Registries
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.ItemAbilities
import net.neoforged.neoforge.common.ItemAbility
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.KotlinModLoadingContext

//#if MC < 12101
//$$ import de.rubixdev.inventorio.enchantment.DeepPocketsEnchantment
//#endif

@Mod(MOD_ID)
class InventorioNeoForge {
    private val neoForgeModIntegrations = listOf(ClumpsIntegration, CuriosIntegration)

    init {
        ScreenTypeProvider.INSTANCE = ScreenTypeProviderNeoForge
        InventorioNetworking.INSTANCE = InventorioNetworkingNeoForge

        //#if MC < 12101
        //$$ val enchantmentRegistry = DeferredRegister.create(Registries.ENCHANTMENT, MOD_ID)
        //$$ enchantmentRegistry.register(KotlinModLoadingContext.get().getKEventBus())
        //$$ enchantmentRegistry.register("deep_pockets") { -> DeepPocketsEnchantment }
        //#endif

        val recipeRegistry = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID)
        recipeRegistry.register(KotlinModLoadingContext.get().getKEventBus())
        val serializer = SpecialRecipeSerializer { category -> DeepPocketsBookRecipe(category) }
        DeepPocketsBookRecipe.SERIALIZER = serializer
        recipeRegistry.register("deep_pockets_book") { -> serializer }

        initToolBelt()
        KotlinModLoadingContext.get().getKEventBus().register(InventorioNetworkingNeoForge)

        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(NeoForgeEvents)
            KotlinModLoadingContext.get().getKEventBus().register(NeoForgeModEvents)
            MinecraftClient.getInstance().options.allKeys += InventorioControls.keys
            PlayerSettings.load(FMLPaths.CONFIGDIR.get().resolve("inventorio.json").toFile())
            KotlinModLoadingContext.get().getKEventBus().register(ScreenTypeProviderNeoForge)
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory::class.java) {
                IConfigScreenFactory { _, parent -> PlayerSettingsScreen.get(parent) }
            }
        }

        InventorioModIntegration.applyModIntegrations(neoForgeModIntegrations)
    }

    private fun initToolBelt() {
        // What this actually does is loads the [InventorioAPI] which creates the ToolBelt
        // The reason why we do it this way is that we can't guarantee that other mods
        // won't call [InventorioAPI] BEFORE [InventorioNeoForge#onInitialize] has been invoked
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_PICKAXE)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ItemAbilities.PICKAXE_DIG)
        }
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_SWORD)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ItemAbilities.SWORD_SWEEP) // todo sword
        }
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_AXE)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ItemAbilities.AXE_DIG)
        }
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_SHOVEL)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ItemAbilities.SHOVEL_DIG)
        }
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_HOE)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ItemAbilities.HOE_DIG)
        }
    }
    private fun testToolType(itemStack: ItemStack, vararg toolActions: ItemAbility): Boolean {
        return toolActions.any { itemStack.canPerformAction(it) }
    }
}
