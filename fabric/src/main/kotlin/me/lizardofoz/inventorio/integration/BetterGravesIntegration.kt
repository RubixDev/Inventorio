package me.lizardofoz.inventorio.integration

import bettergraves.api.BetterGravesAPI
import com.google.common.collect.ImmutableMap
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader

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
        BetterGravesAPI.registerDeathHandler("inventorio", { serverPlayerEntity, damageSource ->
            ImmutableMap.copyOf(serverPlayerEntity.inventoryAddon?.asMap() ?: emptyMap())
        }, { serverPlayerEntity, items ->
            if (items != null)
                serverPlayerEntity.inventoryAddon?.fromMap(items)
        })
    }
}