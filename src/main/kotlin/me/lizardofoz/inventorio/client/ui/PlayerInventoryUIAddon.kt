package me.lizardofoz.inventorio.client.ui

import me.lizardofoz.inventorio.mixin.client.accessor.HandledScreenAccessor
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon.Companion.screenHandlerAddon
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

@Environment(EnvType.CLIENT)
object PlayerInventoryUIAddon
{
    private val BACKGROUND_TEXTURE = Identifier("inventorio", "textures/gui/player_inventory.png")

    private lateinit var inventoryAddon : PlayerInventoryAddon
    private lateinit var screenAccessor: HandledScreenAccessor

    fun init(inventoryScreen: InventoryScreen)
    {
        this.inventoryAddon = PlayerInventoryAddon.Client.local
        this.screenAccessor = inventoryScreen as HandledScreenAccessor
        this.screenAccessor.titleX += CRAFTING_GRID_OFFSET_X
        this.screenAccessor.backgroundWidth = GUI_INVENTORY_TOP.width
        this.screenAccessor.backgroundHeight += inventoryAddon.getExtensionRows() * SLOT_UI_SIZE
    }

    fun postInit()
    {
        inventoryAddon.player.screenHandlerAddon.checkDeepPocketsCapacity()
    }

    fun makeWidgetButton(inventoryScreen: InventoryScreen, recipeBook: RecipeBookWidget, narrow: Boolean): TexturedButtonWidget
    {
        val buttonYOffset = 22 + PlayerInventoryAddon.Client.local.getExtensionRows() * 10

        screenAccessor.x = recipeBook.findLeftEdge(narrow, inventoryScreen.width, screenAccessor.backgroundWidth - 19)
        return TexturedButtonWidget(screenAccessor.x + 124, inventoryScreen.height / 2 - buttonYOffset, 20, 18, 0, 0, 19, Identifier("textures/gui/recipe_button.png"))
        { buttonWidget: ButtonWidget ->
            recipeBook.reset(narrow)
            recipeBook.toggleOpen()
            screenAccessor.x = recipeBook.findLeftEdge(narrow, inventoryScreen.width, screenAccessor.backgroundWidth - 19)
            (buttonWidget as TexturedButtonWidget).setPos(screenAccessor.x + 124, inventoryScreen.height / 2 - buttonYOffset)
        }
    }

    fun drawAddon(matrices: MatrixStack)
    {
        MinecraftClient.getInstance().textureManager.bindTexture(BACKGROUND_TEXTURE)

        val screenX = screenAccessor.x
        val screenY = screenAccessor.y
        val extensionRows = inventoryAddon.getExtensionRows()

        //Top Part
        DrawableHelper.drawTexture(matrices,
                screenX + GUI_INVENTORY_TOP.x, screenY + GUI_INVENTORY_TOP.y,
                CANVAS_INVENTORY_TOP.x, CANVAS_INVENTORY_TOP.y,
                GUI_INVENTORY_TOP.width, GUI_INVENTORY_TOP.height,
                256, 256)

        //Main Rows
        val guiMainRect = GUI_INVENTORY_MAIN(extensionRows)
        DrawableHelper.drawTexture(matrices,
                screenX + guiMainRect.x, screenY + guiMainRect.y,
                CANVAS_INVENTORY_MAIN.x, CANVAS_INVENTORY_MAIN.y,
                guiMainRect.width, guiMainRect.height,
                256, 256)

        //Extension Rows
        if (extensionRows > 0)
        {
            val guiExtensionRect1 = GUI_INVENTORY_EXTENSION(extensionRows)
            DrawableHelper.drawTexture(matrices,
                    screenX + guiExtensionRect1.x, screenY + guiExtensionRect1.y,
                    CANVAS_INVENTORY_EXTENSION.x, CANVAS_INVENTORY_EXTENSION.y,
                    guiExtensionRect1.width, guiExtensionRect1.height,
                    256, 256)

            DrawableHelper.drawTexture(matrices,
                    screenX + GUI_UTILITY_BELT_COLUMN_2.x, screenY + GUI_UTILITY_BELT_COLUMN_2.y,
                    CANVAS_UTILITY_BELT_COLUMN_2.x, CANVAS_UTILITY_BELT_COLUMN_2.y,
                    GUI_UTILITY_BELT_COLUMN_2.width, GUI_UTILITY_BELT_COLUMN_2.height,
                    256, 256)
        }

        //Utility Belt Selection Frame
        DrawableHelper.drawTexture(matrices,
                screenX + GUI_UTILITY_BELT_FRAME_ORIGIN.x + (inventoryAddon.selectedUtility / 4) * SLOT_UI_SIZE,
                screenY + GUI_UTILITY_BELT_FRAME_ORIGIN.y + (inventoryAddon.selectedUtility % 4) * SLOT_UI_SIZE,
                CANVAS_UTILITY_BELT_FRAME.x.toFloat(), CANVAS_UTILITY_BELT_FRAME.y.toFloat(),
                CANVAS_UTILITY_BELT_FRAME.width, CANVAS_UTILITY_BELT_FRAME.height,
                256, 256)

        //Tool Belt
        DrawableHelper.drawTexture(matrices,
                screenX + GUI_TOOL_BELT(extensionRows).x, screenY + GUI_TOOL_BELT(extensionRows).y,
                CANVAS_TOOL_BELT.x, CANVAS_TOOL_BELT.y,
                GUI_TOOL_BELT(extensionRows).width, GUI_TOOL_BELT(extensionRows).height,
                256, 256)

        //ToolBelt - Empty Items
        //This isn't particularly nice, but the built-in system requires an empty slot icon to be a part of a vanilla block atlas
        for ((index, stack) in inventoryAddon.toolBelt.withIndex())
        {
            if (stack.isEmpty)
                DrawableHelper.drawTexture(matrices,
                        screenX + SLOT_TOOL_BELT(extensionRows).x,
                        screenY + SLOT_TOOL_BELT(extensionRows).y + SLOT_UI_SIZE * index,
                        CANVAS_TOOLS.x.toFloat(),
                        CANVAS_TOOLS.y.toFloat() + CANVAS_TOOLS.height * index,
                        CANVAS_TOOLS.width, CANVAS_TOOLS.height,
                        256, 256)
        }
    }

}