package me.lizardofoz.inventorio.client

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper

@Environment(EnvType.CLIENT)
object InventorioControlsFabric : InventorioControls()
{
    init
    {
        inventorioKeys.forEach { KeyBindingHelper.registerKeyBinding(it) }
    }
}