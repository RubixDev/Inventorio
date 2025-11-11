package de.rubixdev.inventorio.integration

import de.rubixdev.inventorio.config.GlobalSettings
import de.rubixdev.inventorio.integration.curios.ICuriosContainer
import net.minecraft.entity.player.PlayerEntity
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.tick.EntityTickEvent

object CuriosIntegration : ModIntegration() {
    override val modId = "curios"

    override val shouldApply: Boolean
        get() = super.shouldApply && GlobalSettings.curiosIntegration.boolValue

    override fun apply() {
        NeoForge.EVENT_BUS.addListener { event: EntityTickEvent.Post ->
            val entity = event.entity
            if (entity is PlayerEntity) {
                (entity.currentScreenHandler as? ICuriosContainer)?.`inventorio$checkQuickMove`()
            }
        }
    }
}
