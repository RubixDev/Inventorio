package de.rubixdev.inventorio

import de.rubixdev.inventorio.client.ui.InventorioScreen
import de.rubixdev.inventorio.player.InventorioScreenHandler
import de.rubixdev.inventorio.util.MOD_ID
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandlerType
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.KotlinModLoadingContext

object ScreenTypeProviderNeoForge : ScreenTypeProvider {
    private val handlerProvider = IMenuTypeExtension.create { syncId, inv, _ ->
        InventorioScreenHandler(syncId, inv)
    }

    init {
        val registry = DeferredRegister.create(Registries.SCREEN_HANDLER, MOD_ID)
        registry.register(KotlinModLoadingContext.get().getKEventBus())
        registry.register("player_screen") { -> handlerProvider }
    }

    override fun getScreenHandlerType(): ScreenHandlerType<InventorioScreenHandler> {
        return handlerProvider
    }

    @SubscribeEvent
    fun registerScreen(event: RegisterMenuScreensEvent) {
        event.register(handlerProvider) { handler, inventory, _ -> InventorioScreen(handler, inventory) }
    }
}
