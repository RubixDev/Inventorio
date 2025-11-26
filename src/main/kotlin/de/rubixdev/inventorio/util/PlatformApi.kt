package de.rubixdev.inventorio.util

@Suppress("UNUSED_PARAMETER", "RedundantNullableReturnType")
object PlatformApi {
    fun isModLoaded(modId: String): Boolean = throw IllegalStateException()
    fun modDisplayname(modId: String): String? = throw IllegalStateException()
    fun modVersion(modId: String): String? = throw IllegalStateException()
}
