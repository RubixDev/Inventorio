package de.rubixdev.inventorio.util

import net.fabricmc.loader.api.FabricLoader

object PlatformApi {
    fun isModLoaded(modId: String): Boolean = FabricLoader.getInstance().isModLoaded(modId)
    fun modDisplayname(modId: String): String? = FabricLoader.getInstance().getModContainer(modId).orElse(null)?.metadata?.name
    fun modVersion(modId: String): String? = FabricLoader.getInstance().getModContainer(modId).orElse(null)?.metadata?.version?.friendlyString
}
