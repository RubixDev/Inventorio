package me.lizardofoz.inventorio

import dev.emi.trinkets.api.SlotGroups
import dev.emi.trinkets.api.TrinketSlots
import me.lizardofoz.inventorio.client.config.InventorioConfig
import me.lizardofoz.inventorio.client.InventorioControls
import me.lizardofoz.inventorio.client.InventorioControlsFabric
import me.lizardofoz.inventorio.client.InventorioKeyHandler
import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.extra.InventorioServerConfig
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.packet.InventorioNetworkingFabric
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

open class InventorioFabric : ModInitializer
{
    override fun onInitialize()
    {
        InventorioNetworking.INSTANCE = InventorioNetworkingFabric
        InventorioServerConfig.load(FabricLoader.getInstance().configDir.toFile())

        Registry.register(Registry.ENCHANTMENT, Identifier("inventorio", "deep_pockets"), DeepPocketsEnchantment)

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
        {
            InventorioControls.INSTANCE = InventorioControlsFabric
            ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick { InventorioKeyHandler.tick() })
            InventorioConfig.load(FabricLoader.getInstance().configDir.toFile())
        }

        //This isn't particularly nice, but it allows to move trinket slots after all mods have added their additional slots
        if (FabricLoader.getInstance().isModLoaded("trinkets"))
        {
            if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
                ClientLifecycleEvents.CLIENT_STARTED.register(ClientLifecycleEvents.ClientStarted { addCompatibilityWithTrinkets() })
            else
                ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { addCompatibilityWithTrinkets() })
        }
    }

    private fun addCompatibilityWithTrinkets()
    {
        val feetGroup = TrinketSlots.slotGroups.first { it.name == SlotGroups.FEET }
        for (slotGroup in TrinketSlots.slotGroups)
        {
            if (slotGroup.name == SlotGroups.HAND || slotGroup.name == SlotGroups.OFFHAND)
            {
                for (slot in slotGroup.slots)
                    feetGroup.slots.add(TrinketSlots.Slot(slot.name, slot.texture, feetGroup))
                slotGroup.slots.clear()
                slotGroup.vanillaSlot = -1
                slotGroup.defaultSlot = null
            }
        }
    }
}