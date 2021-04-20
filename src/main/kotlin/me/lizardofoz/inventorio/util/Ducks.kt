package me.lizardofoz.inventorio.util

import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon

//Named after "duck typing"

interface HandlerDuck
{
    var addon: ScreenHandlerAddon
}

interface InventoryDuck
{
    val addon: PlayerInventoryAddon
}

interface PlayerDuck
{
    val addon: PlayerAddon
}