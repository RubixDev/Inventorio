package me.lizardofoz.inventorio.client.configscreen

import me.lizardofoz.inventorio.config.SettingsEntryBoolean
import me.lizardofoz.inventorio.config.PlayerSettings
import me.lizardofoz.inventorio.config.GlobalSettings
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

        category.addEntry(entryBuilder
            .startEnumSelector(
                TranslatableText("inventorio.settings.player.segmented_hotbar_mode"),
                SegmentedHotbar::class.java,
                PlayerSettings.segmentedHotbar.value as SegmentedHotbar
            )
            .setEnumNameProvider { TranslatableText("inventorio.settings.player.segmented_hotbar_mode." + it.name) }
            .setDefaultValue(SegmentedHotbar.OFF)
            .setSaveConsumer { PlayerSettings.segmentedHotbar.value = it }
            .build())

        addBoolEntry(category, entryBuilder, PlayerSettings.scrollWheelUtilityBelt)
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