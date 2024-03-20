package de.rubixdev.inventorio

import de.rubixdev.inventorio.client.ui.InventorioScreen
import de.rubixdev.inventorio.player.InventorioScreenHandler
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandlerType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.KotlinModLoadingContext

//#if MC >= 12004
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
//#else
//$$ import net.minecraft.client.gui.screen.ingame.HandledScreens
//#endif

object ScreenTypeProviderNeoForge : ScreenTypeProvider {
    private val handlerProvider = IMenuTypeExtension.create { syncId, inv, _ ->
        InventorioScreenHandler.create(syncId, inv)
    }

    init {
        val registry = DeferredRegister.create(Registries.SCREEN_HANDLER, "inventorio")
        registry.register(KotlinModLoadingContext.get().getKEventBus())
        registry.register("player_screen") { -> handlerProvider }
    }

    override fun getScreenHandlerType(): ScreenHandlerType<InventorioScreenHandler> {
        return handlerProvider
    }

    //#if MC >= 12004
    @SubscribeEvent
    fun registerScreen(event: RegisterMenuScreensEvent) {
        event.register(handlerProvider) { handler, inventory, _ -> InventorioScreen.create(handler, inventory) }
    }
    //#else
    //$$ fun registerScreen() {
    //$$     HandledScreens.register(handlerProvider) { handler, inventory, _ -> InventorioScreen.create(handler, inventory) }
    //$$ }
    //#endif
}
