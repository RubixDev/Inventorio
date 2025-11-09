package de.rubixdev.inventorio.integration

import de.rubixdev.inventorio.util.PlatformApi

/**
 * This class is intended for Inventorio to integrate with other mods,
 * _not_ for other mods to integrate with Inventorio.
 */
abstract class ModIntegration {
    abstract val modId: String

    open val displayName: String
        get() = PlatformApi.modDisplayname(modId) ?: modId

    open val shouldApply: Boolean
        get() = PlatformApi.isModLoaded(modId)

    abstract fun apply()
}
