package de.rubixdev.inventorio

import de.rubixdev.inventorio.client.control.InventorioKeyHandler
import de.rubixdev.inventorio.client.ui.HotbarHUDRenderer.renderHotbarAddons
import net.minecraft.util.Identifier
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent
import net.neoforged.neoforge.event.TickEvent

@OnlyIn(Dist.CLIENT)
object NeoForgeEvents {
    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        InventorioKeyHandler.tick()
    }
}

@OnlyIn(Dist.CLIENT)
object NeoForgeModEvents {
    @SubscribeEvent
    fun preGuiRender(event: RegisterGuiOverlaysEvent) {
        event.registerBelowAll(Identifier("inventorio", "hotbar_addons")) { _, guiGraphics, _, _, _ ->
            renderHotbarAddons(guiGraphics)
        }
    }
}
