package me.lizardofoz.inventorio.client

import com.google.gson.Gson
import com.google.gson.JsonObject
import me.lizardofoz.inventorio.util.HotBarSimplified
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import java.io.FileReader
import java.io.FileWriter


@Environment(EnvType.CLIENT)
object InventorioConfigData
{
    private val file = FabricLoader.getInstance().configDir.resolve("inventorio.json").toFile()

    //Global
    var simplifiedHotBar = HotBarSimplified.OFF

    fun scrollSimplifiedHotBar(): HotBarSimplified
    {
        simplifiedHotBar = when (simplifiedHotBar)
        {
            HotBarSimplified.OFF -> HotBarSimplified.ONLY_VISUAL
            HotBarSimplified.ONLY_VISUAL -> HotBarSimplified.ON
            else -> HotBarSimplified.OFF
        }
        save()
        return simplifiedHotBar
    }

    fun save()
    {
        FileWriter(file).use { writer ->
            val configRoot = JsonObject()
            configRoot.addProperty("SimplifiedHotBar", simplifiedHotBar.name)
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
                    simplifiedHotBar = HotBarSimplified.valueOf(configRoot.get("SimplifiedHotBar").asString)
                }
                catch (ignored: Exception)
                {
                }
            }
    }
}