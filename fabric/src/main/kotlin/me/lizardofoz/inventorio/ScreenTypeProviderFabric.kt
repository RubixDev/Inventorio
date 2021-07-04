package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.client.ui.InventorioScreen
import me.lizardofoz.inventorio.player.InventorioScreenHandler
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object ScreenTypeProviderFabric : ScreenTypeProvider
{
    private val handlerProvider = ScreenHandlerRegistry.registerSimple(Identifier("inventorio", "player_screen")) { syncId, inv ->
        InventorioScreenHandler(syncId, inv)
    }

    override fun getScreenHandlerType(): ScreenHandlerType<InventorioScreenHandler>
    {
        return handlerProvider
    }

    fun registerScreen()
    {
        ScreenRegistry.register(handlerProvider) { handler, inventory, text -> InventorioScreen(handler, inventory) }
    }
}