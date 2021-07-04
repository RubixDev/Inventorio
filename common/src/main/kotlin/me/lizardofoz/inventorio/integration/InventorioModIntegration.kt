package me.lizardofoz.inventorio.integration

import me.lizardofoz.inventorio.util.logger

object InventorioModIntegration
{
    var isFabric = false
        private set
    private val modIntegrations = arrayListOf<ModIntegration>()

    init
    {
        try
        {
            isFabric = this.javaClass.classLoader.loadClass("me.lizardofoz.inventorio.InventorioFabric") != null
        }
        catch (ignored: Throwable) { }
    }

    fun addModIntegrations(modIntegrations: Collection<ModIntegration>)
    {
        this.modIntegrations.addAll(modIntegrations)
    }

    fun getModIntegrationByName(name: String): ModIntegration?
    {
        return modIntegrations.firstOrNull { it.name == name }
    }

    fun apply()
    {
        for (modIntegration in modIntegrations)
        {
            try
            {
                val testResult = modIntegration.test()
                if (testResult)
                {
                    modIntegration.applyOnLaunch()
                    logger.info("Mod integration ${if (testResult) "succeeded" else "failed"}: ${modIntegration.displayName}")
                }
            }
            catch (e: Throwable)
            {
                logger.error("Failed to apply a mod integration module ${modIntegration.displayName} (${modIntegration.name})", e)
            }
        }
    }
}