package me.lizardofoz.inventorio.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.ingame.*
import java.awt.Point

@Environment(EnvType.CLIENT)
object InventoryOffsets
{
    private val inventoryTextOffsets = mutableMapOf<Class<out HandledScreen<*>>, Point>()
    private var customOffsets = emptyMap<Class<out HandledScreen<*>>, Point>()

    fun getInventoryTextOffset(screen: HandledScreen<*>): Point
    {
        return inventoryTextOffsets[screen.javaClass] ?: customOffsets.getOrDefault(screen.javaClass, Point(0, 0))
    }

    init
    {
        init()
    }

    fun init()
    {
        inventoryTextOffsets.clear()
        inventoryTextOffsets[GenericContainerScreen::class.java] = Point(-27, 7)
        inventoryTextOffsets[CraftingScreen::class.java] = Point(-27, 8)
        inventoryTextOffsets[AnvilScreen::class.java] = Point(-27, 14)
        inventoryTextOffsets[BrewingStandScreen::class.java] = Point(-27, 13)
        inventoryTextOffsets[EnchantmentScreen::class.java] = Point(-27, 7)
        inventoryTextOffsets[CartographyTableScreen::class.java] = Point(-27, 16)
        inventoryTextOffsets[SmithingScreen::class.java] = Point(-27, 2)
        inventoryTextOffsets[Generic3x3ContainerScreen::class.java] = Point(-27, 8)
        inventoryTextOffsets[GrindstoneScreen::class.java] = Point(-27, 7)
        inventoryTextOffsets[HopperScreen::class.java] = Point(-27, 8)
        inventoryTextOffsets[HorseScreen::class.java] = Point(-2700, 8) //You didn't see that, ok?
        inventoryTextOffsets[LoomScreen::class.java] = Point(-27, 13)
        inventoryTextOffsets[ShulkerBoxScreen::class.java] = Point(-27, 9)
        inventoryTextOffsets[StonecutterScreen::class.java] = Point(-27, 7)

        inventoryTextOffsets[FurnaceScreen::class.java] = Point(-27, 8)
        inventoryTextOffsets[BlastFurnaceScreen::class.java] = Point(-27, 8)
        inventoryTextOffsets[SmokerScreen::class.java] = Point(-27, 8)
    }

    fun setCustomTitleOffsets(offsets: Map<Class<out HandledScreen<*>>, Point>)
    {
        customOffsets = offsets
    }
}