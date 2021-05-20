package me.lizardofoz.inventorio.integration

import org.apache.logging.log4j.LogManager

object InventorioModIntegration
{
    private val logger = LogManager.getLogger("Inventorio Mod Integration")!!

    private val modIntegrations = arrayListOf<ModIntegration>()

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
                logger.info("Mod integration ${if (testResult) "succeeded" else "failed"}: ${modIntegration.displayName}")
                if (testResult)
                    modIntegration.applyOnLaunch()
            }
            catch (e: Throwable)
            {
                logger.error("Failed to apply a mod integration module ${modIntegration.displayName} (${modIntegration.name})", e)
            }
        }
    }
}