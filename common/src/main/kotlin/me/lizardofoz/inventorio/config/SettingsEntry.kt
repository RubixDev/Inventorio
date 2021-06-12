package me.lizardofoz.inventorio.config

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.minecraft.text.Text

open class SettingsEntry(
    @JvmField val defaultValue: Any,
    @JvmField val configKey: String,
    @JvmField val displayText: Text,
    @JvmField val tooltipText: Text?,
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

    override fun equals(other: Any?): Boolean
    {
        return other is SettingsEntry && configKey == other.configKey
    }

    override fun hashCode(): Int
    {
        return configKey.hashCode()
    }
}

class SettingsEntryBoolean(defaultValue: Boolean, configKey: String, displayText: Text, tooltipText: Text? = null, onChange: (Any) -> Unit = { })
    : SettingsEntry(defaultValue, configKey, displayText, tooltipText, { JsonPrimitive(it == true) }, { it?.asBoolean ?: defaultValue }, onChange)
{
    val boolValue get() = this.value == true
}