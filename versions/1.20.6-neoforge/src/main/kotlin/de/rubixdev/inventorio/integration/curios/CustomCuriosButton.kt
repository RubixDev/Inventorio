package de.rubixdev.inventorio.integration.curios

import de.rubixdev.inventorio.player.PlayerInventoryAddon
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ButtonTextures
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.TexturedButtonWidget
import top.theillusivec4.curios.client.gui.CuriosScreen

class CustomCuriosButton(
    private val parentGui: HandledScreen<*>,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    textures: ButtonTextures?,
    pressAction: PressAction?,
) : TexturedButtonWidget(x, y, width, height, textures, pressAction) {
    override fun renderWidget(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        val offsets = CuriosScreen.getButtonOffset(false)
        x = parentGui.guiLeft + offsets.left + 2
        y = parentGui.height / 2 + offsets.right + 2 - 9 * (PlayerInventoryAddon.Client.local?.getDeepPocketsRowCount() ?: 0)
        super.renderWidget(context, mouseX, mouseY, delta)
    }
}
