package me.lizardofoz.inventorio.integration

import dev.emi.trinkets.api.SlotGroups
import dev.emi.trinkets.api.TrinketSlots
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader

object TrinketsIntegration : ModIntegration()
{
    override val name = "trinkets"
    override val displayName = "Trinkets"

    override fun testFabric(): Boolean
    {
        return FabricLoader.getInstance().isModLoaded("trinkets")
    }

    override fun applyOnLaunchInner()
    {
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
            ClientLifecycleEvents.CLIENT_STARTED.register(ClientLifecycleEvents.ClientStarted { removeOverlappingTrinketSlots() })
        else
            ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { removeOverlappingTrinketSlots() })
    }

    /**
     * Trinket Slot Group positions are hardcoded within the Trinkets mod and slot positions refresh every tick.
     * Since we can't really move groups, we have to reattach their slots to other groups,
     * which in some cases effectively disables them.
     * Changes in Trinkets requires to fix this.
     */
    private fun removeOverlappingTrinketSlots()
    {
        val feetGroup = TrinketSlots.slotGroups.first { it.name == SlotGroups.FEET }
        val allowedSlots = arrayListOf(SlotGroups.HEAD, SlotGroups.CHEST, SlotGroups.LEGS, SlotGroups.FEET)
        for (slotGroup in TrinketSlots.slotGroups)
        {
            if (!allowedSlots.contains(slotGroup.name))
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