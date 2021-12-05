package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.client.ui.InventorioScreen
import me.lizardofoz.inventorio.player.InventorioScreenHandler
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.screen.ScreenHandlerType
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.KotlinModLoadingContext

object ScreenTypeProviderForge : ScreenTypeProvider
{
    private val handlerProvider = IForgeContainerType.create { syncId, inv, buf ->
        InventorioScreenHandler(syncId, inv)
    }

    init
    {
        val registry = DeferredRegister.create(ForgeRegistries.CONTAINERS, "inventorio")
        registry.register(KotlinModLoadingContext.get().getKEventBus())
        registry.register("player_screen") { handlerProvider }
    }
    override fun getScreenHandlerType(): ScreenHandlerType<InventorioScreenHandler>
    {
        return handlerProvider
    }

    fun registerScreen()
    {
        HandledScreens.register(handlerProvider) { handler, inventory, text -> InventorioScreen(handler, inventory) }
    }
}