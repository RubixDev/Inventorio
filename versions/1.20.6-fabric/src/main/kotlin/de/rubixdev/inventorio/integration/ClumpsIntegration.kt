package de.rubixdev.inventorio.integration

import com.blamejared.clumps.api.events.ClumpsEvents
import de.rubixdev.inventorio.api.InventorioAPI
import net.fabricmc.loader.api.FabricLoader

object ClumpsIntegration : ModIntegration() {
    override val name = "clumps"
    override val displayName = "Clumps"

    override fun shouldApply() = FabricLoader.getInstance().isModLoaded("clumps")

    override fun apply() {
        ClumpsEvents.REPAIR_EVENT.register { event ->
            event.value = InventorioAPI.getInventoryAddon(event.player)!!.mendToolBeltItems(event.value)
            null
        }
    }
}
