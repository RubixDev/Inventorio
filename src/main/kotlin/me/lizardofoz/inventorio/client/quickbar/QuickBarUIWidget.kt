package me.lizardofoz.inventorio.client.quickbar

import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.slot.QuickBarSlot
import me.lizardofoz.inventorio.util.INVENTORY_SLOT_SIZE
import me.lizardofoz.inventorio.util.QUICK_BAR_RANGE
import me.lizardofoz.inventorio.util.QuickBarMode
import me.lizardofoz.inventorio.util.isNotEmpty
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
        else
        {
            val start = QUICK_BAR_RANGE.first
            for (relative in 0 until slotWidth)
            {
                val slot = playerAddon.player.playerScreenHandler.getSlot(start + relative) as QuickBarSlot
                if (slot.getPhysicalStack().isNotEmpty)
                    DrawableHelper.drawTexture(matrices,
                            guiX + relative * INVENTORY_SLOT_SIZE, guiY,
                            canvasX.toFloat(), canvasY.toFloat(),
                            INVENTORY_SLOT_SIZE, INVENTORY_SLOT_SIZE,
                            canvasSizeX, canvasSizeY)
            }
        }
    }
}