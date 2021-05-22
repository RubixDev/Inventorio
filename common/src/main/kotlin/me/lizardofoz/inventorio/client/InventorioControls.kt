package me.lizardofoz.inventorio.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
object InventorioControls
{
    val keyUseUtility = KeyCoBinding(
        "inventorio.keys.combined.with_use_item",
        {MinecraftClient.getInstance()?.options?.keyUse},
        "inventorio.keys.use_utility",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "inventorio.keys.category"
    )

    val keyNextUtility = KeyBinding(
        "inventorio.keys.next_utility",
        InputUtil.Type.MOUSE,
        GLFW.GLFW_MOUSE_BUTTON_4,
        "inventorio.keys.category"
    )

    val keyPrevUtility = KeyBinding(
        "inventorio.keys.prev_utility",
        InputUtil.Type.MOUSE,
        GLFW.GLFW_MOUSE_BUTTON_5,
        "inventorio.keys.category"
    )

    val keyFireBoostRocket = KeyCoBinding(
        "inventorio.keys.combined.with_jump",
        { MinecraftClient.getInstance()?.options?.keyJump },
        "inventorio.keys.rocket_boost",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "inventorio.keys.category"
    )

    val keyOptionToggleSegmentedHotbar = KeyBinding(
        "inventorio.keys.option_toggle_segmented_hotbar_mode",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "inventorio.keys.category"
    )

    val keyOptionToggleScrollWheelUtilityBelt = KeyBinding(
        "inventorio.keys.option_toggle_scroll_wheel_utility_belt",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "inventorio.keys.category"
    )

    val keyOptionToggleCanThrowUnloyalTrident = KeyBinding(
        "inventorio.keys.option_toggle_can_throw_unloyal_trident",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "inventorio.keys.category"
    )

    val keyOptionToggleUseItemAppliesToOffhand = KeyBinding(
        "inventorio.keys.option_toggle_use_item_applies_to_offhand",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "inventorio.keys.category"
    )

    val keyOpenSettingsMenu = KeyBinding(
        "inventorio.keys.open_settings",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_I,
        "inventorio.keys.category"
    )

    val functionalKeys = arrayOf(
        keyUseUtility,
        keyNextUtility,
        keyPrevUtility,
        keyFireBoostRocket
    )

    val optionToggleKeys = arrayOf(
        keyOptionToggleSegmentedHotbar,
        keyOptionToggleCanThrowUnloyalTrident,
        keyOptionToggleScrollWheelUtilityBelt,
        keyOptionToggleUseItemAppliesToOffhand
    )
    var optionToggleKeysEnabled = false
}