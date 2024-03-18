package de.rubixdev.inventorio.integration

import com.blamejared.clumps.api.events.RepairEvent
import de.rubixdev.inventorio.api.InventorioAPI
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.ModList

object ClumpsIntegration : ModIntegration() {
    override val name = "clumps"
    override val displayName = "Clumps"

    override fun shouldApply() = ModList.get().isLoaded("clumps")

    override fun apply() {
        MinecraftForge.EVENT_BUS.addListener { event: RepairEvent ->
            event.value = InventorioAPI.getInventoryAddon(event.player)!!.mendToolBeltItems(event.value)
        }
    }
}
