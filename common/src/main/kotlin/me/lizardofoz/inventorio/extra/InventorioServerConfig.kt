package me.lizardofoz.inventorio.extra

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object InventorioServerConfig
{
    private lateinit var file : File

    var expandedEnderChest = true
    var infinityBowNeedsNoArrow = true

    fun load(configFolder: File)
    {
        try
        {
            this.file = configFolder.resolve("inventorio_server.json")
            if (file.exists())
            {
                FileReader(file).use { writer ->
                    val configRoot = Gson().fromJson(writer, JsonObject::class.java)
                    expandedEnderChest = configRoot.get("ExpandedEnderChest").asBoolean
                    infinityBowNeedsNoArrow = configRoot.get("InfinityBowNeedsNoArrow").asBoolean
                }
            }
            else
            {
                FileWriter(file).use { writer ->
                    val configRoot = JsonObject()
                    configRoot.addProperty("ExpandedEnderChest", true)
                    configRoot.addProperty("InfinityBowNeedsNoArrow", true)
                    Gson().toJson(configRoot, writer)
                }
            }
        }
        catch (ignored: Exception)
        {
        }
    }
}