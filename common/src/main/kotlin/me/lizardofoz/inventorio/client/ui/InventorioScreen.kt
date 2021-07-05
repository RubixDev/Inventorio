package me.lizardofoz.inventorio.client.ui

import com.mojang.blaze3d.systems.RenderSystem
import me.lizardofoz.inventorio.config.PlayerSettings
import me.lizardofoz.inventorio.mixin.client.accessor.HandledScreenAccessor
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.player.InventorioScreenHandler
import me.lizardofoz.inventorio.player.InventorioScreenHandler.Companion.inventorioScreenHandler
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.slot.ToolBeltSlot
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget
import net.minecraft.client.gui.widget.AbstractButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import java.util.function.Consumer

@Environment(EnvType.CLIENT)
class InventorioScreen(handler: InventorioScreenHandler, inventory: PlayerInventory)
    : AbstractInventoryScreen<InventorioScreenHandler?>(handler, inventory, TranslatableText("container.crafting")), RecipeBookProvider
{
    private var mouseX = 0f
    private var mouseY = 0f
    private val recipeBook = RecipeBookWidget()
    private var recipeButton : TexturedButtonWidget? = null
    private var toggleButton : TexturedButtonWidget? = null
    private var open = false
    private var narrow = false
    private var mouseDown = false

    private val inventoryAddon = inventory.player.inventoryAddon!!

    init
    {
        passEvents = true
        titleX = INVENTORY_TITLE_X + CRAFTING_GRID_OFFSET_X
    }

    //===================================================
    //Heavily modified or new methods
    //===================================================
    override fun init()
    {
        val client = this.client!!
        if (client.interactionManager!!.hasCreativeInventory())
        {
            client.openScreen(CreativeInventoryScreen(client.player))
            return
        }
        onRefresh()
        super.init()
        narrow = width < 379
        recipeBook.initialize(width, height, client, narrow, handler)
        open = true
        x = recipeBook.findLeftEdge(narrow, width, backgroundWidth - 19 - 19 * ((inventoryAddon.toolBelt.size - 1) / ToolBeltSlot.getColumnCapacity(inventoryAddon.getDeepPocketsRowCount())))
        children.add(recipeBook)
        setInitialFocus(recipeBook)
        toggleButton = addToggleButton(this)
        recipeButton = addButton(TexturedButtonWidget(
            x + GUI_RECIPE_WIDGET_BUTTON.x, y + GUI_RECIPE_WIDGET_BUTTON.y,
            GUI_RECIPE_WIDGET_BUTTON.width, GUI_RECIPE_WIDGET_BUTTON.height,
            0, 0,
            19, RECIPE_BUTTON_TEXTURE)
        { buttonWidget: ButtonWidget ->
            recipeBook.reset(narrow)
            recipeBook.toggleOpen()
            x = recipeBook.findLeftEdge(narrow, width, backgroundWidth - 19 - 19 * ((inventoryAddon.toolBelt.size - 1) / ToolBeltSlot.getColumnCapacity(inventoryAddon.getDeepPocketsRowCount())))
            (buttonWidget as TexturedButtonWidget).setPos(x + GUI_RECIPE_WIDGET_BUTTON.x, y + GUI_RECIPE_WIDGET_BUTTON.y)
            mouseDown = true
        })
        client.player?.inventorioScreenHandler?.updateDeepPocketsCapacity()

        initConsumers.forEach {
            try
            {
                it.value.accept(this)
            }
            catch (e: Throwable)
            {
                logger.error("Inventory Screen Init Consumer '${it.key}' has failed: ", e)
            }
        }
    }

    fun onRefresh()
    {
        backgroundWidth = GUI_INVENTORY_TOP.width + ((inventoryAddon.toolBelt.size - 1) / ToolBeltSlot.getColumnCapacity(inventoryAddon.getDeepPocketsRowCount())) * 20
        backgroundHeight = INVENTORY_HEIGHT + inventoryAddon.getDeepPocketsRowCount() * SLOT_UI_SIZE
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int)
    {
        if (PlayerSettings.aggressiveButtonRemoval.boolValue)
        {
            for (button in buttons.filter { it != recipeButton && it != toggleButton })
            {
                buttons.remove(button)
                children.remove(button)
            }
        }
        toggleButton?.setPos(x + backgroundWidth + GUI_TOGGLE_BUTTON_OFFSET.x, y + GUI_TOGGLE_BUTTON_OFFSET.y)

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        val textureManager = client!!.textureManager
        textureManager.bindTexture(BACKGROUND_TEXTURE)

        val deepPocketsRowCount = inventoryAddon.getDeepPocketsRowCount()

        //Top Part
        DrawableHelper.drawTexture(matrices,
            x + GUI_INVENTORY_TOP.x, y + GUI_INVENTORY_TOP.y,
            CANVAS_INVENTORY_TOP.x, CANVAS_INVENTORY_TOP.y,
            GUI_INVENTORY_TOP.width, GUI_INVENTORY_TOP.height,
            CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Main Rows
        val guiMainRect = GUI_INVENTORY_MAIN(deepPocketsRowCount)
        DrawableHelper.drawTexture(matrices,
            x + guiMainRect.x, y + guiMainRect.y,
            CANVAS_INVENTORY_MAIN.x, CANVAS_INVENTORY_MAIN.y,
            guiMainRect.width, guiMainRect.height,
            CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Deep Pockets Rows
        if (inventoryAddon.getAvailableUtilityBeltSize() == UTILITY_BELT_FULL_SIZE)
        {
            val guiDeepPocketsRect = GUI_INVENTORY_DEEP_POCKETS(deepPocketsRowCount)
            DrawableHelper.drawTexture(matrices,
                x + guiDeepPocketsRect.x, y + guiDeepPocketsRect.y,
                CANVAS_INVENTORY_DEEP_POCKETS.x, CANVAS_INVENTORY_DEEP_POCKETS.y,
                guiDeepPocketsRect.width, guiDeepPocketsRect.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

            DrawableHelper.drawTexture(matrices,
                x + GUI_UTILITY_BELT_COLUMN_2.x, y + GUI_UTILITY_BELT_COLUMN_2.y,
                CANVAS_UTILITY_BELT_COLUMN_2.x, CANVAS_UTILITY_BELT_COLUMN_2.y,
                GUI_UTILITY_BELT_COLUMN_2.width, GUI_UTILITY_BELT_COLUMN_2.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)
        }

        //Utility Belt Selection Frame
        DrawableHelper.drawTexture(matrices,
            x + GUI_UTILITY_BELT_FRAME_ORIGIN.x + (inventoryAddon.selectedUtility / UTILITY_BELT_SMALL_SIZE) * SLOT_UI_SIZE,
            y + GUI_UTILITY_BELT_FRAME_ORIGIN.y + (inventoryAddon.selectedUtility % UTILITY_BELT_SMALL_SIZE) * SLOT_UI_SIZE,
            CANVAS_UTILITY_BELT_FRAME.x.toFloat(), CANVAS_UTILITY_BELT_FRAME.y.toFloat(),
            CANVAS_UTILITY_BELT_FRAME.width, CANVAS_UTILITY_BELT_FRAME.height,
            CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Tool Belt

        //If Tool Belt is 2+ columns wide, draw extra background pieces
        val size = inventoryAddon.toolBelt.size
        for (column in 0 until (size - 1) / ToolBeltSlot.getColumnCapacity(deepPocketsRowCount))
            DrawableHelper.drawTexture(matrices,
                x + GUI_TOOL_BELT_UI_EXTENSION.x + column * 20, y + GUI_TOOL_BELT_UI_EXTENSION.y,
                CANVAS_TOOL_BELT_UI_EXTENSION.x.toFloat(), CANVAS_TOOL_BELT_UI_EXTENSION.y.toFloat(),
                CANVAS_TOOL_BELT_UI_EXTENSION.width, CANVAS_TOOL_BELT_UI_EXTENSION.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Draw a slot background per each tool belt slot
        for (index in inventoryAddon.toolBelt.indices)
            DrawableHelper.drawTexture(matrices,
                x + ToolBeltSlot.getGuiPosition(deepPocketsRowCount, index, size).x, y + ToolBeltSlot.getGuiPosition(deepPocketsRowCount, index, size).y,
                CANVAS_TOOL_BELT.x, CANVAS_TOOL_BELT.y,
                SLOT_UI_SIZE, SLOT_UI_SIZE,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Draw empty slot icons
        for ((index, stack) in inventoryAddon.toolBelt.withIndex())
            if (stack.isEmpty)
            {
                textureManager.bindTexture(PlayerInventoryAddon.toolBeltTemplates[index].emptyIcon)
                DrawableHelper.drawTexture(matrices,
                    x + ToolBeltSlot.getSlotPosition(deepPocketsRowCount, index, size).x,
                    y + ToolBeltSlot.getSlotPosition(deepPocketsRowCount, index, size).y,
                    0f, 0f, 16, 16, 16, 16
                )
            }

        InventoryScreen.drawEntity(x + 51, y + 75, 30, (x + 51).toFloat() - this.mouseX, (y + 75 - 50).toFloat() - this.mouseY, client!!.player)
    }

    //===================================================
    //Unmodified methods lifted from InventoryScreen
    //===================================================
    override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int)
    {
        textRenderer.draw(matrices, title, titleX.toFloat(), titleY.toFloat(), 4210752)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float)
    {
        this.renderBackground(matrices)
        drawStatusEffects = !recipeBook.isOpen
        if (recipeBook.isOpen && narrow)
        {
            drawBackground(matrices, delta, mouseX, mouseY)
            recipeBook.render(matrices, mouseX, mouseY, delta)
        }
        else
        {
            recipeBook.render(matrices, mouseX, mouseY, delta)
            super.render(matrices, mouseX, mouseY, delta)
            recipeBook.drawGhostSlots(matrices, x, y, false, delta)
        }
        drawMouseoverTooltip(matrices, mouseX, mouseY)
        recipeBook.drawTooltip(matrices, x, y, mouseX, mouseY)
        this.mouseX = mouseX.toFloat()
        this.mouseY = mouseY.toFloat()
    }

    override fun isPointWithinBounds(xPosition: Int, yPosition: Int, width: Int, height: Int, pointX: Double, pointY: Double): Boolean
    {
        return (!narrow || !recipeBook.isOpen) && super.isPointWithinBounds(xPosition, yPosition, width, height, pointX, pointY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean
    {
        if (!recipeBook.mouseClicked(mouseX, mouseY, button))
            return if (narrow && recipeBook.isOpen) false else super.mouseClicked(mouseX, mouseY, button)
        focused = recipeBook
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean
    {
        if (!mouseDown)
            return super.mouseReleased(mouseX, mouseY, button)
        mouseDown = false
        return true
    }

    override fun isClickOutsideBounds(mouseX: Double, mouseY: Double, left: Int, top: Int, button: Int): Boolean
    {
        val bl = mouseX < left.toDouble() || mouseY < top.toDouble() || mouseX >= (left + backgroundWidth).toDouble() || mouseY >= (top + backgroundHeight).toDouble()
        return recipeBook.isClickOutsideBounds(mouseX, mouseY, x, y, backgroundWidth, backgroundHeight, button) && bl
    }

    override fun onMouseClick(slot: Slot?, invSlot: Int, clickData: Int, actionType: SlotActionType)
    {
        super.onMouseClick(slot, invSlot, clickData, actionType)
        recipeBook.slotClicked(slot)
    }

    override fun refreshRecipeBook()
    {
        recipeBook.refresh()
    }

    override fun removed()
    {
        if (open)
            recipeBook.close()
        super.removed()
    }

    override fun getRecipeBookWidget(): RecipeBookWidget
    {
        return recipeBook
    }

    override fun tick()
    {
        if (client!!.interactionManager!!.hasCreativeInventory())
            client!!.openScreen(CreativeInventoryScreen(client!!.player))
        else
            recipeBook.update()
    }

    //===================================================
    //Companion Object
    //===================================================
    companion object
    {
        private val RECIPE_BUTTON_TEXTURE = Identifier("textures/gui/recipe_button.png")
        private val BACKGROUND_TEXTURE = Identifier("inventorio", "textures/gui/player_inventory.png")

        private val initConsumers = mutableMapOf<Identifier, Consumer<InventorioScreen>>()

        @JvmStatic
        fun registerInitConsumer(customIdentifier: Identifier, uiConsumer: Consumer<InventorioScreen>)
        {
            if (initConsumers.containsKey(customIdentifier))
                throw IllegalStateException("The Identifier '$customIdentifier' has already been taken")
            initConsumers[customIdentifier] = uiConsumer
        }

        @JvmStatic
        fun addToggleButton(screen: Screen): TexturedButtonWidget?
        {
            if (!PlayerSettings.toggleButton.boolValue)
                return null
            val canvas = if (screen is InventorioScreen) CANVAS_TOGGLE_BUTTON_ON else CANVAS_TOGGLE_BUTTON_OFF
            val screenAccessor = screen as HandledScreenAccessor
            return screenAccessor.addAButton(TexturedButtonWidget(
                screenAccessor.x + screen.backgroundWidth + GUI_TOGGLE_BUTTON_OFFSET.x, screenAccessor.y + GUI_TOGGLE_BUTTON_OFFSET.y,
                GUI_TOGGLE_BUTTON_OFFSET.width, GUI_TOGGLE_BUTTON_OFFSET.height,
                canvas.x, canvas.y,
                CANVAS_TOGGLE_BUTTON_HOVER_SHIFT, BACKGROUND_TEXTURE)
            { button ->
                val client = MinecraftClient.getInstance() ?: return@TexturedButtonWidget
                val toVanilla = client.currentScreen is InventorioScreen
                client.currentScreen?.onClose()
                if (toVanilla)
                    client.openScreen(InventoryScreen(client.player))
                else
                    InventorioNetworking.INSTANCE.c2sOpenInventorioScreen()
            })
        }
    }
}