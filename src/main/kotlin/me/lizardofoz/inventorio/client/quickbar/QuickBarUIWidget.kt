package me.lizardofoz.inventorio.client.quickbar

import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.util.INVENTORY_SLOT_SIZE
import me.lizardofoz.inventorio.util.QuickBarMode
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack

@Environment(EnvType.CLIENT)
class QuickBarUIWidget(val playerAddon: PlayerAddon)
{
    fun drawPhysSlots(matrices: MatrixStack, guiX: Int, guiY: Int, canvasX: Int, canvasY: Int, slotWidth: Int, canvasSizeX: Int, canvasSizeY: Int)
    {
        if (playerAddon.quickBarMode == QuickBarMode.PHYSICAL_SLOTS)
        {
            DrawableHelper.drawTexture(matrices,
                    guiX, guiY,
                    canvasX.toFloat(), canvasY.toFloat(),
                    INVENTORY_SLOT_SIZE * slotWidth, INVENTORY_SLOT_SIZE,
                    canvasSizeX, canvasSizeY)
        }
    }
}