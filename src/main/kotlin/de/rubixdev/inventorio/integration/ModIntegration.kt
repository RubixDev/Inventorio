package de.rubixdev.inventorio.integration

/**
 * This class is intended for INVENTORIO to integrate with other mods,
 * not for other mods to integrate with INVENTORIO.
 */
abstract class ModIntegration {
    var isActive = false
        private set

    abstract val name: String
    abstract val displayName: String

    open fun test(): Boolean {
        isActive = if (InventorioModIntegration.isFabric) testFabric() else testNeoForge()
        return isActive
    }

    fun applyOnLaunch(): Boolean {
        if (isActive) {
            applyOnLaunchInner()
        }
        return isActive
    }

    fun applyInRuntime(vararg args: Any?): Boolean {
        if (isActive) {
            applyInRuntimeInner(*args)
        }
        return isActive
    }

    protected open fun testFabric(): Boolean {
        throw IllegalStateException("Test Fabric has not been implemented or ran in incorrect environment for $displayName")
    }

    protected open fun testNeoForge(): Boolean {
        throw IllegalStateException("Test NeoForge has not been implemented or ran in incorrect environment for $displayName")
    }

    protected open fun applyOnLaunchInner() { }
    protected open fun applyInRuntimeInner(vararg args: Any?) { }
}
