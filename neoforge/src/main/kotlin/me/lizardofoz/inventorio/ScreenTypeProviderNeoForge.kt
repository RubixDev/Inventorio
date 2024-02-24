package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.client.ui.InventorioScreen
import me.lizardofoz.inventorio.player.InventorioScreenHandler
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandlerType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.KotlinModLoadingContext

object ScreenTypeProviderNeoForge : ScreenTypeProvider
{
    private val handlerProvider = IMenuTypeExtension.create { syncId, inv, _ ->
        InventorioScreenHandler(syncId, inv)
    }

    init
    {
        val registry = DeferredRegister.create(Registries.SCREEN_HANDLER, "inventorio")
        registry.register(KotlinModLoadingContext.get().getKEventBus())
        registry.register("player_screen") { -> handlerProvider }
    }
    override fun getScreenHandlerType(): ScreenHandlerType<InventorioScreenHandler>
    {
        return handlerProvider
    }

    fun registerScreen()
    {
        HandledScreens.register(handlerProvider) { handler, inventory, _ -> InventorioScreen(handler, inventory) }
    }
}