package de.rubixdev.inventorio

import de.rubixdev.inventorio.client.ui.InventorioScreen
import de.rubixdev.inventorio.player.InventorioScreenHandler
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.screen.ScreenHandlerType
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.KotlinModLoadingContext

object ScreenTypeProviderForge : ScreenTypeProvider {
    private val handlerProvider = IForgeMenuType.create { syncId, inv, _ ->
        InventorioScreenHandler(syncId, inv)
    }

    init
    {
        val registry = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "inventorio")
        registry.register(KotlinModLoadingContext.get().getKEventBus())
        registry.register("player_screen") { handlerProvider }
    }
    override fun getScreenHandlerType(): ScreenHandlerType<InventorioScreenHandler> {
        return handlerProvider
    }

    fun registerScreen() {
        HandledScreens.register(handlerProvider) { handler, inventory, _ -> InventorioScreen(handler, inventory) }
    }
}
