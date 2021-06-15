package me.lizardofoz.inventorio.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive

open class SettingsEntry(
    @JvmField val defaultValue: Any,
    @JvmField val configKey: String,
    @JvmField val displayText: String,
    @JvmField val tooltipText: String?,
    @JvmField val valueAsElement: (Any) -> JsonElement,
    @JvmField val elementAsValue: (JsonElement?) -> Any,
    @JvmField val onChange: (Any) -> Unit = { }
)
{
    var value = defaultValue
        set(value) {
            field = value
            onChange(value)
        }

    fun tryElementAsValue(element: JsonElement?): Any
    {
        return try
        {
            elementAsValue(element)
        }
        catch (ignored: Throwable)
        {
            defaultValue
        }
    }

    override fun equals(other: Any?): Boolean
    {
        return other is SettingsEntry && configKey == other.configKey
    }

    override fun hashCode(): Int
    {
        return configKey.hashCode()
    }
}

class SettingsEntryBoolean(defaultValue: Boolean, configKey: String, displayText: String, tooltipText: String? = null, onChange: (Any) -> Unit = { })
    : SettingsEntry(defaultValue, configKey, displayText, tooltipText, { JsonPrimitive(it == true) }, { it?.asBoolean ?: defaultValue }, onChange)
{
    val boolValue get() = this.value == true
}