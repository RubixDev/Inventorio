package de.rubixdev.inventorio.integration

/**
 * This class is intended for Inventorio to integrate with other mods,
 * _not_ for other mods to integrate with Inventorio.
 */
abstract class ModIntegration {
    abstract val name: String
    abstract val displayName: String

    abstract fun shouldApply(): Boolean

    abstract fun apply()
}
