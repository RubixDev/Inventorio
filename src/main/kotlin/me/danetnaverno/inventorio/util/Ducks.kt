package me.danetnaverno.inventorio.util

import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.player.PlayerInventoryAddon

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