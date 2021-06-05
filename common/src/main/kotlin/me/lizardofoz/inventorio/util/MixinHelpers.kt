package me.lizardofoz.inventorio.util

import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon.Companion.screenHandlerAddon
import net.minecraft.entity.player.PlayerEntity
import java.util.function.Consumer

object MixinHelpers
{
    @JvmStatic
    @JvmOverloads
    fun withInventoryAddon(playerEntity: PlayerEntity?, consumer: Consumer<PlayerInventoryAddon>, ifNotPresent: Consumer<PlayerEntity?> = Consumer { })
    {
        val addon = playerEntity?.inventoryAddon
        if (addon != null)
            consumer.accept(addon)
        else
            ifNotPresent.accept(playerEntity)
    }

    @JvmStatic
    @JvmOverloads
    fun withScreenHandlerAddon(playerEntity: PlayerEntity?, consumer: Consumer<PlayerScreenHandlerAddon>, ifNotPresent: Consumer<PlayerEntity?> = Consumer { })
    {
        val addon = playerEntity?.screenHandlerAddon
        if (addon != null)
            consumer.accept(addon)
        else
            ifNotPresent.accept(playerEntity)
    }
}