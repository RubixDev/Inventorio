package me.danetnaverno.inventorio.client.quickbar

import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.util.INVENTORY_SLOT_SIZE
import me.danetnaverno.inventorio.util.QuickBarMode
import me.danetnaverno.inventorio.util.isNotEmpty
import me.danetnaverno.inventorio.util.QUICK_BAR_PHYS_RANGE
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack

@Environment(EnvType.CLIENT)
class QuickBarInventoryWidget(val playerAddon: PlayerAddon)
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
        else
        {
            val start = QUICK_BAR_PHYS_RANGE.first
            for (relative in 0 until slotWidth)
            {
                if (playerAddon.player.playerScreenHandler.getSlot(start + relative).stack.isNotEmpty)
                    DrawableHelper.drawTexture(matrices,
                            guiX + relative * INVENTORY_SLOT_SIZE, guiY,
                            canvasX.toFloat(), canvasY.toFloat(),
                            INVENTORY_SLOT_SIZE, INVENTORY_SLOT_SIZE,
                            canvasSizeX, canvasSizeY)
            }
        }
    }
}