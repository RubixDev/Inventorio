package de.rubixdev.inventorio.integration.curios

import de.rubixdev.inventorio.client.ui.InventorioScreen
import de.rubixdev.inventorio.integration.curios.InventorioScreenMixinHelper.Companion.CURIO_INVENTORY
import de.rubixdev.inventorio.packet.InventorioNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import top.theillusivec4.curios.client.gui.PageButton
import top.theillusivec4.curios.common.network.client.CPacketPage

class CustomPageButton(
    private val parentGui: InventorioScreen,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    private val type: PageButton.Type,
) : ButtonWidget(x, y, width, height, ScreenTexts.EMPTY, {
    InventorioNetworking.INSTANCE.sendToServer(CPacketPage(parentGui.screenHandler.syncId, type == PageButton.Type.NEXT))
}, DEFAULT_NARRATION_SUPPLIER) {
    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val curiosHandler = parentGui.screenHandler as ICuriosContainer
        var xText = if (type == PageButton.Type.NEXT) 43 else 32
        var yText = 25

        if (type == PageButton.Type.NEXT) {
            x = parentGui.guiLeft - 17
            active = curiosHandler.`inventorio$currentPage` + 1 < curiosHandler.`inventorio$totalPages`
        } else {
            x = parentGui.guiLeft - 28
            active = curiosHandler.`inventorio$currentPage` > 0
        }

        if (!isNarratable) {
            yText += 12
        } else if (isSelected) {
            xText += 22
        }

        if (isHovered) {
            context.drawTooltip(
                MinecraftClient.getInstance().textRenderer,
                Text.translatable("gui.curios.page", curiosHandler.`inventorio$currentPage` + 1, curiosHandler.`inventorio$totalPages`),
                x,
                y,
            )
        }
        context.drawTexture(CURIO_INVENTORY, x, y, xText, yText, width, height)
    }
}
