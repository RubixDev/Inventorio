package de.rubixdev.inventorio.integration

import com.blamejared.clumps.api.events.RepairEvent
import de.rubixdev.inventorio.api.InventorioAPI
import net.neoforged.fml.ModList
import net.neoforged.neoforge.common.NeoForge

object ClumpsIntegration : ModIntegration() {
    override val name = "clumps"
    override val displayName = "Clumps"

    override fun testNeoForge(): Boolean {
        return ModList.get().isLoaded("clumps")
    }

    override fun applyOnLaunchInner() {
        try {
            NeoForge.EVENT_BUS.addListener { event: RepairEvent ->
                event.value = InventorioAPI.getInventoryAddon(event.player)!!.mendToolBeltItems(event.value)
            }
        } catch (ignored: Throwable) {
        }
    }
}
