package me.danetnaverno.inventorio.client.inventory

import com.google.common.collect.ImmutableList
import me.danetnaverno.inventorio.client.quickbar.QuickBarInventoryWidget
import me.danetnaverno.inventorio.mixin.client.HandledScreenAccessor
import me.danetnaverno.inventorio.player.CreativeScreenHandlerAddon
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemGroup
import net.minecraft.screen.slot.Slot
import net.minecraft.util.Identifier
import java.awt.Rectangle

@Environment(EnvType.CLIENT)
object CreativeInventoryScreenAddon
{
    private val BACKGROUND_TEXTURE = Identifier("inventorio", "textures/gui/inventory_creative_replacement.png")

    lateinit var inventoryScreen: CreativeInventoryScreen
    private lateinit var playerAddon: PlayerAddon
    private lateinit var screenAccessor: HandledScreenAccessor
    lateinit var quickBarWidget: QuickBarInventoryWidget
    private var lastGroup: ItemGroup? = null

    fun init(creativeInventoryScreen: CreativeInventoryScreen)
    {
        playerAddon = PlayerAddon.Client.local
        inventoryScreen = creativeInventoryScreen
        screenAccessor = inventoryScreen as HandledScreenAccessor
        quickBarWidget = QuickBarInventoryWidget(playerAddon)
        screenAccessor.backgroundWidth = 249
        screenAccessor.backgroundHeight = 136
    }

    fun setSelectedTab(slots: List<Slot>?, group: ItemGroup): List<Slot>?
    {
        if (group == ItemGroup.INVENTORY)
        {
            //scrollPosition = 0.0f
            if (slots == null)
                return ImmutableList.copyOf(inventoryScreen.screenHandler.slots)
            ((inventoryScreen.screenHandler as HandlerDuck).addon as CreativeScreenHandlerAddon).enableSurvivalInventory(playerAddon)
        }
        else
        {
            if (slots != null)
            {
                inventoryScreen.screenHandler.slots.clear()
                inventoryScreen.screenHandler.slots.addAll(slots)
                return null
            }
            if (lastGroup == ItemGroup.INVENTORY)
                ((inventoryScreen.screenHandler as HandlerDuck).addon as CreativeScreenHandlerAddon).disableSurvivalInventory(playerAddon)
        }
        lastGroup = group
        return slots
    }

    fun drawAddon(matrices: MatrixStack)
    {
        MinecraftClient.getInstance().textureManager.bindTexture(BACKGROUND_TEXTURE)
        if (inventoryScreen.selectedTab == ItemGroup.INVENTORY.index)
            drawNonSurvivalTabAddon(matrices)
        else
            drawNonSurvivalTabAddon(matrices)
    }

    private fun drawSurvivalTabAddon(matrices: MatrixStack)
    {
        val screenX = screenAccessor.x
        val screenY = screenAccessor.y

        val topRect = Rectangle(-13, -13, 256, 30)
        DrawableHelper.drawTexture(matrices,
                screenX + topRect.x, screenY + topRect.y,
                CANVAS_EXTERNAL_INVENTORY_TOP_PART.x.toFloat(), CANVAS_EXTERNAL_INVENTORY_TOP_PART.y.toFloat(),
                topRect.width, topRect.height,
                256, 256)

        val mainRec = Rectangle(-13, 30 - 13, 256, 96)
        val canvas = Rectangle(0, 96, 256, 96)
        DrawableHelper.drawTexture(matrices,
                screenX + mainRec.x, screenY + mainRec.y,
                canvas.x.toFloat(), canvas.y.toFloat(),
                mainRec.width, mainRec.height,
                256, 256)

        //QuickBar (Physical slots when present)
        val quickBarRect = Rectangle(8, 76, 0, 0)
        quickBarWidget.drawPhysSlots(matrices,
                screenX + quickBarRect.x, screenY + quickBarRect.y,
                CANVAS_CREATIVE_INVENTORY_PHYS_BAR.x, CANVAS_CREATIVE_INVENTORY_PHYS_BAR.y,
                inventorioRowLength,
                256, 256)
    }

    private fun drawNonSurvivalTabAddon(matrices: MatrixStack)
    {
        val screenX = screenAccessor.x
        val screenY = screenAccessor.y

        val mainRec = Rectangle(-13 + 9, -13, 256, 162)
        DrawableHelper.drawTexture(matrices,
                screenX + mainRec.x, screenY + mainRec.y,
                CANVAS_CREATIVE_INVENTORY.x.toFloat(), CANVAS_CREATIVE_INVENTORY.y.toFloat(),
                mainRec.width, mainRec.height,
                256, 256)

        if (inventoryScreen.selectedTab == ItemGroup.SEARCH.index)
        {
            val guiSearchBar = Rectangle(80, 4, 90, 12)
            DrawableHelper.drawTexture(matrices,
                    screenX + guiSearchBar.x, screenY + guiSearchBar.y,
                    CANVAS_CREATIVE_INVENTORY_SEARCH_BAR.x.toFloat(), CANVAS_CREATIVE_INVENTORY_SEARCH_BAR.y.toFloat(),
                    guiSearchBar.width, guiSearchBar.height,
                    256, 256)
        }

        //QuickBar (Physical slots when present)
        val quickBarRect = Rectangle(8, 111, 0, 0)
        quickBarWidget.drawPhysSlots(matrices,
                screenX + quickBarRect.x, screenY + quickBarRect.y,
                CANVAS_CREATIVE_INVENTORY_PHYS_BAR.x, CANVAS_CREATIVE_INVENTORY_PHYS_BAR.y,
                inventorioRowLength,
                256, 256)
    }

    fun drawScrollBar(scrollPosition: Float, matrices: MatrixStack)
    {
        val screenX = screenAccessor.x
        val screenY = screenAccessor.y
        val i = screenX + 229
        val j = screenY + 18
        val k = j + 112

        DrawableHelper.drawTexture(matrices,
                i, j + ((k - j - 17).toFloat() * scrollPosition).toInt(),
                232f, 0f,
                12, 15,
                256, 256)
    }
}