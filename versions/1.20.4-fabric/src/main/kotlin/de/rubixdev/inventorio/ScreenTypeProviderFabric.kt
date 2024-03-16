package de.rubixdev.inventorio

import de.rubixdev.inventorio.client.ui.InventorioScreen
import de.rubixdev.inventorio.player.InventorioScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object ScreenTypeProviderFabric : ScreenTypeProvider {
    private val handlerProvider = ScreenHandlerRegistry.registerSimple(
        Identifier("inventorio", "player_screen"),
    ) { syncId, inv -> InventorioScreenHandler(syncId, inv) }

    override fun getScreenHandlerType(): ScreenHandlerType<InventorioScreenHandler> {
        return handlerProvider
    }

    fun registerScreen() {
        HandledScreens.register(getScreenHandlerType()) { handler, inventory, _ -> InventorioScreen(handler, inventory) }
    }
}
