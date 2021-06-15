package me.lizardofoz.inventorio.client.configscreen

import me.lizardofoz.inventorio.config.SettingsEntryBoolean
import me.lizardofoz.inventorio.config.GlobalSettings
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.TranslatableText

@Environment(EnvType.CLIENT)
object GlobalSettingsScreen
{
    fun get(parent: Screen?): Screen
    {
        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setSavingRunnable(GlobalSettings::save)
            .setTitle(TranslatableText("inventorio.settings.global.title"))
        val entryBuilder = builder.entryBuilder()
        val category = builder.getOrCreateCategory(TranslatableText("inventorio.settings.global.title"))

        if (MinecraftClient.getInstance().networkHandler?.connection?.isLocal != true)
        {
            category.addEntry(entryBuilder.startTextDescription(TranslatableText("inventorio.settings.global.disabled_by_server")).build())
            return builder.build()
        }

        category.addEntry(entryBuilder.startTextDescription(TranslatableText("inventorio.settings.global.description")).build())

        addBoolEntry(category, entryBuilder, GlobalSettings.expandedEnderChest)
        addBoolEntry(category, entryBuilder, GlobalSettings.infinityBowNeedsNoArrow)
        addBoolEntry(category, entryBuilder, GlobalSettings.totemFromUtilityBelt)
        addBoolEntry(category, entryBuilder, GlobalSettings.allowSwappedHands)

        category.addEntry(entryBuilder.startTextDescription(TranslatableText("inventorio.settings.global.integrations")).build())
        addBoolEntry(category, entryBuilder, GlobalSettings.integrationGravestones)
        addBoolEntry(category, entryBuilder, GlobalSettings.integrationJEI)

        return builder.build()
    }

    private fun addBoolEntry(category: ConfigCategory, entryBuilder: ConfigEntryBuilder, settingsEntry: SettingsEntryBoolean)
    {
        val builder = entryBuilder
            .startBooleanToggle(
                TranslatableText(settingsEntry.displayText),
                settingsEntry.boolValue
            )
            .requireRestart()
            .setDefaultValue(settingsEntry.defaultValue == true)
            .setSaveConsumer { settingsEntry.value = it }
        if (settingsEntry.tooltipText != null)
            builder.setTooltip(TranslatableText(settingsEntry.tooltipText))
        category.addEntry(builder.build())
    }
}
