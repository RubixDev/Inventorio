package me.lizardofoz.inventorio.integration

abstract class ModIntegration
{
    var isActive = false
        private set

    abstract val name: String
    abstract val displayName: String

    fun test(): Boolean
    {
        isActive = if (InventorioModIntegration.isFabric) testFabric() else testForge()
        return isActive
    }

    open fun applyOnLaunch(vararg args: Any?): Boolean
    {
        if (isActive)
            applyOnLaunchInner(*args)
        return isActive
    }

    open fun applyInRuntime(vararg args: Any?): Boolean
    {
        if (isActive)
            applyInRuntimeInner(*args)
        return isActive
    }

    protected open fun testFabric(): Boolean
    {
        throw IllegalStateException("Test Fabric has not been implemented or ran in incorrect environment")
    }

    protected open fun testForge(): Boolean
    {
        throw IllegalStateException("Test Forge has not been implemented or ran in incorrect environment")
    }

    protected open fun applyOnLaunchInner(vararg args: Any?) { }
    protected open fun applyInRuntimeInner(vararg args: Any?) { }
}