package me.lizardofoz.inventorio

import me.lizardofoz.inventorio.client.InventorioKeyHandler
import me.lizardofoz.inventorio.client.ui.HotbarHUDRenderer.renderHotbarAddons
import net.minecraft.client.MinecraftClient
import net.minecraft.world.GameMode
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

object ForgeEvents
{
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun onClientTick(event: TickEvent.ClientTickEvent)
    {
        InventorioKeyHandler.tick()
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    fun onClientTick(event: RenderGameOverlayEvent.Post)
    {
        if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR &&
                MinecraftClient.getInstance().interactionManager?.currentGameMode != GameMode.SPECTATOR)
            renderHotbarAddons(event.matrixStack)
    }
}