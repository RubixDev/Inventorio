package de.rubixdev.inventorio.integration

import com.blamejared.clumps.api.events.ClumpsEvents
import de.rubixdev.inventorio.api.InventorioAPI

object ClumpsIntegration : ModIntegration() {
    override val modId = "clumps"

    override fun apply() {
        ClumpsEvents.REPAIR_EVENT.register { event ->
            event.value = InventorioAPI.getInventoryAddon(event.player)!!.mendToolBeltItems(event.value)
            null
        }
    }
}
