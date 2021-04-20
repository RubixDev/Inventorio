package me.lizardofoz.inventorio.util

import net.minecraft.screen.*
import java.awt.Point

object ScreenHandlerOffsets
{
    private val screenHandlerOffsets = mutableMapOf<Class<out ScreenHandler>, Point>()
    private var customOffsets = emptyMap<Class<out ScreenHandler>, Point>()

    fun getScreenHandlerOffset(screenHandler: ScreenHandler): Point
    {
        return screenHandlerOffsets[screenHandler.javaClass] ?: customOffsets.getOrDefault(screenHandler.javaClass, Point(0, 0))
    }

    init
    {
        init()
    }

    fun init()
    {
        screenHandlerOffsets.clear()
        screenHandlerOffsets[EnchantmentScreenHandler::class.java] = Point(0, 5)
        screenHandlerOffsets[AnvilScreenHandler::class.java] = Point(0, 12)
        screenHandlerOffsets[GrindstoneScreenHandler::class.java] = Point(0, 12)
        screenHandlerOffsets[CartographyTableScreenHandler::class.java] = Point(0, 9)
        screenHandlerOffsets[HorseScreenHandler::class.java] = Point(0, 8)
        screenHandlerOffsets[MerchantScreenHandler::class.java] = Point(100, 8) //todo villager gui is ugly
        screenHandlerOffsets[StonecutterScreenHandler::class.java] = Point(0, 19)

    }

    fun setCustomTitleOffsets(offsets: Map<Class<out ScreenHandler>, Point>)
    {
        customOffsets = offsets
    }
}