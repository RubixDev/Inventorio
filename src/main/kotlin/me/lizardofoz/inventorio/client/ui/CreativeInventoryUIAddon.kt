package me.lizardofoz.inventorio.client.ui

import com.google.common.collect.ImmutableList
import me.lizardofoz.inventorio.client.quickbar.QuickBarUIWidget
import me.lizardofoz.inventorio.mixin.client.accessor.HandledScreenAccessor
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.util.*
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
object CreativeInventoryUIAddon
{
    private val BACKGROUND_TEXTURE_SURVIVAL = Identifier("inventorio", "textures/gui/inventory_creative_survival.png")
    private val BACKGROUND_TEXTURE = Identifier("inventorio", "textures/gui/inventory_creative_replacement.png")

    lateinit var inventoryScreen: CreativeInventoryScreen
    private lateinit var playerAddon: PlayerAddon
    private lateinit var screenAccessor: HandledScreenAccessor
    lateinit var quickBarWidget: QuickBarUIWidget
    private var lastGroup: ItemGroup? = null

    fun init(creativeInventoryScreen: CreativeInventoryScreen)
    {
        playerAddon = PlayerAddon.Client.local
        inventoryScreen = creativeInventoryScreen
        screenAccessor = inventoryScreen as HandledScreenAccessor
        quickBarWidget = QuickBarUIWidget(playerAddon)
        screenAccessor.backgroundWidth = 249
        screenAccessor.backgroundHeight = 136
    }

    fun setSelectedTab(slots: MutableList<Slot>?, group: ItemGroup, deleteItemSlot: Slot?): MutableList<Slot>?
    {
        if (group == ItemGroup.INVENTORY)
        {
            //scrollPosition = 0.0f
            if (slots == null)
                return ImmutableList.copyOf(inventoryScreen.screenHandler.slots)
            ((inventoryScreen.screenHandler as HandlerDuck).addon as CreativeScreenHandlerAddon).enableSurvivalInventory(playerAddon, deleteItemSlot)
        }
        else
        {
            if (slots != null)
            {
                inventoryScreen.screenHandler.slots.clear()
                inventoryScreen.screenHandler.slots.addAll(slots)
                return null
            }
            ((inventoryScreen.screenHandler as HandlerDuck).addon as CreativeScreenHandlerAddon).disableSurvivalInventory(playerAddon, deleteItemSlot)
        }
        lastGroup = group
        return slots
    }

    fun drawAddon(matrices: MatrixStack)
    {
        if (inventoryScreen.selectedTab == ItemGroup.INVENTORY.index)
            drawSurvivalTabAddon(matrices)
        else
            drawNonSurvivalTabAddon(matrices)
    }

    private fun drawSurvivalTabAddon(matrices: MatrixStack)
    {
        MinecraftClient.getInstance().textureManager.bindTexture(BACKGROUND_TEXTURE_SURVIVAL)
        val screenX = screenAccessor.x
        val screenY = screenAccessor.y

        val mainRec = Rectangle(-13 + 9, -13, 256, 162)
        DrawableHelper.drawTexture(matrices,
                screenX + mainRec.x, screenY + mainRec.y,
                CANVAS_CREATIVE_INVENTORY.x.toFloat(), CANVAS_CREATIVE_INVENTORY.y.toFloat(),
                mainRec.width, mainRec.height,
                256, 256)

        //QuickBar (Physical slots when present)
        val quickBarRect = Rectangle(8, 111, 0, 0)
        quickBarWidget.drawPhysSlots(matrices,
                screenX + quickBarRect.x, screenY + quickBarRect.y,
                CANVAS_CREATIVE_INVENTORY_PHYS_BAR.x, CANVAS_CREATIVE_INVENTORY_PHYS_BAR.y,
                INVENTORIO_ROW_LENGTH,
                256, 256)
    }

    private fun drawNonSurvivalTabAddon(matrices: MatrixStack)
    {
        MinecraftClient.getInstance().textureManager.bindTexture(BACKGROUND_TEXTURE)
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
                INVENTORIO_ROW_LENGTH,
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