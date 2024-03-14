package me.lizardofoz.inventorio.client.configscreen

import com.google.gson.JsonObject
import me.lizardofoz.inventorio.config.GlobalSettings
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@Environment(EnvType.CLIENT)
object GlobalSettingsSyncPrompt {
    private var exitFlag = false

    fun get(newSettingsJson: JsonObject): Screen {
        exitFlag = false
        val builder = ConfigBuilder.create()
            .setParentScreen(getLoopbackScreen(newSettingsJson))
            .setTitle(Text.translatable("inventorio.settings.global.title"))
        val entryBuilder = builder.entryBuilder()
        val category = builder.getOrCreateCategory(Text.translatable("inventorio.settings.global.title"))

        val appended = Text.literal("")
        for (option in GlobalSettings.entries) {
            val newValue = option.tryElementAsValue(newSettingsJson.get(option.configKey))
            if (option.value == newValue) {
                continue
            }
            val crossedValueText = Text.translatable(option.value.toString())
            crossedValueText.style = Style.EMPTY.withFormatting(Formatting.STRIKETHROUGH)
            appended.append(option.displayText).append(": ").append(crossedValueText).append(" -> $newValue\n")
        }
        category.addEntry(entryBuilder.startTextDescription(Text.translatable("inventorio.settings.global.sync_restart_prompt", appended)).build())

        category.addEntry(
            entryBuilder
                .startEnumSelector(
                    Text.translatable("inventorio.settings.global.sync_restart_toggle"),
                    ExitMode::class.java,
                    ExitMode.PLACEHOLDER,
                )
                .setEnumNameProvider { Text.translatable("inventorio.settings.global.sync_restart_toggle." + it.name) }
                .setSaveConsumer {
                    exitFlag = true
                    if (it != ExitMode.DO_NOTHING) {
                        GlobalSettings.fromJson(newSettingsJson)
                        GlobalSettings.save()
                    }
                    if (it == ExitMode.SYNC_AND_EXIT) {
                        MinecraftClient.getInstance().stop()
                    } else {
                        MinecraftClient.getInstance().setScreen(null)
                    }
                }
                .build(),
        )
        category.addEntry(entryBuilder.startTextDescription(Text.translatable("inventorio.settings.global.sync_restart_prompt.hint")).build())

        return builder.build()
    }

    private fun getLoopbackScreen(newSettingsJson: JsonObject): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(null)
            .setAfterInitConsumer { MinecraftClient.getInstance().setScreen(if (!exitFlag) get(newSettingsJson) else null) }
            .setTitle(Text.translatable("inventorio.settings.global.title"))
        builder.getOrCreateCategory(Text.translatable("inventorio.settings.global.title"))
        return builder.build()
    }

    private enum class ExitMode {
        SYNC_AND_EXIT,
        SYNC_WITHOUT_EXIT,
        DO_NOTHING,
        PLACEHOLDER,
    }
}
