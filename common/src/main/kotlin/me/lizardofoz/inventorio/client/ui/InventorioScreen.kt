package me.lizardofoz.inventorio.client.ui

import com.mojang.blaze3d.systems.RenderSystem
import me.lizardofoz.inventorio.config.GlobalSettings
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
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.screen.ButtonTextures
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer

@Environment(EnvType.CLIENT)
class InventorioScreen(handler: InventorioScreenHandler, inventory: PlayerInventory)
    : AbstractInventoryScreen<InventorioScreenHandler?>(handler, inventory, Text.translatable("container.crafting")), RecipeBookProvider {
    private var mouseX = 0f
    private var mouseY = 0f
    private val recipeBook = RecipeBookWidget()
    private var recipeButton: TexturedButtonWidget? = null
    private var toggleButton: TexturedButtonWidget? = null
    private var lockedCraftButton: TexturedButtonWidget? = null
    private var open = false
    private var narrow = false
    private var mouseDown = false

    private val inventoryAddon = inventory.player.inventoryAddon!!

    init {
        titleX = INVENTORY_TITLE_X + CRAFTING_GRID_OFFSET_X
    }

    //===================================================
    //Heavily modified or new methods
    //===================================================
    override fun init() {
        val client = this.client!!
        if (client.interactionManager!!.hasCreativeInventory()) {
            client.setScreen(CreativeInventoryScreen(client.player, client.player!!.networkHandler.enabledFeatures, client.options.operatorItemsTab.value))
            return
        }
        onRefresh()
        super.init()
        narrow = width < 379
        recipeBook.initialize(width, height, client, narrow, handler)
        open = true
        x = findLeftEdge(recipeBook, width, backgroundWidth - 19 - 19 * ((inventoryAddon.toolBelt.size - 1) / ToolBeltSlot.getColumnCapacity(inventoryAddon.getDeepPocketsRowCount())))
        toggleButton = addToggleButton(this)
        lockedCraftButton = addLockedCraftButton(this)
        recipeButton = addDrawableChild(TexturedButtonWidget(
                x + GUI_RECIPE_WIDGET_BUTTON.x, y + GUI_RECIPE_WIDGET_BUTTON.y,
                GUI_RECIPE_WIDGET_BUTTON.width, GUI_RECIPE_WIDGET_BUTTON.height,
                RecipeBookWidget.BUTTON_TEXTURES)
        { buttonWidget: ButtonWidget ->
            recipeBook.toggleOpen()
            x = findLeftEdge(recipeBook, width, backgroundWidth - 19 - 19 * ((inventoryAddon.toolBelt.size - 1) / ToolBeltSlot.getColumnCapacity(inventoryAddon.getDeepPocketsRowCount())))
            (buttonWidget as TexturedButtonWidget).x = x + GUI_RECIPE_WIDGET_BUTTON.x
            (buttonWidget as TexturedButtonWidget).y = y + GUI_RECIPE_WIDGET_BUTTON.y
            mouseDown = true
        })
        addSelectableChild(recipeBook)
        setInitialFocus(recipeBook)
        client.player?.inventorioScreenHandler?.updateDeepPocketsCapacity()

        initConsumers.forEach {
            try {
                it.value.accept(this)
            } catch (e: Throwable) {
                logger.error("Inventory Screen Init Consumer '${it.key}' has failed: ", e)
            }
        }
    }

    fun onRefresh() {
        backgroundWidth = GUI_INVENTORY_TOP.width + ((inventoryAddon.toolBelt.size - 1) / ToolBeltSlot.getColumnCapacity(inventoryAddon.getDeepPocketsRowCount())) * 20
        backgroundHeight = INVENTORY_HEIGHT + inventoryAddon.getDeepPocketsRowCount() * SLOT_UI_SIZE
    }

    override fun drawBackground(drawContext: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        if (PlayerSettings.aggressiveButtonRemoval.boolValue) {
            for (child in children().filter {
                it is Drawable
                        && it != recipeButton
                        && it != toggleButton
                        && it != lockedCraftButton
            })
                remove(child)
        }
        toggleButton?.x = x + backgroundWidth + GUI_TOGGLE_BUTTON_OFFSET.x
        toggleButton?.y = y + GUI_TOGGLE_BUTTON_OFFSET.y
        lockedCraftButton?.x = x + GUI_LOCKED_CRAFTING_POS.x
        lockedCraftButton?.y = y + GUI_LOCKED_CRAFTING_POS.y

        RenderSystem.setShader { GameRenderer.getPositionTexProgram() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        val texture = if (PlayerSettings.darkTheme.boolValue)
            BACKGROUND_TEXTURE_DARK
        else
            BACKGROUND_TEXTURE

        val deepPocketsRowCount = inventoryAddon.getDeepPocketsRowCount()

        //Top Part
        drawContext.drawTexture(texture,
                x + GUI_INVENTORY_TOP.x, y + GUI_INVENTORY_TOP.y,
                CANVAS_INVENTORY_TOP.x, CANVAS_INVENTORY_TOP.y,
                GUI_INVENTORY_TOP.width, GUI_INVENTORY_TOP.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Main Rows
        val guiMainRect = GUI_INVENTORY_MAIN(deepPocketsRowCount)
        drawContext.drawTexture(texture,
                x + guiMainRect.x, y + guiMainRect.y,
                CANVAS_INVENTORY_MAIN.x, CANVAS_INVENTORY_MAIN.y,
                guiMainRect.width, guiMainRect.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Deep Pockets Rows
        if (inventoryAddon.getAvailableUtilityBeltSize() == UTILITY_BELT_FULL_SIZE) {
            val guiDeepPocketsRect = GUI_INVENTORY_DEEP_POCKETS(deepPocketsRowCount)
            drawContext.drawTexture(texture,
                    x + guiDeepPocketsRect.x, y + guiDeepPocketsRect.y,
                    CANVAS_INVENTORY_DEEP_POCKETS.x, CANVAS_INVENTORY_DEEP_POCKETS.y,
                    guiDeepPocketsRect.width, guiDeepPocketsRect.height,
                    CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

            drawContext.drawTexture(texture,
                    x + GUI_UTILITY_BELT_COLUMN_2.x, y + GUI_UTILITY_BELT_COLUMN_2.y,
                    CANVAS_UTILITY_BELT_COLUMN_2.x, CANVAS_UTILITY_BELT_COLUMN_2.y,
                    GUI_UTILITY_BELT_COLUMN_2.width, GUI_UTILITY_BELT_COLUMN_2.height,
                    CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)
        }

        //Utility Belt Selection Frame
        drawContext.drawTexture(texture,
                x + GUI_UTILITY_BELT_FRAME_ORIGIN.x + (inventoryAddon.selectedUtility / UTILITY_BELT_SMALL_SIZE) * SLOT_UI_SIZE,
                y + GUI_UTILITY_BELT_FRAME_ORIGIN.y + (inventoryAddon.selectedUtility % UTILITY_BELT_SMALL_SIZE) * SLOT_UI_SIZE,
                CANVAS_UTILITY_BELT_FRAME.x.toFloat(), CANVAS_UTILITY_BELT_FRAME.y.toFloat(),
                CANVAS_UTILITY_BELT_FRAME.width, CANVAS_UTILITY_BELT_FRAME.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Tool Belt

        //If Tool Belt is 2+ columns wide, draw extra background pieces
        val size = inventoryAddon.toolBelt.size
        for (column in 0 until (size - 1) / ToolBeltSlot.getColumnCapacity(deepPocketsRowCount))
            drawContext.drawTexture(texture,
                    x + GUI_TOOL_BELT_UI_EXTENSION.x + column * 20, y + GUI_TOOL_BELT_UI_EXTENSION.y,
                    CANVAS_TOOL_BELT_UI_EXTENSION.x.toFloat(), CANVAS_TOOL_BELT_UI_EXTENSION.y.toFloat(),
                    CANVAS_TOOL_BELT_UI_EXTENSION.width, CANVAS_TOOL_BELT_UI_EXTENSION.height,
                    CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Draw a slot background per each tool belt slot
        for (index in inventoryAddon.toolBelt.indices)
            drawContext.drawTexture(texture,
                    x + ToolBeltSlot.getGuiPosition(deepPocketsRowCount, index, size).x, y + ToolBeltSlot.getGuiPosition(deepPocketsRowCount, index, size).y,
                    CANVAS_TOOL_BELT.x, CANVAS_TOOL_BELT.y,
                    SLOT_UI_SIZE, SLOT_UI_SIZE,
                    CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y)

        //Draw empty slot icons
        for ((index, stack) in inventoryAddon.toolBelt.withIndex())
            if (stack.isEmpty) {
                drawContext.drawTexture(PlayerInventoryAddon.toolBeltTemplates[index].emptyIcon,
                        x + ToolBeltSlot.getSlotPosition(deepPocketsRowCount, index, size).x,
                        y + ToolBeltSlot.getSlotPosition(deepPocketsRowCount, index, size).y,
                        0f, 0f, 16, 16, 16, 16
                )
            }

        InventoryScreen.drawEntity(drawContext, x + 26, y + 8, x + 75, y + 78, 30, 0.0625f, this.mouseX, this.mouseY, client!!.player)
    }

    //Yes, it's a vanilla method replicated here. 1.17 and 1.17.1 mapping differ and I don't want to have different versions of a mod
    //todo this is a 1.18 branch!
    private fun findLeftEdge(widget: RecipeBookWidget, width: Int, parentWidth: Int): Int {
        return if (widget.isOpen && !narrow)
            177 + (width - parentWidth - 200) / 2
        else
            (width - parentWidth) / 2
    }

    //===================================================
    //Unmodified methods lifted from InventoryScreen
    //===================================================
    override fun drawForeground(drawContext: DrawContext, mouseX: Int, mouseY: Int) {
        drawContext.drawText(textRenderer, title, titleX, titleY, 4210752, false)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (recipeBook.isOpen && this.narrow) {
            this.renderBackground(context, mouseX, mouseY, delta)
            recipeBook.render(context, mouseX, mouseY, delta)
        } else {
            super.render(context, mouseX, mouseY, delta)
            recipeBook.render(context, mouseX, mouseY, delta)
            recipeBook.drawGhostSlots(context, this.x, this.y, false, delta)
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY)
        recipeBook.drawTooltip(context, this.x, this.y, mouseX, mouseY)
        this.mouseX = mouseX.toFloat()
        this.mouseY = mouseY.toFloat()
    }

    override fun isPointWithinBounds(xPosition: Int, yPosition: Int, width: Int, height: Int, pointX: Double, pointY: Double): Boolean {
        return (!narrow || !recipeBook.isOpen) && super.isPointWithinBounds(xPosition, yPosition, width, height, pointX, pointY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!recipeBook.mouseClicked(mouseX, mouseY, button))
            return if (narrow && recipeBook.isOpen) false else super.mouseClicked(mouseX, mouseY, button)
        focused = recipeBook
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!mouseDown)
            return super.mouseReleased(mouseX, mouseY, button)
        mouseDown = false
        return true
    }

    override fun isClickOutsideBounds(mouseX: Double, mouseY: Double, left: Int, top: Int, button: Int): Boolean {
        val bl = mouseX < left.toDouble() || mouseY < top.toDouble() || mouseX >= (left + backgroundWidth).toDouble() || mouseY >= (top + backgroundHeight).toDouble()
        return recipeBook.isClickOutsideBounds(mouseX, mouseY, x, y, backgroundWidth, backgroundHeight, button) && bl
    }

    override fun onMouseClick(slot: Slot?, invSlot: Int, clickData: Int, actionType: SlotActionType) {
        super.onMouseClick(slot, invSlot, clickData, actionType)
        recipeBook.slotClicked(slot)
    }

    override fun refreshRecipeBook() {
        recipeBook.refresh()
    }

    override fun getRecipeBookWidget(): RecipeBookWidget {
        return recipeBook
    }

    override fun handledScreenTick() {
        val client = client!!
        if (client.interactionManager!!.hasCreativeInventory() && client.player != null)
            client.setScreen(CreativeInventoryScreen(client.player, client.player!!.networkHandler.enabledFeatures, client.options.operatorItemsTab.value))
        else
            recipeBook.update()
    }

    //===================================================
    //Companion Object
    //===================================================
    companion object {
        private val TOGGLE_BUTTON_ON_TEXTURES = ButtonTextures(Identifier("inventorio", "toggle_button_on"), Identifier("inventorio", "toggle_button_active_on"))
        private val TOGGLE_BUTTON_OFF_TEXTURES = ButtonTextures(Identifier("inventorio", "toggle_button_off"), Identifier("inventorio", "toggle_button_active_off"))
        private val LOCK_BUTTON_TEXTURES = ButtonTextures(Identifier("inventorio", "lock_button"), Identifier("inventorio", "lock_button_active"))
        private val TOGGLE_BUTTON_ON_TEXTURES_DARK = ButtonTextures(Identifier("inventorio", "toggle_button_on_dark"), Identifier("inventorio", "toggle_button_active_on_dark"))
        private val TOGGLE_BUTTON_OFF_TEXTURES_DARK = ButtonTextures(Identifier("inventorio", "toggle_button_off_dark"), Identifier("inventorio", "toggle_button_active_off_dark"))
        private val LOCK_BUTTON_TEXTURES_DARK = ButtonTextures(Identifier("inventorio", "lock_button_dark"), Identifier("inventorio", "lock_button_active_dark"))
        private val BACKGROUND_TEXTURE = Identifier("inventorio", "textures/gui/player_inventory.png")
        private val BACKGROUND_TEXTURE_DARK = Identifier("inventorio", "textures/gui/player_inventory_dark.png")

        private val initConsumers = mutableMapOf<Identifier, Consumer<InventorioScreen>>()

        @JvmField
        var shouldOpenVanillaInventory = false

        @JvmStatic
        fun registerInitConsumer(customIdentifier: Identifier, uiConsumer: Consumer<InventorioScreen>) {
            if (initConsumers.containsKey(customIdentifier))
                throw IllegalStateException("The Identifier '$customIdentifier' has already been taken")
            initConsumers[customIdentifier] = uiConsumer
        }

        @JvmStatic
        fun addToggleButton(screen: Screen): TexturedButtonWidget? {
            if (!PlayerSettings.toggleButton.boolValue)
                return null
            val textures = if (screen is InventorioScreen) TOGGLE_BUTTON_ON_TEXTURES else TOGGLE_BUTTON_OFF_TEXTURES
            val texturesDark = if (screen is InventorioScreen) TOGGLE_BUTTON_ON_TEXTURES_DARK else TOGGLE_BUTTON_OFF_TEXTURES_DARK
            val screenAccessor = screen as HandledScreenAccessor
            val button = TexturedButtonWidget(
                    screenAccessor.x + screen.backgroundWidth + GUI_TOGGLE_BUTTON_OFFSET.x, screenAccessor.y + GUI_TOGGLE_BUTTON_OFFSET.y,
                    GUI_TOGGLE_BUTTON_OFFSET.width, GUI_TOGGLE_BUTTON_OFFSET.height,
                    if (PlayerSettings.darkTheme.boolValue) texturesDark else textures)
            {
                val client = MinecraftClient.getInstance() ?: return@TexturedButtonWidget
                shouldOpenVanillaInventory = client.currentScreen is InventorioScreen
                client.currentScreen?.close()
                if (shouldOpenVanillaInventory)
                    client.setScreen(InventoryScreen(client.player))
                else
                    InventorioNetworking.INSTANCE.c2sOpenInventorioScreen()
            }
            screenAccessor.selectables.add(button)
            screenAccessor.drawables.add(button)
            screenAccessor.children.add(button)
            return button
        }

        @JvmStatic
        fun addLockedCraftButton(screen: Screen): TexturedButtonWidget? {
            if (GlobalSettings.allow2x2CraftingGrid.boolValue)
                return null
            val screenAccessor = screen as HandledScreenAccessor
            val button = TexturedButtonWidget(
                    screenAccessor.x + GUI_LOCKED_CRAFTING_POS.x, screenAccessor.y + GUI_LOCKED_CRAFTING_POS.y,
                    GUI_LOCKED_CRAFTING_POS.width, GUI_LOCKED_CRAFTING_POS.height,
                    if (PlayerSettings.darkTheme.boolValue) LOCK_BUTTON_TEXTURES_DARK else LOCK_BUTTON_TEXTURES)
            {
                val client = MinecraftClient.getInstance() ?: return@TexturedButtonWidget
                client.currentScreen?.close()
                client.setScreen(InventoryScreen(client.player))
            }
            screenAccessor.selectables.add(button)
            screenAccessor.drawables.add(button)
            screenAccessor.children.add(button)
            return button
        }
    }
}