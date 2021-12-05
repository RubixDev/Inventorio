package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.api.InventorioAPI
import me.lizardofoz.inventorio.client.control.InventorioControls
import me.lizardofoz.inventorio.config.PlayerSettings
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.integration.InventorioModIntegration
import me.lizardofoz.inventorio.integration.ModIntegration
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.packet.InventorioNetworkingForge
import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemStack
import net.minecraft.tag.BlockTags
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.ToolAction
import net.minecraftforge.common.ToolActions
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.loading.FMLPaths
import net.minecraftforge.registries.ForgeRegistries

@Mod("inventorio")
class InventorioForge
{
    private val forgeModIntegrations = listOf<ModIntegration>()

    init
    {
        ScreenTypeProvider.INSTANCE = ScreenTypeProviderForge
        InventorioNetworking.INSTANCE = InventorioNetworkingForge
        ForgeRegistries.ENCHANTMENTS.register((DeepPocketsEnchantment as Enchantment).setRegistryName(Identifier("inventorio", "deep_pockets")))
        initToolBelt()

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(ForgeEvents)
            MinecraftClient.getInstance().options.keysAll += InventorioControls.keys
            PlayerSettings.load(FMLPaths.CONFIGDIR.get().resolve("inventorio.json").toFile())
            ScreenTypeProviderForge.registerScreen()
        }

        InventorioModIntegration.addModIntegrations(forgeModIntegrations)
        InventorioModIntegration.apply()
    }

    private fun initToolBelt()
    {
        //What this actually does is loads the [InventorioAPI] which creates the ToolBelt
        //The reason why we do it this way is because we can't guarantee that other mods
        //  won't call [InventorioAPI] BEFORE [InventorioForge#onInitialize] has been invoked
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_PICKAXE)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ToolActions.PICKAXE_DIG)
        }
        InventorioAPI.getToolBeltSlotTemplate(InventorioAPI.SLOT_SWORD)?.addAllowingCondition { itemStack, _ ->
            testToolType(itemStack, ToolActions.SWORD_SWEEP) //todo sword
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
    private fun testToolType(itemStack: ItemStack, vararg toolActions: ToolAction): Boolean
    {
        return toolActions.any { itemStack.canPerformAction(it) }
    }
}