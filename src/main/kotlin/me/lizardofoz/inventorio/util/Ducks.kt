package me.lizardofoz.inventorio.util

import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon

//Named after "duck typing"

interface ScreenHandlerDuck
{
    var addon: PlayerScreenHandlerAddon
}

interface InventoryDuck
{
    val addon: PlayerInventoryAddon
}

interface PlayerDuck
{
    val addon: PlayerAddon
}