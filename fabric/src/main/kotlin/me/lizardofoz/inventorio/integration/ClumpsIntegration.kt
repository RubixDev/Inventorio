package me.lizardofoz.inventorio.integration

import com.blamejared.clumps.api.events.ClumpsEvents
import com.blamejared.clumps.api.events.RepairEvent
import me.lizardofoz.inventorio.api.InventorioAPI
import net.fabricmc.loader.api.FabricLoader

object ClumpsIntegration : ModIntegration()
{
    override val name = "clumps"
    override val displayName = "Clumps"

    override fun testFabric(): Boolean
    {
        return FabricLoader.getInstance().isModLoaded("clumps")
    }

    override fun applyOnLaunchInner()
    {
        try
        {
            ClumpsEvents.REPAIR_EVENT.register { event: RepairEvent ->
                event.value = InventorioAPI.getInventoryAddon(event.player)!!.mendToolBeltItems(event.value)
                null
            }
        }
        catch (ignored: Throwable)
        {
        }
    }
}