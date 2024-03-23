package de.rubixdev.inventorio.client.ui

import com.mojang.blaze3d.systems.RenderSystem
import de.rubixdev.inventorio.config.GlobalSettings
import de.rubixdev.inventorio.config.PlayerSettings
import de.rubixdev.inventorio.mixin.client.accessor.HandledScreenAccessor
import de.rubixdev.inventorio.packet.InventorioNetworking
import de.rubixdev.inventorio.player.InventorioScreenHandler
import de.rubixdev.inventorio.player.InventorioScreenHandler.Companion.inventorioScreenHandler
import de.rubixdev.inventorio.player.PlayerInventoryAddon
import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import de.rubixdev.inventorio.slot.ToolBeltSlot
import de.rubixdev.inventorio.util.*
import java.util.function.Consumer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import net.minecraft.util.Identifier

//#if MC >= 12002
import net.minecraft.client.gui.screen.ButtonTextures
//#endif

@Environment(EnvType.CLIENT)
class InventorioScreen(handler: InventorioScreenHandler, inventory: PlayerInventory) :
    AbstractInventoryScreen<InventorioScreenHandler?>(handler, inventory, Text.translatable("container.crafting")), RecipeBookProvider {
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

    // ===================================================
    // Heavily modified or new methods
    // ===================================================
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
        toggleButton = addToggleButton(this)
        lockedCraftButton = addLockedCraftButton(this)
        recipeButton = addDrawableChild(
            TexturedButtonWidget(
                x + GUI_RECIPE_WIDGET_BUTTON.x,
                y + GUI_RECIPE_WIDGET_BUTTON.y,
                GUI_RECIPE_WIDGET_BUTTON.width,
                GUI_RECIPE_WIDGET_BUTTON.height,
                //#if MC >= 12002
                RecipeBookWidget.BUTTON_TEXTURES,
                //#else
                //$$ 0, 0, 19, RECIPE_BUTTON_TEXTURE,
                //#endif
            ) {
                recipeBook.toggleOpen()
                updateScreenPosition()
            },
        )
        addSelectableChild(recipeBook)
        setInitialFocus(recipeBook)
        updateScreenPosition()
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

    @Suppress("MemberVisibilityCanBePrivate") // used from non-common package
    fun updateScreenPosition() {
        x = recipeBook.findLeftEdge(
            width,
            backgroundWidth - 19 - 19
                * ((inventoryAddon.toolBelt.size - 1) / ToolBeltSlot.getColumnCapacity(inventoryAddon.getDeepPocketsRowCount())),
        )
        recipeButton?.x = x + GUI_RECIPE_WIDGET_BUTTON.x
        recipeButton?.y = y + GUI_RECIPE_WIDGET_BUTTON.y
        mouseDown = true
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
        val texture = if (PlayerSettings.darkTheme.boolValue) {
            BACKGROUND_TEXTURE_DARK
        } else {
            BACKGROUND_TEXTURE
        }

        val deepPocketsRowCount = inventoryAddon.getDeepPocketsRowCount()

        // Top Part
        drawContext.drawTexture(
            texture,
            x + GUI_INVENTORY_TOP.x, y + GUI_INVENTORY_TOP.y,
            CANVAS_INVENTORY_TOP.x, CANVAS_INVENTORY_TOP.y,
            GUI_INVENTORY_TOP.width, GUI_INVENTORY_TOP.height,
            CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y,
        )

        // Main Rows
        val guiMainRect = GUI_INVENTORY_MAIN(deepPocketsRowCount)
        drawContext.drawTexture(
            texture,
            x + guiMainRect.x, y + guiMainRect.y,
            CANVAS_INVENTORY_MAIN.x, CANVAS_INVENTORY_MAIN.y,
            guiMainRect.width, guiMainRect.height,
            CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y,
        )

        // Deep Pockets Rows
        if (inventoryAddon.getAvailableUtilityBeltSize() == UTILITY_BELT_FULL_SIZE) {
            val guiDeepPocketsRect = GUI_INVENTORY_DEEP_POCKETS(deepPocketsRowCount)
            drawContext.drawTexture(
                texture,
                x + guiDeepPocketsRect.x, y + guiDeepPocketsRect.y,
                CANVAS_INVENTORY_DEEP_POCKETS.x, CANVAS_INVENTORY_DEEP_POCKETS.y,
                guiDeepPocketsRect.width, guiDeepPocketsRect.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y,
            )

            drawContext.drawTexture(
                texture,
                x + GUI_UTILITY_BELT_COLUMN_2.x, y + GUI_UTILITY_BELT_COLUMN_2.y,
                CANVAS_UTILITY_BELT_COLUMN_2.x, CANVAS_UTILITY_BELT_COLUMN_2.y,
                GUI_UTILITY_BELT_COLUMN_2.width, GUI_UTILITY_BELT_COLUMN_2.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y,
            )
        }

        // Utility Belt Selection Frame
        drawContext.drawTexture(
            texture,
            x + GUI_UTILITY_BELT_FRAME_ORIGIN.x + (inventoryAddon.selectedUtility / UTILITY_BELT_SMALL_SIZE) * SLOT_UI_SIZE,
            y + GUI_UTILITY_BELT_FRAME_ORIGIN.y + (inventoryAddon.selectedUtility % UTILITY_BELT_SMALL_SIZE) * SLOT_UI_SIZE,
            CANVAS_UTILITY_BELT_FRAME.x.toFloat(), CANVAS_UTILITY_BELT_FRAME.y.toFloat(),
            CANVAS_UTILITY_BELT_FRAME.width, CANVAS_UTILITY_BELT_FRAME.height,
            CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y,
        )

        // Tool Belt

        // If Tool Belt is 2+ columns wide, draw extra background pieces
        val size = inventoryAddon.toolBelt.size
        for (column in 0 until (size - 1) / ToolBeltSlot.getColumnCapacity(deepPocketsRowCount))
            drawContext.drawTexture(
                texture,
                x + GUI_TOOL_BELT_UI_EXTENSION.x + column * 20, y + GUI_TOOL_BELT_UI_EXTENSION.y,
                CANVAS_TOOL_BELT_UI_EXTENSION.x.toFloat(), CANVAS_TOOL_BELT_UI_EXTENSION.y.toFloat(),
                CANVAS_TOOL_BELT_UI_EXTENSION.width, CANVAS_TOOL_BELT_UI_EXTENSION.height,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y,
            )

        // Draw a slot background per each tool belt slot
        for (index in inventoryAddon.toolBelt.indices)
            drawContext.drawTexture(
                texture,
                x + ToolBeltSlot.getGuiPosition(deepPocketsRowCount, index, size).x, y + ToolBeltSlot.getGuiPosition(deepPocketsRowCount, index, size).y,
                CANVAS_TOOL_BELT.x, CANVAS_TOOL_BELT.y,
                SLOT_UI_SIZE, SLOT_UI_SIZE,
                CANVAS_INVENTORY_TEXTURE_SIZE.x, CANVAS_INVENTORY_TEXTURE_SIZE.y,
            )

        // Draw empty slot icons
        for ((index, stack) in inventoryAddon.toolBelt.withIndex())
            if (stack.isEmpty) {
                drawContext.drawTexture(
                    PlayerInventoryAddon.toolBeltTemplates[index].emptyIcon,
                    x + ToolBeltSlot.getSlotPosition(deepPocketsRowCount, index, size).x,
                    y + ToolBeltSlot.getSlotPosition(deepPocketsRowCount, index, size).y,
                    0f, 0f, 16, 16, 16, 16,
                )
            }

        //#if MC >= 12002
        InventoryScreen.drawEntity(drawContext, x + 26, y + 8, x + 75, y + 78, 30, 0.0625f, this.mouseX, this.mouseY, client!!.player)
        //#else
        //$$ InventoryScreen.drawEntity(drawContext, x + 51, y + 75, 30, (x + 51).toFloat() - this.mouseX, (y + 75 - 50).toFloat() - this.mouseY, client!!.player!!)
        //#endif
    }

    // ===================================================
    // Unmodified methods lifted from InventoryScreen
    // ===================================================
    override fun drawForeground(drawContext: DrawContext, mouseX: Int, mouseY: Int) {
        drawContext.drawText(textRenderer, title, titleX, titleY, 4210752, false)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        //#if MC < 12002
        //$$ this.renderBackground(context)
        //#endif
        if (recipeBook.isOpen && narrow) {
            //#if MC >= 12002
            this.renderBackground(context, mouseX, mouseY, delta)
            //#else
            //$$ drawBackground(context, delta, mouseX, mouseY)
            //#endif
            recipeBook.render(context, mouseX, mouseY, delta)
        } else {
            super.render(context, mouseX, mouseY, delta)
            recipeBook.render(context, mouseX, mouseY, delta)
            recipeBook.drawGhostSlots(context, x, y, false, delta)
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY)
        recipeBook.drawTooltip(context, x, y, mouseX, mouseY)
        this.mouseX = mouseX.toFloat()
        this.mouseY = mouseY.toFloat()
    }

    override fun isPointWithinBounds(xPosition: Int, yPosition: Int, width: Int, height: Int, pointX: Double, pointY: Double): Boolean {
        return (!narrow || !recipeBook.isOpen) && super.isPointWithinBounds(xPosition, yPosition, width, height, pointX, pointY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!recipeBook.mouseClicked(mouseX, mouseY, button)) {
            return if (narrow && recipeBook.isOpen) false else super.mouseClicked(mouseX, mouseY, button)
        }
        focused = recipeBook
        return true
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!mouseDown) {
            return super.mouseReleased(mouseX, mouseY, button)
        }
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
        if (client.interactionManager!!.hasCreativeInventory() && client.player != null) {
            client.setScreen(CreativeInventoryScreen(client.player, client.player!!.networkHandler.enabledFeatures, client.options.operatorItemsTab.value))
        } else {
            recipeBook.update()
        }
    }

    override fun drawMouseoverTooltip(context: DrawContext, x: Int, y: Int) {
        super.drawMouseoverTooltip(context, x, y)
    }

    @Suppress("RedundantOverride") // this makes it easier to add functionality for mod compat via mixin
    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    @Suppress("RedundantOverride") // this makes it easier to add functionality for mod compat via mixin
    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        //#if MC >= 12002
        horizontalAmount: Double,
        //#endif
        verticalAmount: Double,
    ): Boolean {
        //#if MC >= 12002
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
        //#else
        //$$ return super.mouseScrolled(mouseX, mouseY, verticalAmount)
        //#endif
    }

    // ===================================================
    // Companion Object
    // ===================================================
    companion object {
        //#if MC >= 12002
        private val TOGGLE_BUTTON_ON_TEXTURES = ButtonTextures(Identifier("inventorio", "toggle_button_on"), Identifier("inventorio", "toggle_button_active_on"))
        private val TOGGLE_BUTTON_OFF_TEXTURES = ButtonTextures(Identifier("inventorio", "toggle_button_off"), Identifier("inventorio", "toggle_button_active_off"))
        private val LOCK_BUTTON_TEXTURES = ButtonTextures(Identifier("inventorio", "lock_button"), Identifier("inventorio", "lock_button_active"))
        private val TOGGLE_BUTTON_ON_TEXTURES_DARK = ButtonTextures(Identifier("inventorio", "toggle_button_on_dark"), Identifier("inventorio", "toggle_button_active_on_dark"))
        private val TOGGLE_BUTTON_OFF_TEXTURES_DARK = ButtonTextures(Identifier("inventorio", "toggle_button_off_dark"), Identifier("inventorio", "toggle_button_active_off_dark"))
        private val LOCK_BUTTON_TEXTURES_DARK = ButtonTextures(Identifier("inventorio", "lock_button_dark"), Identifier("inventorio", "lock_button_active_dark"))
        //#else
        //$$ private val RECIPE_BUTTON_TEXTURE = Identifier("textures/gui/recipe_button.png")
        //#endif
        private val BACKGROUND_TEXTURE = Identifier("inventorio", "textures/gui/player_inventory.png")
        private val BACKGROUND_TEXTURE_DARK = Identifier("inventorio", "textures/gui/player_inventory_dark.png")

        private val initConsumers = mutableMapOf<Identifier, Consumer<InventorioScreen>>()

        @JvmField
        var shouldOpenVanillaInventory = false
        @JvmField
        var isSwappingInvScreens = false

        @JvmStatic
        fun registerInitConsumer(customIdentifier: Identifier, uiConsumer: Consumer<InventorioScreen>) {
            if (initConsumers.containsKey(customIdentifier)) {
                throw IllegalStateException("The Identifier '$customIdentifier' has already been taken")
            }
            initConsumers[customIdentifier] = uiConsumer
        }

        @JvmStatic
        fun addToggleButton(screen: Screen): TexturedButtonWidget? {
            if (!PlayerSettings.toggleButton.boolValue) {
                return null
            }
            //#if MC >= 12002
            val textures = if (screen is InventorioScreen) TOGGLE_BUTTON_ON_TEXTURES else TOGGLE_BUTTON_OFF_TEXTURES
            val texturesDark = if (screen is InventorioScreen) TOGGLE_BUTTON_ON_TEXTURES_DARK else TOGGLE_BUTTON_OFF_TEXTURES_DARK
            //#else
            //$$ val canvas = if (screen is InventorioScreen) CANVAS_TOGGLE_BUTTON_ON else CANVAS_TOGGLE_BUTTON_OFF
            //#endif
            val screenAccessor = screen as HandledScreenAccessor<*>
            val button = TexturedButtonWidget(
                screenAccessor.x + screen.backgroundWidth + GUI_TOGGLE_BUTTON_OFFSET.x,
                screenAccessor.y + GUI_TOGGLE_BUTTON_OFFSET.y,
                GUI_TOGGLE_BUTTON_OFFSET.width,
                GUI_TOGGLE_BUTTON_OFFSET.height,
                //#if MC >= 12002
                if (PlayerSettings.darkTheme.boolValue) texturesDark else textures,
                //#else
                //$$ canvas.x, canvas.y, CANVAS_TOGGLE_BUTTON_HOVER_SHIFT,
                //$$ if (PlayerSettings.darkTheme.boolValue) BACKGROUND_TEXTURE_DARK else BACKGROUND_TEXTURE,
                //#endif
            ) {
                val client = MinecraftClient.getInstance() ?: return@TexturedButtonWidget
                shouldOpenVanillaInventory = client.currentScreen is InventorioScreen
                isSwappingInvScreens = true
                client.currentScreen?.close()
                if (shouldOpenVanillaInventory) {
                    client.setScreen(InventoryScreen(client.player))
                } else {
                    InventorioNetworking.INSTANCE.c2sOpenInventorioScreen()
                }
                isSwappingInvScreens = false
            }
            screenAccessor.selectables.add(button)
            screenAccessor.drawables.add(button)
            screenAccessor.children.add(button)
            return button
        }

        @JvmStatic
        fun addLockedCraftButton(screen: Screen): TexturedButtonWidget? {
            if (GlobalSettings.allow2x2CraftingGrid.boolValue) {
                return null
            }
            val screenAccessor = screen as HandledScreenAccessor<*>
            val button = TexturedButtonWidget(
                screenAccessor.x + GUI_LOCKED_CRAFTING_POS.x,
                screenAccessor.y + GUI_LOCKED_CRAFTING_POS.y,
                GUI_LOCKED_CRAFTING_POS.width,
                GUI_LOCKED_CRAFTING_POS.height,
                //#if MC >= 12002
                if (PlayerSettings.darkTheme.boolValue) LOCK_BUTTON_TEXTURES_DARK else LOCK_BUTTON_TEXTURES,
                //#else
                //$$ CANVAS_LOCKED_CRAFT_BUTTON.x, CANVAS_LOCKED_CRAFT_BUTTON.y,
                //$$ GUI_LOCKED_CRAFTING_POS.height,
                //$$ if (PlayerSettings.darkTheme.boolValue) BACKGROUND_TEXTURE_DARK else BACKGROUND_TEXTURE,
                //#endif
            ) {
                val client = MinecraftClient.getInstance() ?: return@TexturedButtonWidget
                isSwappingInvScreens = true
                client.currentScreen?.close()
                client.setScreen(InventoryScreen(client.player))
                isSwappingInvScreens = false
            }
            screenAccessor.selectables.add(button)
            screenAccessor.drawables.add(button)
            screenAccessor.children.add(button)
            return button
        }
    }
}
