package me.lizardofoz.inventorio.client.ui

import com.mojang.blaze3d.systems.RenderSystem
import me.lizardofoz.inventorio.client.InventoryOffsets
import me.lizardofoz.inventorio.client.quickbar.QuickBarUIWidget
import me.lizardofoz.inventorio.mixin.client.accessor.HandledScreenAccessor
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.screenhandler.ExternalScreenHandlerAddon
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import java.awt.Point

@Environment(EnvType.CLIENT)
object ExternalInventoryUIAddon
{
    private val BACKGROUND_TEXTURE = Identifier("inventorio", "textures/gui/inventory_replacement.png")

    private var screen: HandledScreen<*>? = null
    private lateinit var accessor: HandledScreenAccessor
    private val client = MinecraftClient.getInstance()!!
    private lateinit var quickBarWidget: QuickBarUIWidget
    private var isIgnored = false
    private lateinit var offsetPoint : Point

    private fun initialize(handledScreen: HandledScreen<*>)
    {
        screen = handledScreen
        val playerAddon = PlayerAddon.Client.local
        isIgnored = playerAddon.isScreenHandlerIgnored(handledScreen.screenHandler)
        if (isIgnored)
            return

        offsetPoint = ScreenHandlerOffsets.getScreenHandlerOffset(handledScreen.screenHandler)
        accessor = screen as HandledScreenAccessor
        accessor.backgroundHeight += playerAddon.getExtraRows() * INVENTORY_SLOT_SIZE
        accessor.y = (handledScreen.height - accessor.backgroundHeight) / 2

        quickBarWidget = QuickBarUIWidget(playerAddon)

        val offset = InventoryOffsets.getInventoryTextOffset(handledScreen)
        accessor.playerInventoryTitleX = accessor.playerInventoryTitleX + offset.x
        accessor.playerInventoryTitleY = accessor.playerInventoryTitleY + offset.y

        val screenHandlerAddon = (handledScreen.screenHandler as? HandlerDuck)?.addon as ExternalScreenHandlerAddon
        screenHandlerAddon.offsetPlayerSlots(
                Math.min(0, (accessor.backgroundWidth - GUI_GENERAL_SCREEN_WIDTH) / 2), 0,
                0, 0)
    }

    fun drawAddon(handledScreen: HandledScreen<*>, matrices: MatrixStack, mouseX: Int, mouseY: Int)
    {
        if (screen != handledScreen)
            initialize(handledScreen)
        if (isIgnored)
            return

        val nonPlayerSlots = screen!!.screenHandler.slots.filterNot { it.isPlayerSlot }

        val screenX = accessor.x + offsetPoint.x + Math.min(0, (accessor.backgroundWidth - GUI_GENERAL_SCREEN_WIDTH) / 2)
        val screenY = accessor.y + offsetPoint.y + (nonPlayerSlots.maxOfOrNull { it.y } ?: 0) + INVENTORY_SLOT_SIZE + 3
        val expansionRows = PlayerAddon[client.player!!].getExtraRows()

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        client.textureManager.bindTexture(BACKGROUND_TEXTURE)

        DrawableHelper.drawTexture(matrices,
                screenX + GUI_EXTERNAL_INVENTORY_TOP_PART.x, screenY + GUI_EXTERNAL_INVENTORY_TOP_PART.y,
                CANVAS_EXTERNAL_INVENTORY_TOP_PART.x.toFloat(), CANVAS_EXTERNAL_INVENTORY_TOP_PART.y.toFloat(),
                GUI_EXTERNAL_INVENTORY_TOP_PART.width, GUI_EXTERNAL_INVENTORY_TOP_PART.height,
                256, 256)

        val mainRec = GUI_EXTERNAL_INVENTORY_MAIN_PART(expansionRows)
        DrawableHelper.drawTexture(matrices,
                screenX + mainRec.x, screenY + mainRec.y,
                CANVAS_EXTERNAL_INVENTORY_MAIN_PART.x.toFloat(), CANVAS_EXTERNAL_INVENTORY_MAIN_PART.y.toFloat(),
                mainRec.width, mainRec.height,
                256, 256)

        if (expansionRows > 0)
        {
            val extRec = GUI_EXTERNAL_INVENTORY_EXTENSION_PART_TOP(expansionRows)
            DrawableHelper.drawTexture(matrices,
                    screenX + extRec.x, screenY + extRec.y,
                    CANVAS_EXTERNAL_INVENTORY_EXTENSION_PART_TOP.x.toFloat(), CANVAS_EXTERNAL_INVENTORY_EXTENSION_PART_TOP.y.toFloat(),
                    extRec.width, extRec.height,
                    256, 256)

            val extRec2 = GUI_EXTERNAL_INVENTORY_EXTENSION_PART_BOTTOM(expansionRows)
            DrawableHelper.drawTexture(matrices,
                    screenX + extRec2.x, screenY + extRec2.y,
                    CANVAS_EXTERNAL_INVENTORY_EXTENSION_PART_BOTTOM.x.toFloat(), CANVAS_EXTERNAL_INVENTORY_EXTENSION_PART_BOTTOM.y.toFloat(),
                    extRec2.width, extRec2.height,
                    256, 256)
        }

        //QuickBar (Physical slots when present)
        val quickBarRect = GUI_EXTERNAL_INVENTORY_QUICK_BAR(expansionRows)
        quickBarWidget.drawPhysSlots(matrices,
                screenX + quickBarRect.x, screenY + quickBarRect.y,
                CANVAS_PLAYER_INVENTORY_PHYS_BAR.x, CANVAS_PLAYER_INVENTORY_PHYS_BAR.y,
                INVENTORIO_ROW_LENGTH,
                256, 256)

        if (!handledScreen.javaClass.name.startsWith("net.minecraft."))
        {
            val extRec3 = GUI_EXTERNAL_INVENTORY_INGORE_BUTTON(expansionRows)
            val text = LiteralText("✘")
            accessor.addAButton(ButtonWidget(screenX + extRec3.x, screenY + extRec3.y, extRec3.width, extRec3.height, text,
                    { button ->
                        val playerAddonLocal = PlayerAddon.Client.local
                        playerAddonLocal.addScreenHandlerToIgnored(playerAddonLocal.player.currentScreenHandler)
                        client.player!!.closeHandledScreen()
                    }, { buttonWidget: ButtonWidget, matrixStack: MatrixStack, x: Int, y: Int ->
                screen!!.renderTooltip(matrices, TranslatableText("inventorio.ignore_screen.tooltip"), x, y)
            }))
        }
    }
}