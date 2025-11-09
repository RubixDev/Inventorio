package de.rubixdev.inventorio.util

import net.neoforged.fml.ModList

object PlatformApi {
    fun isModLoaded(modId: String): Boolean = ModList.get().isLoaded(modId)
}
