package me.lizardofoz.inventorio.client

import com.google.gson.Gson
import com.google.gson.JsonObject
import me.lizardofoz.inventorio.util.SegmentedHotbar
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import java.io.File
import java.io.FileReader
import java.io.FileWriter

@Environment(EnvType.CLIENT)
object InventorioConfig
{
    private lateinit var file : File

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
        try
        {
            FileWriter(file).use { writer ->
                val configRoot = JsonObject()
                configRoot.addProperty("SegmentedHotbar", segmentedHotbar.name)
                configRoot.addProperty("ScrollWheelUtilityBelt", scrollWheelUtilityBelt)
                Gson().toJson(configRoot, writer)
            }
        }
        catch (ignored: Exception)
        {
        }
    }

    fun load(configFolder: File)
    {
        try
        {
            this.file = configFolder.resolve("inventorio.json")
            if (file.exists())
                FileReader(file).use { writer ->
                    val configRoot = Gson().fromJson(writer, JsonObject::class.java)
                    segmentedHotbar = SegmentedHotbar.valueOf(configRoot.get("SegmentedHotbar").asString)
                    scrollWheelUtilityBelt = configRoot.get("ScrollWheelUtilityBelt").asBoolean
                }
        }
        catch (ignored: Exception)
        {
        }
    }
}