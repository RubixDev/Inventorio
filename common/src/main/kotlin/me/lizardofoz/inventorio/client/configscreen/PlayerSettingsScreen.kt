package me.lizardofoz.inventorio.client.configscreen

import me.lizardofoz.inventorio.config.SettingsEntryBoolean
import me.lizardofoz.inventorio.config.PlayerSettings
import me.lizardofoz.inventorio.config.GlobalSettings
import me.lizardofoz.inventorio.config.SettingsEntry
import me.lizardofoz.inventorio.util.ScrollWheelUtilityBeltMode
import me.lizardofoz.inventorio.util.SegmentedHotbar
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.TranslatableText

@Environment(EnvType.CLIENT)
object PlayerSettingsScreen
{
    fun get(parent: Screen?): Screen
    {
        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setSavingRunnable(PlayerSettings::save)
            .setTitle(TranslatableText("inventorio.settings.player.title"))
        val entryBuilder = builder.entryBuilder()
        val category = builder.getOrCreateCategory(TranslatableText("inventorio.settings.player.title"))

        if (GlobalSettings.allowSwappedHands.boolValue)
            addBoolEntry(category, entryBuilder, PlayerSettings.swappedHands, false, false)

        addEnumEntry(category, entryBuilder, PlayerSettings.segmentedHotbar, false, false, SegmentedHotbar::class.java, SegmentedHotbar.OFF)
        addEnumEntry(category, entryBuilder, PlayerSettings.scrollWheelUtilityBelt, false, false, ScrollWheelUtilityBeltMode::class.java, ScrollWheelUtilityBeltMode.OFF)
        addBoolEntry(category, entryBuilder, PlayerSettings.skipEmptyUtilitySlots, false, false)
        addBoolEntry(category, entryBuilder, PlayerSettings.useItemAppliesToOffhand, false, false)
        addBoolEntry(category, entryBuilder, PlayerSettings.canThrowUnloyalTrident, false, false)
        addBoolEntry(category, entryBuilder, PlayerSettings.aggressiveButtonRemoval, false, false)
        addBoolEntry(category, entryBuilder, PlayerSettings.toggleButton, false, false)

        if (!GlobalSettings.allowSwappedHands.boolValue)
            category.addEntry(
                entryBuilder
                    .startTextDescription(TranslatableText("inventorio.settings.player.swapped_hands.disabled"))
                    .setTooltip(TranslatableText("inventorio.settings.player.swapped_hands.disabled.tooltip"))
                    .build()
            )
        return builder.build()
    }

    fun <T : Enum<*>> addEnumEntry(category: ConfigCategory, entryBuilder: ConfigEntryBuilder, settingsEntry: SettingsEntry, requireRestart: Boolean, blocked: Boolean, enumClass: Class<T>, defaultValue: T)
    {
        if (blocked)
        {
            category.addEntry(entryBuilder
                .startTextDescription(TranslatableText(settingsEntry.displayText)
                    .append(" = ")
                    .append( TranslatableText("${settingsEntry.displayText}.${settingsEntry.value}")))
                .build())
            return
        }

        val builder = entryBuilder
            .startEnumSelector(
                TranslatableText(settingsEntry.displayText),
                enumClass,
                settingsEntry.value as T
            )
            .setEnumNameProvider { TranslatableText("${settingsEntry.displayText}.${it.name}") }
            .setDefaultValue(defaultValue)
            .setSaveConsumer { settingsEntry.value = it }
        if (requireRestart)
            builder.requireRestart()
        if (settingsEntry.tooltipText != null)
            builder.setTooltip(TranslatableText(settingsEntry.tooltipText))
        category.addEntry(builder.build())
    }

    fun addBoolEntry(category: ConfigCategory, entryBuilder: ConfigEntryBuilder, settingsEntry: SettingsEntryBoolean, requireRestart: Boolean, blocked: Boolean)
    {
        if (blocked)
        {
            category.addEntry(entryBuilder
                .startTextDescription(TranslatableText(settingsEntry.displayText)
                    .append(" = ")
                    .append(TranslatableText("text.cloth-config.boolean.value.${settingsEntry.boolValue}")))
                .build())
            return
        }

        val builder = entryBuilder
            .startBooleanToggle(
                TranslatableText(settingsEntry.displayText),
                settingsEntry.boolValue
            )
            .setDefaultValue(settingsEntry.defaultValue == true)
            .setSaveConsumer { settingsEntry.value = it }
        if (requireRestart)
            builder.requireRestart()
        if (settingsEntry.tooltipText != null)
            builder.setTooltip(TranslatableText(settingsEntry.tooltipText))
        category.addEntry(builder.build())
    }
}