package me.lizardofoz.inventorio.client.ui

import me.lizardofoz.inventorio.mixin.client.accessor.HandledScreenAccessor
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

@Environment(EnvType.CLIENT)
object PlayerInventoryUIAddon
{
    private val BACKGROUND_TEXTURE = Identifier("inventorio", "textures/gui/player_inventory.png")

    private lateinit var playerAddon : PlayerAddon
    private lateinit var screenAccessor: HandledScreenAccessor

    fun init(inventoryScreen: InventoryScreen)
    {
        playerAddon = PlayerAddon.Client.local
        screenAccessor = inventoryScreen as HandledScreenAccessor
        screenAccessor.setBackgroundWidth(GUI_INVENTORY_TOP.width)
        screenAccessor.titleX += CRAFTING_GRID_OFFSET_X
        screenAccessor.backgroundHeight += playerAddon.getExtensionRows() * INVENTORY_SLOT_SIZE
    }

    fun postInit()
    {
        playerAddon.handlerAddon.checkCapacity()
    }

    fun drawAddon(matrices: MatrixStack)
    {
        MinecraftClient.getInstance().textureManager.bindTexture(BACKGROUND_TEXTURE)

        val screenX = screenAccessor.x
        val screenY = screenAccessor.y
        val extensionRows = playerAddon.getExtensionRows()

        //Top Part
        DrawableHelper.drawTexture(matrices,
                screenX + GUI_INVENTORY_TOP.x, screenY + GUI_INVENTORY_TOP.y,
                CANVAS_INVENTORY_TOP.x.toFloat(), CANVAS_INVENTORY_TOP.y.toFloat(),
                GUI_INVENTORY_TOP.width, GUI_INVENTORY_TOP.height,
                256, 256)

        //Main Rows
        val guiMainRect = GUI_INVENTORY_MAIN(extensionRows)
        DrawableHelper.drawTexture(matrices,
                screenX + guiMainRect.x, screenY + guiMainRect.y,
                CANVAS_INVENTORY_MAIN.x.toFloat(), CANVAS_INVENTORY_MAIN.y.toFloat(),
                guiMainRect.width, guiMainRect.height,
                256, 256)

        //Extension Rows
        if (extensionRows > 0)
        {
            val guiExtensionRect1 = GUI_INVENTORY_EXTENSION(extensionRows)
            DrawableHelper.drawTexture(matrices,
                    screenX + guiExtensionRect1.x, screenY + guiExtensionRect1.y,
                    CANVAS_INVENTORY_EXTENSION.x.toFloat(), CANVAS_INVENTORY_EXTENSION.y.toFloat(),
                    guiExtensionRect1.width, guiExtensionRect1.height,
                    256, 256)

            DrawableHelper.drawTexture(matrices,
                    screenX + GUI_UTILITY_BELT_COLUMN_2.x, screenY + GUI_UTILITY_BELT_COLUMN_2.y,
                    CANVAS_UTILITY_BELT_COLUMN_2.x.toFloat(), CANVAS_UTILITY_BELT_COLUMN_2.y.toFloat(),
                    GUI_UTILITY_BELT_COLUMN_2.width, GUI_UTILITY_BELT_COLUMN_2.height,
                    256, 256)
        }

        //Utility Belt Selection Frame
        DrawableHelper.drawTexture(matrices,
                screenX + GUI_UTILITY_BELT_FRAME_ORIGIN.x + (playerAddon.inventoryAddon.selectedUtility / 4) * INVENTORY_SLOT_SIZE,
                screenY + GUI_UTILITY_BELT_FRAME_ORIGIN.y + (playerAddon.inventoryAddon.selectedUtility % 4) * INVENTORY_SLOT_SIZE,
                CANVAS_UTILITY_BELT_FRAME.x.toFloat(), CANVAS_UTILITY_BELT_FRAME.y.toFloat(),
                CANVAS_UTILITY_BELT_FRAME.width, CANVAS_UTILITY_BELT_FRAME.height,
                256, 256)

        //Tool Belt
        DrawableHelper.drawTexture(matrices,
                screenX + GUI_TOOL_BELT.x, screenY + GUI_TOOL_BELT.y,
                CANVAS_TOOL_BELT.x.toFloat(), CANVAS_TOOL_BELT.y.toFloat(),
                GUI_TOOL_BELT.width, GUI_TOOL_BELT.height,
                256, 256)

        //ToolBelt - Empty Items
        //This isn't particularly nice, but the built-in system requires an empty slot icon to be a part of a vanilla block atlas
        for ((absolute, relative) in TOOL_BELT_RANGE.withRelativeIndex())
        {
            if (playerAddon.handlerAddon.screenHandler.getSlot(absolute).stack.isEmpty)
            {
                DrawableHelper.drawTexture(matrices,
                        screenX + SLOT_TOOL_BELT.x,
                        screenY + SLOT_TOOL_BELT.y + INVENTORY_SLOT_SIZE * relative,

                        CANVAS_TOOLS.x.toFloat(),
                        CANVAS_TOOLS.y.toFloat() + CANVAS_TOOLS.height * relative,
                        CANVAS_TOOLS.width, CANVAS_TOOLS.height,
                        256, 256)
            }
        }
    }

}