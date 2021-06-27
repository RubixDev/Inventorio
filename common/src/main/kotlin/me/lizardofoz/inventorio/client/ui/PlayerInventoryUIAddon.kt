package me.lizardofoz.inventorio.client.ui

import me.lizardofoz.inventorio.mixin.client.accessor.HandledScreenAccessor
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon.Companion.screenHandlerAddon
import me.lizardofoz.inventorio.slot.ToolBeltSlot
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

    private lateinit var inventoryAddon: PlayerInventoryAddon
    private lateinit var screenAccessor: HandledScreenAccessor

    fun init(inventoryScreen: InventoryScreen)
    {
        inventoryAddon = PlayerInventoryAddon.Client.local!!
        screenAccessor = inventoryScreen as HandledScreenAccessor
        onRefresh()
    }

    fun postInit()
    {
        inventoryAddon.player.screenHandlerAddon?.updateDeepPocketsCapacity()
    }

    fun onRefresh()
    {
        if (!this::screenAccessor.isInitialized)
            return
        screenAccessor.titleX = INVENTORY_TITLE_X + CRAFTING_GRID_OFFSET_X
        screenAccessor.backgroundWidth = GUI_INVENTORY_TOP.width + ((inventoryAddon.toolBelt.size - 1) / ToolBeltSlot.getColumnCapacity(inventoryAddon.getDeepPocketsRowCount())) * 20
        screenAccessor.backgroundHeight = INVENTORY_HEIGHT + inventoryAddon.getDeepPocketsRowCount() * SLOT_UI_SIZE
    }

    fun makeWidgetButton(inventoryScreen: InventoryScreen, recipeBook: RecipeBookWidget, narrow: Boolean): TexturedButtonWidget
    {
        val buttonYOffset = GUI_RECIPE_WIDGET_BUTTON_OFFSET.y + (PlayerInventoryAddon.Client.local?.getDeepPocketsRowCount() ?: 0) * 10

        screenAccessor.x = recipeBook.findLeftEdge(narrow, inventoryScreen.width, screenAccessor.backgroundWidth - 19 - 19 * ((inventoryAddon.toolBelt.size - 1) / ToolBeltSlot.getColumnCapacity(inventoryAddon.getDeepPocketsRowCount())))
        return TexturedButtonWidget(
            screenAccessor.x + GUI_RECIPE_WIDGET_BUTTON_OFFSET.x,
            inventoryScreen.height / 2 - buttonYOffset,
            CANVAS_RECIPE_WIDGET_BUTTON.width, CANVAS_RECIPE_WIDGET_BUTTON.height,
            CANVAS_RECIPE_WIDGET_BUTTON.x, CANVAS_RECIPE_WIDGET_BUTTON.y,
            GUI_RECIPE_WIDGET_BUTTON_TOOLTIP_OFFSET,
            Identifier("textures/gui/recipe_button.png"))
        { buttonWidget: ButtonWidget ->
            recipeBook.reset(narrow)
            recipeBook.toggleOpen()
            screenAccessor.x = recipeBook.findLeftEdge(narrow, inventoryScreen.width, screenAccessor.backgroundWidth - 19 - 19 * ((inventoryAddon.toolBelt.size - 1) / ToolBeltSlot.getColumnCapacity(inventoryAddon.getDeepPocketsRowCount())))
            (buttonWidget as TexturedButtonWidget).setPos(screenAccessor.x + GUI_RECIPE_WIDGET_BUTTON_OFFSET.x, inventoryScreen.height / 2 - buttonYOffset)
        }
    }

    fun drawAddon(matrices: MatrixStack)
    {
        val textureManager = MinecraftClient.getInstance().textureManager
        textureManager.bindTexture(BACKGROUND_TEXTURE)

        val screenX = screenAccessor.x
        val screenY = screenAccessor.y
        val deepPocketsRowCount = inventoryAddon.getDeepPocketsRowCount()

        //Top Part
        DrawableHelper.drawTexture(matrices,
            screenX + GUI_INVENTORY_TOP.x, screenY + GUI_INVENTORY_TOP.y,
            CANVAS_INVENTORY_TOP.x, CANVAS_INVENTORY_TOP.y,
            GUI_INVENTORY_TOP.width, GUI_INVENTORY_TOP.height,
            CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Main Rows
        val guiMainRect = GUI_INVENTORY_MAIN(deepPocketsRowCount)
        DrawableHelper.drawTexture(matrices,
            screenX + guiMainRect.x, screenY + guiMainRect.y,
            CANVAS_INVENTORY_MAIN.x, CANVAS_INVENTORY_MAIN.y,
            guiMainRect.width, guiMainRect.height,
            CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Deep Pockets Rows
        if (inventoryAddon.getAvailableUtilityBeltSize() == UTILITY_BELT_FULL_SIZE)
        {
            val guiDeepPocketsRect = GUI_INVENTORY_DEEP_POCKETS(deepPocketsRowCount)
            DrawableHelper.drawTexture(matrices,
                screenX + guiDeepPocketsRect.x, screenY + guiDeepPocketsRect.y,
                CANVAS_INVENTORY_DEEP_POCKETS.x, CANVAS_INVENTORY_DEEP_POCKETS.y,
                guiDeepPocketsRect.width, guiDeepPocketsRect.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

            DrawableHelper.drawTexture(matrices,
                screenX + GUI_UTILITY_BELT_COLUMN_2.x, screenY + GUI_UTILITY_BELT_COLUMN_2.y,
                CANVAS_UTILITY_BELT_COLUMN_2.x, CANVAS_UTILITY_BELT_COLUMN_2.y,
                GUI_UTILITY_BELT_COLUMN_2.width, GUI_UTILITY_BELT_COLUMN_2.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)
        }

        //Utility Belt Selection Frame
        DrawableHelper.drawTexture(matrices,
            screenX + GUI_UTILITY_BELT_FRAME_ORIGIN.x + (inventoryAddon.selectedUtility / UTILITY_BELT_SMALL_SIZE) * SLOT_UI_SIZE,
            screenY + GUI_UTILITY_BELT_FRAME_ORIGIN.y + (inventoryAddon.selectedUtility % UTILITY_BELT_SMALL_SIZE) * SLOT_UI_SIZE,
            CANVAS_UTILITY_BELT_FRAME.x.toFloat(), CANVAS_UTILITY_BELT_FRAME.y.toFloat(),
            CANVAS_UTILITY_BELT_FRAME.width, CANVAS_UTILITY_BELT_FRAME.height,
            CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Tool Belt

        //If Tool Belt is 2+ columns wide, draw extra background pieces
        val size = inventoryAddon.toolBelt.size
        for (column in 0 until (size - 1) / ToolBeltSlot.getColumnCapacity(deepPocketsRowCount))
            DrawableHelper.drawTexture(matrices,
                screenX + GUI_TOOL_BELT_UI_EXTENSION.x + column * 20, screenY + GUI_TOOL_BELT_UI_EXTENSION.y,
                CANVAS_TOOL_BELT_UI_EXTENSION.x.toFloat(), CANVAS_TOOL_BELT_UI_EXTENSION.y.toFloat(),
                CANVAS_TOOL_BELT_UI_EXTENSION.width, CANVAS_TOOL_BELT_UI_EXTENSION.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Draw a slot background per each tool belt slot
        for (index in inventoryAddon.toolBelt.indices)
            DrawableHelper.drawTexture(matrices,
                screenX + ToolBeltSlot.getGuiPosition(deepPocketsRowCount, index, size).x, screenY + ToolBeltSlot.getGuiPosition(deepPocketsRowCount, index, size).y,
                CANVAS_TOOL_BELT.x, CANVAS_TOOL_BELT.y,
                SLOT_UI_SIZE, SLOT_UI_SIZE,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Draw empty slot icons
        for ((index, stack) in inventoryAddon.toolBelt.withIndex())
            if (stack.isEmpty)
            {
                textureManager.bindTexture(PlayerScreenHandlerAddon.toolBeltTemplates[index].emptyIcon)
                DrawableHelper.drawTexture(matrices,
                    screenX + ToolBeltSlot.getSlotPosition(deepPocketsRowCount, index, size).x,
                    screenY + ToolBeltSlot.getSlotPosition(deepPocketsRowCount, index, size).y,
                    0f, 0f, 16, 16, 16, 16
                )
            }
    }
}