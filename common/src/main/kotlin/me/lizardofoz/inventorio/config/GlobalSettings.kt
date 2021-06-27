package me.lizardofoz.inventorio.config

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import me.lizardofoz.inventorio.client.configscreen.GlobalSettingsSyncPrompt
import me.lizardofoz.inventorio.util.DeepPocketsMode
import net.minecraft.client.MinecraftClient
import java.io.File

object GlobalSettings : AbstractSettings()
{
    @JvmField val deepPocketsInSurvival =
        SettingsEntry(
            DeepPocketsMode.ENABLED,
            "DeepPocketsEnchantment",
            "inventorio.settings.global.deep_pockets",
            "inventorio.settings.global.deep_pockets.tooltip",
            { JsonPrimitive((it as DeepPocketsMode).name) },
            { DeepPocketsMode.valueOf(it!!.asString) })

    @JvmField val expandedEnderChest = SettingsEntryBoolean(true, "ExpandedEnderChest", "inventorio.settings.global.expanded_ender_chest")
    @JvmField val infinityBowNeedsNoArrow = SettingsEntryBoolean(true, "InfinityBowNeedsNoArrow", "inventorio.settings.global.infinity_bow_no_arrows")
    @JvmField val totemFromUtilityBelt = SettingsEntryBoolean(true, "TotemFromUtilityBelt", "inventorio.settings.global.totem_from_utility_belt")
    @JvmField val allowSwappedHands = SettingsEntryBoolean(true, "AllowSwappedHands", "inventorio.settings.global.allow_swapped_hands")
    @JvmField val ignoreModdedHandlers = SettingsEntryBoolean(true, "IgnoreModdedHandlers", "inventorio.settings.global.ignore_modded_handlers", "inventorio.settings.global.ignore_modded_handlers.tooltip")

    @JvmField val integrationGravestones = SettingsEntryBoolean(true, "Integrations.Gravestones", "inventorio.settings.global.integrations.gravestones")
    @JvmField val integrationJEI = SettingsEntryBoolean(true, "Integrations.JEI", "inventorio.settings.global.integrations.jei")

    init
    {
        entries = listOf(
            deepPocketsInSurvival,
            expandedEnderChest,
            infinityBowNeedsNoArrow,
            totemFromUtilityBelt,
            allowSwappedHands,
            ignoreModdedHandlers,

            integrationGravestones,
            integrationJEI)
        load(File(".").resolve("config/inventorio_shared.json"))
    }

    fun syncFromServer(newSettingsJson: JsonObject)
    {
        if (GlobalSettings.anyChanges(newSettingsJson))
            MinecraftClient.getInstance()?.openScreen(GlobalSettingsSyncPrompt.get(newSettingsJson))
    }
}