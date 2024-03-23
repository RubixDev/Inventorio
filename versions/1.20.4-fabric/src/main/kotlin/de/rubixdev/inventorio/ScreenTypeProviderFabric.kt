package de.rubixdev.inventorio

import de.rubixdev.inventorio.client.ui.InventorioScreen
import de.rubixdev.inventorio.player.InventorioScreenHandler
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object ScreenTypeProviderFabric : ScreenTypeProvider {
    private val handlerProvider = Registry.register(
        Registries.SCREEN_HANDLER,
        Identifier("inventorio", "player_screen"),
        ScreenHandlerType({ syncId, inv -> InventorioScreenHandler(syncId, inv) }, FeatureFlags.VANILLA_FEATURES),
    )

    override fun getScreenHandlerType(): ScreenHandlerType<InventorioScreenHandler> {
        return handlerProvider
    }

    fun registerScreen() {
        HandledScreens.register(getScreenHandlerType()) { handler, inventory, _ -> InventorioScreen(handler, inventory) }
    }
}
