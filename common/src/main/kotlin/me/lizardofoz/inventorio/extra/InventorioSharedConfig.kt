package me.lizardofoz.inventorio.extra

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object InventorioSharedConfig
{
    private lateinit var file : File

    var expandedEnderChest = true
        private set
    var infinityBowNeedsNoArrow = true
        private set
    var totemFromUtilityBelt = true
        private set
    var allowSwappedHands = true
        private set

    init
    {
        load(File(".").resolve("config"))
    }

    private fun load(configFolder: File)
    {
        try
        {
            this.file = configFolder.resolve("inventorio_shared.json")
            if (file.exists())
            {
                FileReader(file).use { writer ->
                    val configRoot = Gson().fromJson(writer, JsonObject::class.java)
                    expandedEnderChest = configRoot.get("ExpandedEnderChest").asBoolean
                    infinityBowNeedsNoArrow = configRoot.get("InfinityBowNeedsNoArrow").asBoolean
                    totemFromUtilityBelt = configRoot.get("TotemFromUtilityBelt").asBoolean
                    allowSwappedHands = configRoot.get("AllowSwappedHands").asBoolean
                }
            }
            else
            {
                FileWriter(file).use { writer ->
                    val configRoot = JsonObject()
                    configRoot.addProperty("ExpandedEnderChest", true)
                    configRoot.addProperty("InfinityBowNeedsNoArrow", true)
                    configRoot.addProperty("TotemFromUtilityBelt", true)
                    configRoot.addProperty("AllowSwappedHands", true)
                    Gson().toJson(configRoot, writer)
                }
            }
        }
        catch (ignored: Exception)
        {
            try
            {
                FileWriter(file).use { writer ->
                    val configRoot = JsonObject()
                    configRoot.addProperty("ExpandedEnderChest", expandedEnderChest)
                    configRoot.addProperty("InfinityBowNeedsNoArrow", infinityBowNeedsNoArrow)
                    configRoot.addProperty("TotemFromUtilityBelt", totemFromUtilityBelt)
                    configRoot.addProperty("AllowSwappedHands", allowSwappedHands)
                    Gson().toJson(configRoot, writer)
                }
            }
            catch (ignored: Exception)
            {
            }
        }
    }
}