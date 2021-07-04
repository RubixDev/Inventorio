package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.player.InventorioScreenHandler
import net.minecraft.screen.ScreenHandlerType

interface ScreenTypeProvider
{
    fun getScreenHandlerType(): ScreenHandlerType<InventorioScreenHandler>

    companion object
    {
        lateinit var INSTANCE: ScreenTypeProvider
    }
}