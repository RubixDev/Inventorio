package me.lizardofoz.inventorio.modcomp

abstract class ModComp
{
    private var isActive = false

    abstract val name: String

    fun test(): Boolean
    {
        isActive = if (isFabric) testFabric() else testForge()
        return isActive
    }

    open fun applyOnLaunch(vararg args: Any?): Boolean
    {
        if (isActive)
            applyOnLaunchInner(*args)
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
        throw IllegalStateException("Test Fabric has not been implemented or ran in incorrect environment")
    }

    protected open fun testForge(): Boolean
    {
        throw IllegalStateException("Test Forge has not been implemented or ran in incorrect environment")
    }

    protected open fun applyOnLaunchInner(vararg args: Any?) { }
    protected open fun applyInRuntimeInner(vararg args: Any?) { }

    companion object
    {
        var isFabric = false
            private set

        init
        {
            try
            {
                net.fabricmc.loader.api.FabricLoader.getInstance()
                isFabric = true
            }
            catch (e: Throwable)
            {
            }
        }
    }
}