package me.lizardofoz.inventorio.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
abstract class InventorioControls
{
    val keyUseUtility = KeyBinding(
            "inventorio.keys.use_utility",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
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

    val keySwitchSegmentedHotbarMode = KeyBinding(
            "inventorio.keys.settings_segmented_hotbar_mode",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "inventorio.keys.category"
    )

    val keyScrollWheelUtilityBeltMode = KeyBinding(
        "inventorio.keys.settings_scroll_wheel_utility_belt",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_O,
        "inventorio.keys.category"
    )

    val keySwitchJumpToRocketBoostMode = KeyBinding(
        "inventorio.keys.settings_jump_to_rocket_boost",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_P,
        "inventorio.keys.category"
    )

    val keySwitchCanThrowUnloyalTrident = KeyBinding(
        "inventorio.keys.settings_can_throw_unloyal_trident",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_LEFT_BRACKET,
        "inventorio.keys.category"
    )

    val keyOpenSettings = KeyBinding(
        "inventorio.keys.open_settings",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_RIGHT_BRACKET,
        "inventorio.keys.category"
    )

    val functionalKeys = arrayOf(keyUseUtility, keyNextUtility, keyPrevUtility)
    val settingsKeys = arrayOf(keySwitchSegmentedHotbarMode, keySwitchJumpToRocketBoostMode, keySwitchCanThrowUnloyalTrident, keyScrollWheelUtilityBeltMode)
    var settingsKeysEnabled = false
        internal set

    companion object
    {
        lateinit var INSTANCE: InventorioControls
    }
}