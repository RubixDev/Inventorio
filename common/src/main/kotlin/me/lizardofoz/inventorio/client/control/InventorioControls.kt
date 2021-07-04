package me.lizardofoz.inventorio.client.control

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
object InventorioControls
{
    @JvmField val keyUseUtility = KeyCoBinding(
        "inventorio.keys.combined.with_use_item",
        {MinecraftClient.getInstance()?.options?.keyUse},
        "inventorio.keys.use_utility",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "inventorio.keys.category"
    )

    @JvmField val keyNextUtility = KeyBinding(
        "inventorio.keys.next_utility",
        InputUtil.Type.MOUSE,
        GLFW.GLFW_MOUSE_BUTTON_4,
        "inventorio.keys.category"
    )

    @JvmField val keyPrevUtility = KeyBinding(
        "inventorio.keys.prev_utility",
        InputUtil.Type.MOUSE,
        GLFW.GLFW_MOUSE_BUTTON_5,
        "inventorio.keys.category"
    )

    @JvmField val keyEmptyUtility = KeyBinding(
        "inventorio.keys.empty_utility",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "inventorio.keys.category"
    )

    @JvmField val keyFireBoostRocket = KeyCoBinding(
        "inventorio.keys.combined.with_jump",
        { MinecraftClient.getInstance()?.options?.keyJump },
        "inventorio.keys.rocket_boost",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_UNKNOWN,
        "inventorio.keys.category"
    )

    @JvmField val keyOpenPlayerSettingsMenu = KeyBinding(
        "inventorio.keys.open_player_settings",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_I,
        "inventorio.keys.category"
    )

    @JvmField val keyOpenGlobalSettingsMenu = KeyBinding(
        "inventorio.keys.open_global_settings",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_O,
        "inventorio.keys.category"
    )

    @JvmField val keys = arrayOf(
        keyUseUtility,
        keyNextUtility,
        keyPrevUtility,
        keyEmptyUtility,
        keyFireBoostRocket,

        keyOpenPlayerSettingsMenu,
        keyOpenGlobalSettingsMenu
    )
}