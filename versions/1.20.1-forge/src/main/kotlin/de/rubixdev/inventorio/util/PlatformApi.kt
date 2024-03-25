package de.rubixdev.inventorio.util

import net.minecraftforge.fml.ModList

object PlatformApi {
    fun isModLoaded(modId: String): Boolean = ModList.get().isLoaded(modId)
}
