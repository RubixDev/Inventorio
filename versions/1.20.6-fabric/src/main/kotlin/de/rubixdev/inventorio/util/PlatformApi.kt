package de.rubixdev.inventorio.util

import net.fabricmc.loader.api.FabricLoader

object PlatformApi {
    fun isModLoaded(modId: String): Boolean = FabricLoader.getInstance().isModLoaded(modId)
}
