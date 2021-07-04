package me.lizardofoz.inventorio.config

import com.google.gson.JsonPrimitive
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.util.ScrollWheelUtilityBeltMode
import me.lizardofoz.inventorio.util.SegmentedHotbar
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient

@Environment(EnvType.CLIENT)
object PlayerSettings : AbstractSettings()
{
    @JvmField
    val swappedHands = SettingsEntryBoolean(
        false,
        "SwappedHands",
        "inventorio.settings.player.swapped_hands",
        "inventorio.settings.player.swapped_hands.tooltip"
    ) { MinecraftClient.getInstance().player?.inventoryAddon?.swappedHands = it == true }

    @JvmField
    val segmentedHotbar = SettingsEntry(SegmentedHotbar.OFF,
        "SegmentedHotbar",
        "inventorio.settings.player.segmented_hotbar_mode",
        null,
        { JsonPrimitive((it as SegmentedHotbar).name) },
        { if (it != null) SegmentedHotbar.valueOf(it.asString) else SegmentedHotbar.OFF })

    @JvmField
    val scrollWheelUtilityBelt =
        SettingsEntry(ScrollWheelUtilityBeltMode.OFF,
            "ScrollWheelUtilityBelt",
            "inventorio.settings.player.scroll_wheel_utility_belt_mode",
            null,
            { JsonPrimitive((it as ScrollWheelUtilityBeltMode).name) },
            { ScrollWheelUtilityBeltMode.valueOf(it!!.asString)})

    @JvmField
    val skipEmptyUtilitySlots = SettingsEntryBoolean(true,
        "SkipEmptyUtilitySlots",
        "inventorio.settings.player.skip_empty_utility_slots")

    @JvmField
    val useItemAppliesToOffhand = SettingsEntryBoolean(false,
        "UseItemAppliesToOffhand",
        "inventorio.settings.player.use_item_applies_to_offhand",
        "inventorio.settings.player.use_item_applies_to_offhand.tooltip")

    @JvmField
    val canThrowUnloyalTrident = SettingsEntryBoolean(false,
        "CanThrowUnloyalTrident",
        "inventorio.settings.player.can_throw_unloyal_trident")

    @JvmField
    val aggressiveButtonRemoval = SettingsEntryBoolean(false,
        "AggressiveButtonRemoval",
        "inventorio.settings.player.aggressive_button_removal",
        "inventorio.settings.player.aggressive_button_removal.tooltip")

    @JvmField
    val toggleButton = SettingsEntryBoolean(true,
        "ToggleButton",
        "inventorio.settings.player.toggle_button")

    init
    {
        entries = listOf(
            segmentedHotbar,
            scrollWheelUtilityBelt,
            canThrowUnloyalTrident,
            useItemAppliesToOffhand,
            skipEmptyUtilitySlots,
            swappedHands,
            aggressiveButtonRemoval,
            toggleButton)
    }
}