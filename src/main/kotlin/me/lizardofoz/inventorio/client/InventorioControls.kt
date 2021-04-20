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

    val keyQuickBar10 = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "inventorio.keys.quickbar_10",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_0,
            "inventorio.keys.category"
    ))!!

    val keyQuickBar11 = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "inventorio.keys.quickbar_11",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_MINUS,
            "inventorio.keys.category"
    ))!!

    val keyQuickBar12 = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "inventorio.keys.quickbar_12",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_EQUAL,
            "inventorio.keys.category"
    ))!!

    val keyUseUtility = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "inventorio.keys.use_utility",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "inventorio.keys.category"
    ))!!

    val keyNextUtility = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "inventorio.keys.next_utility",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "inventorio.keys.category"
    ))!!

    val keyPrevUtility = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "inventorio.keys.prev_utility",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_N,
            "inventorio.keys.category"
    ))!!

    val keyOpenConfig = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "inventorio.keys.open_config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "inventorio.keys.category"
    ))!!
}