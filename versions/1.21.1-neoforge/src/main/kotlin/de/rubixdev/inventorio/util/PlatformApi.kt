package de.rubixdev.inventorio.util

import net.neoforged.fml.ModList

object PlatformApi {
    fun isModLoaded(modId: String): Boolean = ModList.get().isLoaded(modId)
    fun modDisplayname(modId: String): String? = ModList.get().getModContainerById(modId).orElse(null)?.modInfo?.displayName
    fun modVersion(modId: String): String? = ModList.get().getModContainerById(modId).orElse(null)?.modInfo?.version?.toString()
}
