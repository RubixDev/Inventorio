package me.danetnaverno.inventorio.client.quickbar

import me.danetnaverno.inventorio.*
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.util.indicesAndOffsets
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack

@Environment(EnvType.CLIENT)
class QuickBarInventoryWidget(val playerAddon: PlayerAddon)
{
    fun drawBackground(matrices: MatrixStack, guiStartX: Int, guiStartY: Int, canvasEmptyRowStartX: Int, canvasEmptyRowStartY: Int)
    {
        if (playerAddon.quickBarMode == QuickBarMode.PHYSICAL_SLOTS)
        {
            DrawableHelper.drawTexture(matrices,
                    guiStartX, guiStartY,
                    canvasEmptyRowStartX.toFloat(), canvasEmptyRowStartY.toFloat(),
                    gui_canvas_inventorySlotSize * inventorioRowLength, gui_canvas_inventorySlotSize,
                    256, 512)
        }
        else
        {
            for ((absolute, relative) in quickBarPhysicalSlotsRange.indicesAndOffsets())
            {
                if (playerAddon.player.playerScreenHandler.getSlot(absolute).stack.isNotEmpty)
                    DrawableHelper.drawTexture(matrices,
                            guiStartX + relative * gui_canvas_inventorySlotSize, guiStartY,
                            canvasEmptyRowStartX.toFloat(), canvasEmptyRowStartY.toFloat(),
                            gui_canvas_inventorySlotSize, gui_canvas_inventorySlotSize,
                            256, 512)
            }
        }
    }
}