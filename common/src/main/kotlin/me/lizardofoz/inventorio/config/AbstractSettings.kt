package me.lizardofoz.inventorio.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File
import java.io.FileReader
import java.io.FileWriter

abstract class AbstractSettings
{
    private lateinit var configFile: File
    var entries = emptyList<SettingsEntry>()
        protected set

    fun asJson(): JsonObject
    {
        val configRoot = JsonObject()
        entries.forEach { configRoot.add(it.configKey, it.valueAsElement(it.value)) }
        return configRoot
    }

    /**
     * Returns true if there's an option not covered by an input json.
     * (Used in [load] to write automatically write new entries into the config)
     */
    fun fromJson(jsonObject: JsonObject): Boolean
    {
        val missingEntries = entries.any { !jsonObject.has(it.configKey) }
        for (option in entries)
            option.value = option.elementAsValue(jsonObject.get(option.configKey))
        return missingEntries
    }

    fun anyChanges(jsonObject: JsonObject): Boolean
    {
        return entries.any { option -> option.value != option.elementAsValue(jsonObject.get(option.configKey)) }
    }

    fun save()
    {
        try
        {
            FileWriter(configFile).use { Gson().toJson(asJson(), it) }
        }
        catch (ignored: Exception)
        {
        }
    }

    fun load(configFile: File)
    {
        try
        {
            this.configFile = configFile
            if (!configFile.exists() || FileReader(configFile).use { fromJson(Gson().fromJson(it, JsonObject::class.java)) })
                save()
        }
        catch (ignored: Exception)
        {
            save()
        }
    }
}