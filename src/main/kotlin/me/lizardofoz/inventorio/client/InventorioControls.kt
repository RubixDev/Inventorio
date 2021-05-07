package me.lizardofoz.inventorio.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
object InventorioControls
{
    fun initialize()  //Hallo, this method exists just to load the class
    {
    }

    val keyUseUtility = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "inventorio.keys.use_utility",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "inventorio.keys.category"
    ))!!

    val keyNextUtility = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "inventorio.keys.next_utility",
            InputUtil.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_4,
            "inventorio.keys.category"
    ))!!

    val keyPrevUtility = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "inventorio.keys.prev_utility",
            InputUtil.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_5,
            "inventorio.keys.category"
    ))!!

    val keyScrollSimplifiedMode = KeyBindingHelper.registerKeyBinding(KeyBinding(
        "inventorio.keys.scroll_simplified_hotbar",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_I,
        "inventorio.keys.category"
    ))!!
}