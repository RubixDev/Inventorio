package me.lizardofoz.inventorio.extra

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object InventorioServerConfig
{
    private lateinit var file : File

    var infinityBowNeedsNoArrow = false
    var unloyalTridentCannotBeThrown = false

    fun load(configFolder: File)
    {
        try
        {
            this.file = configFolder.resolve("inventorio_server.json")
            if (file.exists())
            {
                FileReader(file).use { writer ->
                    val configRoot = Gson().fromJson(writer, JsonObject::class.java)
                    infinityBowNeedsNoArrow = configRoot.get("InfinityBowNeedsNoArrow").asBoolean
                    unloyalTridentCannotBeThrown = configRoot.get("UnloyalTridentCannotBeThrown").asBoolean
                }
            }
            else
            {
                FileWriter(file).use { writer ->
                    val configRoot = JsonObject()
                    configRoot.addProperty("InfinityBowNeedsNoArrow", false)
                    configRoot.addProperty("UnloyalTridentCannotBeThrown", false)
                    Gson().toJson(configRoot, writer)
                }
            }
        }
        catch (ignored: Exception)
        {
        }
    }
}