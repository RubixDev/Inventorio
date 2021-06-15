package me.lizardofoz.inventorio.config

import com.google.gson.JsonObject
import me.lizardofoz.inventorio.client.configscreen.GlobalSettingsSyncPrompt
import net.minecraft.client.MinecraftClient
import net.minecraft.text.TranslatableText
import java.io.File

object GlobalSettings : AbstractSettings()
{
    @JvmField val expandedEnderChest = SettingsEntryBoolean(true, "ExpandedEnderChest", "inventorio.settings.global.expanded_ender_chest")
    @JvmField val infinityBowNeedsNoArrow = SettingsEntryBoolean(true, "InfinityBowNeedsNoArrow", "inventorio.settings.global.infinity_bow_no_arrows")
    @JvmField val totemFromUtilityBelt = SettingsEntryBoolean(true, "TotemFromUtilityBelt", "inventorio.settings.global.totem_from_utility_belt")
    @JvmField val allowSwappedHands = SettingsEntryBoolean(true, "AllowSwappedHands", "inventorio.settings.global.allow_swapped_hands")
    @JvmField val integrationGravestones = SettingsEntryBoolean(true, "Integrations.Gravestones", "inventorio.settings.global.integrations.gravestones")
    @JvmField val integrationJEI = SettingsEntryBoolean(true, "Integrations.JEI", "inventorio.settings.global.integrations.jei")

    init
    {
        entries = listOf(expandedEnderChest, infinityBowNeedsNoArrow, totemFromUtilityBelt, allowSwappedHands, integrationGravestones, integrationJEI)
        load(File(".").resolve("config/inventorio_shared.json"))
    }

    fun syncFromServer(newSettingsJson: JsonObject)
    {
        if (GlobalSettings.anyChanges(newSettingsJson))
            MinecraftClient.getInstance()?.openScreen(GlobalSettingsSyncPrompt.get(newSettingsJson))
    }
}