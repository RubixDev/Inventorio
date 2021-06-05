package me.lizardofoz.inventorio.integration

abstract class ModIntegration
{
    var isActive = false
        private set

    abstract val name: String
    abstract val displayName: String

    open fun test(): Boolean
    {
        isActive = if (InventorioModIntegration.isFabric) testFabric() else testForge()
        return isActive
    }

    fun applyOnLaunch(): Boolean
    {
        if (isActive)
            applyOnLaunchInner()
        return isActive
    }

    fun applyInRuntime(vararg args: Any?): Boolean
    {
        if (isActive)
            applyInRuntimeInner(*args)
        return isActive
    }

    protected open fun testFabric(): Boolean
    {
        throw IllegalStateException("Test Fabric has not been implemented or ran in incorrect environment for $displayName")
    }

    protected open fun testForge(): Boolean
    {
        throw IllegalStateException("Test Forge has not been implemented or ran in incorrect environment for $displayName")
    }

    protected open fun applyOnLaunchInner() { }
    protected open fun applyInRuntimeInner(vararg args: Any?) { }
}