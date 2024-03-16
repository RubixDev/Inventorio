package de.rubixdev.inventorio

import de.rubixdev.inventorio.client.control.InventorioKeyHandler
import de.rubixdev.inventorio.client.ui.HotbarHUDRenderer.renderHotbarAddons
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.RenderGuiEvent
import net.neoforged.neoforge.event.TickEvent

object NeoForgeEvents {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        InventorioKeyHandler.tick()
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun preGuiRender(event: RenderGuiEvent.Pre) {
        renderHotbarAddons(event.guiGraphics)
    }
}
