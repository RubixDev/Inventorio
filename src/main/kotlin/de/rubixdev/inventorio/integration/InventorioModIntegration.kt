package de.rubixdev.inventorio.integration

import de.rubixdev.inventorio.util.logger

/**
 * This class is intended for Inventorio to integrate with other mods,
 * _not_ for other mods to integrate with Inventorio.
 */
@Suppress("unused")
object InventorioModIntegration {
    fun applyModIntegrations(modIntegrations: Collection<ModIntegration>) {
        for (modIntegration in modIntegrations) {
            try {
                if (modIntegration.shouldApply()) {
                    modIntegration.apply()
                    logger.info("Mod integration succeeded for ${modIntegration.displayName}")
                } else {
                    logger.info("Skipping mod integration for ${modIntegration.displayName}")
                }
            } catch (e: Throwable) {
                logger.error("Failed to apply mod integration for ${modIntegration.displayName} (${modIntegration.name})", e)
            }
        }
    }
}
