package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.player.InventorioScreenHandler
import net.minecraft.screen.ScreenHandlerType

interface ScreenTypeProvider {
    fun getScreenHandlerType(): ScreenHandlerType<InventorioScreenHandler>

    companion object {
        // TODO: custom ktlint rule to allow lateinit var in caps
        @Suppress("ktlint:standard:property-naming")
        lateinit var INSTANCE: ScreenTypeProvider
    }
}
