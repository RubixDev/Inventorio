package de.rubixdev.inventorio.integration.curios

import de.rubixdev.inventorio.client.ui.InventorioScreen
import de.rubixdev.inventorio.packet.InventorioNetworking
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.text.Text
import top.theillusivec4.curios.client.gui.CosmeticButton
import top.theillusivec4.curios.common.network.client.CPacketToggleCosmetics

class CustomCosmeticButton(
    private val parentGui: InventorioScreen,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
) : TexturedButtonWidget(x, y, width, height, CosmeticButton.OFF, {
    (parentGui.screenHandler as ICuriosContainer).`inventorio$toggleCosmetics`()
    InventorioNetworking.INSTANCE.sendToServer(CPacketToggleCosmetics(parentGui.screenHandler.syncId))
}) {
    init {
        tooltip = Tooltip.of(Text.translatable("gui.curios.toggle.cosmetics"))
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val sprites = when ((parentGui.screenHandler as ICuriosContainer).`inventorio$isViewingCosmetics`) {
            true -> CosmeticButton.ON
            false -> CosmeticButton.OFF
        }
        x = parentGui.guiLeft - 27
        y = parentGui.guiTop - 18
        val identifier = sprites.get(isNarratable, isSelected)
        context.drawGuiTexture(identifier, x, y, width, height)
    }
}
