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

        addEnumEntry(category, entryBuilder, PlayerSettings.segmentedHotbar, SegmentedHotbar::class.java, SegmentedHotbar.OFF)
        addEnumEntry(category, entryBuilder, PlayerSettings.scrollWheelUtilityBelt, ScrollWheelUtilityBeltMode::class.java, ScrollWheelUtilityBeltMode.OFF)

        addBoolEntry(category, entryBuilder, PlayerSettings.canThrowUnloyalTrident)
        addBoolEntry(category, entryBuilder, PlayerSettings.useItemAppliesToOffhand)
        addBoolEntry(category, entryBuilder, PlayerSettings.skipEmptyUtilitySlots)

        if (GlobalSettings.allowSwappedHands.boolValue)
            addBoolEntry(category, entryBuilder, PlayerSettings.swappedHands)
        else
            category.addEntry(
                entryBuilder
                    .startTextDescription(TranslatableText("inventorio.settings.player.swapped_hands.disabled"))
                    .setTooltip(TranslatableText("inventorio.settings.player.swapped_hands.disabled.tooltip"))
                    .build()
            )

        return builder.build()
    }

    private fun <T : Enum<*>> addEnumEntry(category: ConfigCategory, entryBuilder: ConfigEntryBuilder, settingsEntry: SettingsEntry, enumClass: Class<T>, defaultValue: T)
    {
        val builder = entryBuilder
            .startEnumSelector(
                TranslatableText(settingsEntry.displayText),
                enumClass,
                settingsEntry.value as T
            )
            .setEnumNameProvider { TranslatableText("${settingsEntry.displayText}.${it.name}") }
            .setDefaultValue(defaultValue)
            .setSaveConsumer { settingsEntry.value = it }
        if (settingsEntry.tooltipText != null)
            builder.setTooltip(TranslatableText(settingsEntry.tooltipText))
        category.addEntry(builder.build())
    }

    private fun addBoolEntry(category: ConfigCategory, entryBuilder: ConfigEntryBuilder, settingsEntry: SettingsEntryBoolean)
    {
        val builder = entryBuilder
            .startBooleanToggle(
                TranslatableText(settingsEntry.displayText),
                settingsEntry.boolValue
            )
            .setDefaultValue(settingsEntry.defaultValue == true)
            .setSaveConsumer { settingsEntry.value = it }
        if (settingsEntry.tooltipText != null)
            builder.setTooltip(TranslatableText(settingsEntry.tooltipText))
        category.addEntry(builder.build())
    }
}