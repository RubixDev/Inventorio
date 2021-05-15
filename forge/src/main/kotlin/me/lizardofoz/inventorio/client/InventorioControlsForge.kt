package me.lizardofoz.inventorio.client

import net.minecraft.client.MinecraftClient
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
object InventorioControlsForge : InventorioControls()
{
    init
    {
        MinecraftClient.getInstance().options.keysAll += inventorioKeys
    }
}