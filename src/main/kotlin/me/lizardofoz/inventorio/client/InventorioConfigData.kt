package me.lizardofoz.inventorio.client

import com.google.gson.Gson
import com.google.gson.JsonObject
import me.lizardofoz.inventorio.util.SegmentedHotbar
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import java.io.FileReader
import java.io.FileWriter


@Environment(EnvType.CLIENT)
object InventorioConfigData
{
    private val file = FabricLoader.getInstance().configDir.resolve("inventorio.json").toFile()

    var segmentedHotbar = SegmentedHotbar.OFF
    var scrollWheelUtilityBelt = false

    fun switchSegmentedHotbarMode(): SegmentedHotbar
    {
        segmentedHotbar = when (segmentedHotbar)
        {
            SegmentedHotbar.OFF -> SegmentedHotbar.ONLY_VISUAL
            SegmentedHotbar.ONLY_VISUAL -> SegmentedHotbar.ON
            else -> SegmentedHotbar.OFF
        }
        save()
        return segmentedHotbar
    }

    fun switchScrollWheelUtilityBeltMode(): Boolean
    {
        scrollWheelUtilityBelt = !scrollWheelUtilityBelt
        save()
        return scrollWheelUtilityBelt
    }

    fun save()
    {
        FileWriter(file).use { writer ->
            val configRoot = JsonObject()
            configRoot.addProperty("SegmentedHotbar", segmentedHotbar.name)
            configRoot.addProperty("ScrollWheelUtilityBelt", scrollWheelUtilityBelt)
            Gson().toJson(configRoot, writer)
        }
    }

    fun load()
    {
        if (file.exists())
            FileReader(file).use { writer ->
                try
                {
                    val configRoot = Gson().fromJson(writer, JsonObject::class.java)
                    segmentedHotbar = SegmentedHotbar.valueOf(configRoot.get("SegmentedHotbar").asString)
                    scrollWheelUtilityBelt = configRoot.get("ScrollWheelUtilityBelt").asBoolean
                }
                catch (ignored: Exception)
                {
                }
            }
    }
}