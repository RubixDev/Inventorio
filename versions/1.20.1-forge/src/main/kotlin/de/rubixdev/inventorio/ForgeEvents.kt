package de.rubixdev.inventorio

import de.rubixdev.inventorio.client.control.InventorioKeyHandler
import de.rubixdev.inventorio.client.ui.HotbarHUDRenderer.renderHotbarAddons
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

@OnlyIn(Dist.CLIENT)
object ForgeEvents {
    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        InventorioKeyHandler.tick()
    }
}

@OnlyIn(Dist.CLIENT)
object ForgeModEvents {
    @SubscribeEvent
    fun onGuiRender(event: RegisterGuiOverlaysEvent) {
        event.registerBelowAll("inventorio_hotbar_addons") { _, guiGraphics, _, _, _ ->
            renderHotbarAddons(guiGraphics)
        }
    }
}
