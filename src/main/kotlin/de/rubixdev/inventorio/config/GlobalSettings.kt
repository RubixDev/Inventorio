package de.rubixdev.inventorio.config

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import de.rubixdev.inventorio.client.configscreen.GlobalSettingsSyncPrompt
import de.rubixdev.inventorio.util.ToolBeltMode
import java.io.File
import net.minecraft.client.MinecraftClient

object GlobalSettings : AbstractSettings() {
    @JvmField val expandedEnderChest = SettingsEntryBoolean(
        true,
        "ExpandedEnderChest",
        "inventorio.settings.global.expanded_ender_chest",
    )
    @JvmField val infinityBowNeedsNoArrow = SettingsEntryBoolean(
        true,
        "InfinityBowNeedsNoArrow",
        "inventorio.settings.global.infinity_bow_no_arrows",
    )
    @JvmField val totemFromUtilityBelt = SettingsEntryBoolean(
        true,
        "TotemFromUtilityBelt",
        "inventorio.settings.global.totem_from_utility_belt",
    )
    @JvmField val allowSwappedHands = SettingsEntryBoolean(
        true,
        "AllowSwappedHands",
        "inventorio.settings.global.allow_swapped_hands",
    )
    @JvmField val allow2x2CraftingGrid = SettingsEntryBoolean(
        true,
        "2x2CraftingGrid",
        "inventorio.settings.global.2x2_crafting_grid",
        "inventorio.settings.global.2x2_crafting_grid.tooltip",
    )

    @JvmField val toolBeltMode =
        SettingsEntry(
            ToolBeltMode.ENABLED,
            "ToolBeltMode",
            "inventorio.settings.global.tool_belt_mode",
            "inventorio.settings.global.tool_belt_mode.tooltip",
            { JsonPrimitive((it as ToolBeltMode).name) },
            { ToolBeltMode.valueOf(it!!.asString) },
        )
    @JvmField val utilityBeltShortDefaultSize = SettingsEntryBoolean(
        true,
        "UtilityBeltShortDefaultSize",
        "inventorio.settings.global.utility_belt_short_default_size",
        "inventorio.settings.global.utility_belt_short_default_size.tooltip",
    )
    @JvmField val deepPocketsBookCraft = SettingsEntryBoolean(
        true,
        "DeepPocketsBookCraft",
        "inventorio.settings.global.deep_pockets_book_craft",
    )
    @JvmField val deepPocketsInTrades = SettingsEntryBoolean(
        true,
        "DeepPocketsInTrades",
        "inventorio.settings.global.deep_pockets_in_trades",
    )
    @JvmField val deepPocketsInRandomSelection = SettingsEntryBoolean(
        true,
        "DeepPocketsInRandomSelection",
        "inventorio.settings.global.deep_pockets_in_random_selection",
    )

    init {
        entries = listOf(
            expandedEnderChest,
            infinityBowNeedsNoArrow,
            totemFromUtilityBelt,
            allowSwappedHands,
            allow2x2CraftingGrid,

            toolBeltMode,
            utilityBeltShortDefaultSize,
            deepPocketsBookCraft,
            deepPocketsInTrades,
            deepPocketsInRandomSelection,
        )
        load(File(".").resolve("config/inventorio_shared.json"))
    }

    fun syncFromServer(newSettingsJson: JsonObject) {
        if (GlobalSettings.anyChanges(newSettingsJson)) {
            MinecraftClient.getInstance()?.setScreen(GlobalSettingsSyncPrompt.get(newSettingsJson))
        }
    }
}
