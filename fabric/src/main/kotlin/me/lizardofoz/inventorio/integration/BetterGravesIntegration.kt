package me.lizardofoz.inventorio.integration

import bettergraves.api.BetterGravesAPI
import com.google.common.collect.ImmutableMap
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemStack

object BetterGravesIntegration : ModIntegration()
{
    override val name = "bettergraves"
    override val displayName = "Better Graves"

    override fun testFabric(): Boolean
    {
        return FabricLoader.getInstance().isModLoaded("bettergraves")
    }

    override fun applyOnLaunchInner()
    {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
            ClientLifecycleEvents.CLIENT_STARTED.register(ClientLifecycleEvents.ClientStarted { registerDeathHandler() })
        else
            ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { registerDeathHandler() })
    }

    private fun registerDeathHandler()
    {
        BetterGravesAPI.registerDeathHandler("inventorio", { player, _ ->
            val addon = player.inventoryAddon ?: return@registerDeathHandler ImmutableMap.of()
            ImmutableMap.copyOf(addon.stacks.withIndex().associate { it.index to it.value })
        }, { player, items ->
            val addon = player.inventoryAddon?: return@registerDeathHandler
            for ((index, itemStack) in items.entries)
            {
                if (index in addon.stacks.indices)
                    addon.stacks[index] = itemStack
                else
                    player.dropItem(itemStack, true)
            }
        })
    }
}