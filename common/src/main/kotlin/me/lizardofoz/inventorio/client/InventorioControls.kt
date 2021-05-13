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
            "inventorio.keys.switch_segment_hotbar_mode",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "inventorio.keys.category"
    )

    val keyScrollWheelUtilityBeltMode = KeyBinding(
            "inventorio.keys.scroll_wheel_utility_belt_mode",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "inventorio.keys.category"
    )

    val inventorioKeys = arrayOf(keyUseUtility, keyNextUtility, keyPrevUtility, keySwitchSegmentedHotbarMode, keyScrollWheelUtilityBeltMode)

    abstract fun registerKeyBindings()

    companion object
    {
        lateinit var INSTANCE: InventorioControls
    }
}