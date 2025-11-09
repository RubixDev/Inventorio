package de.rubixdev.inventorio

import de.rubixdev.inventorio.client.control.InventorioKeyHandler
import de.rubixdev.inventorio.client.ui.HotbarHUDRenderer.renderHotbarAddons
import net.minecraft.util.Identifier
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent

@OnlyIn(Dist.CLIENT)
object NeoForgeEvents {
    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        InventorioKeyHandler.tick()
    }
}

@OnlyIn(Dist.CLIENT)
object NeoForgeModEvents {
    @SubscribeEvent
    fun preGuiRender(event: RegisterGuiLayersEvent) {
        event.registerBelowAll(Identifier("inventorio", "hotbar_addons")) { guiGraphics, _ ->
            renderHotbarAddons(guiGraphics)
        }
    }
}
