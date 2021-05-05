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