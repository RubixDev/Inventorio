package de.rubixdev.inventorio.integration

import com.blamejared.clumps.api.events.RepairEvent
import de.rubixdev.inventorio.api.InventorioAPI
import net.neoforged.neoforge.common.NeoForge

object ClumpsIntegration : ModIntegration() {
    override val modId = "clumps"

    override fun apply() {
        NeoForge.EVENT_BUS.addListener { event: RepairEvent ->
            event.value = InventorioAPI.getInventoryAddon(event.player)!!.mendToolBeltItems(event.value)
        }
    }
}
