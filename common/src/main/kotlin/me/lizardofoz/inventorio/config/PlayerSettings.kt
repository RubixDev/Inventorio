package me.lizardofoz.inventorio.config

import com.google.gson.JsonPrimitive
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.util.SegmentedHotbar
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.text.TranslatableText

@Environment(EnvType.CLIENT)
object PlayerSettings : AbstractSettings()
{
    @JvmField
    val segmentedHotbar = SettingsEntry(SegmentedHotbar.OFF,
        "SegmentedHotbar",
        TranslatableText("inventorio.settings.player.segmented_hotbar_mode"),
        null,
        { JsonPrimitive((it as SegmentedHotbar).name) },
        { if (it != null) SegmentedHotbar.valueOf(it.asString) else SegmentedHotbar.OFF })

    @JvmField
    val canThrowUnloyalTrident = SettingsEntryBoolean(false,
        "CanThrowUnloyalTrident",
        TranslatableText("inventorio.settings.player.can_throw_unloyal_trident"))

    @JvmField
    val swappedHands = SettingsEntryBoolean(
        false,
        "SwappedHands",
        TranslatableText("inventorio.settings.player.swapped_hands"),
        TranslatableText("inventorio.settings.player.swapped_hands.tooltip")
    ) { MinecraftClient.getInstance().player?.inventoryAddon?.swappedHands = it == true }

    @JvmField
    val scrollWheelUtilityBelt = SettingsEntryBoolean(false,
        "ScrollWheelUtilityBelt",
        TranslatableText("inventorio.settings.player.scroll_wheel_utility_belt_mode"))

    @JvmField
    val useItemAppliesToOffhand = SettingsEntryBoolean(false,
        "UseItemAppliesToOffhand",
        TranslatableText("inventorio.settings.player.use_item_applies_to_offhand"),
        TranslatableText("inventorio.settings.player.use_item_applies_to_offhand.tooltip"))

    init
    {
        entries = listOf(segmentedHotbar, scrollWheelUtilityBelt, canThrowUnloyalTrident, useItemAppliesToOffhand, swappedHands)
    }
}