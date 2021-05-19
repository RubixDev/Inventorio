package me.lizardofoz.inventorio.modcomp

import org.apache.logging.log4j.LogManager

object InventorioModCompatibility
{
    private val modComps = arrayListOf<ModComp>()

    fun addModComps(fabricModComps: Collection<ModComp>)
    {
        modComps.addAll(fabricModComps)
    }

    fun getModCompByName(name: String): ModComp?
    {
        return modComps.firstOrNull { it.name == name }
    }

    fun apply()
    {
        for (modComp in modComps)
        {
            try
            {
                if (modComp.test())
                    modComp.applyOnLaunch()
            }
            catch (e: Throwable)
            {
                LogManager.getLogger("Inventorio Mod Compatibility").error("Failed to apply a mod compatibility module ${modComp.name}", e)
            }
        }
    }
}