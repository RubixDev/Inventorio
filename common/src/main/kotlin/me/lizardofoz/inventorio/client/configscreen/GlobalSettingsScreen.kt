package me.lizardofoz.inventorio.client.configscreen

import me.lizardofoz.inventorio.client.configscreen.PlayerSettingsScreen.addBoolEntry
import me.lizardofoz.inventorio.client.configscreen.PlayerSettingsScreen.addEnumEntry
import me.lizardofoz.inventorio.config.GlobalSettings
import me.lizardofoz.inventorio.util.ToolBeltMode
import me.shedaniel.clothconfig2.api.ConfigBuilder
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

        category.addEntry(entryBuilder.startTextDescription(TranslatableText("inventorio.settings.global.description")).build())

        val isNotLocal = MinecraftClient.getInstance().networkHandler?.connection?.isLocal != true
        if (isNotLocal)
            category.addEntry(entryBuilder.startTextDescription(TranslatableText("inventorio.settings.global.disabled_by_server")).build())

        addBoolEntry(category, entryBuilder, GlobalSettings.expandedEnderChest, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.infinityBowNeedsNoArrow, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.totemFromUtilityBelt, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.allowSwappedHands, true, isNotLocal)

        addEnumEntry(category, entryBuilder, GlobalSettings.toolBeltMode, true, isNotLocal, ToolBeltMode::class.java, ToolBeltMode.ENABLED)
        addBoolEntry(category, entryBuilder, GlobalSettings.utilityBeltShortDefaultSize, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.deepPocketsInTreasures, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.deepPocketsInTrades, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.deepPocketsInRandomSelection, true, isNotLocal)

        return builder.build()
    }
}
