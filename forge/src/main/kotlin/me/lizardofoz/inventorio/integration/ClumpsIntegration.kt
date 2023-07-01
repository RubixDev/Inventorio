package me.lizardofoz.inventorio.integration

import com.blamejared.clumps.api.events.RepairEvent
import me.lizardofoz.inventorio.api.InventorioAPI
import me.lizardofoz.inventorio.util.logger
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.ModList

object ClumpsIntegration : ModIntegration()
{
    override val name = "clumps"
    override val displayName = "Clumps"

    override fun testForge(): Boolean
    {
        return ModList.get().isLoaded("clumps")
    }

    override fun applyOnLaunchInner()
    {
        try
        {
            MinecraftForge.EVENT_BUS.addListener { event: RepairEvent ->
                event.value = InventorioAPI.getInventoryAddon(event.player)!!.mendToolBeltItems(event.value)
            }
        }
        catch (ignored: Throwable)
        {
        }
    }
}