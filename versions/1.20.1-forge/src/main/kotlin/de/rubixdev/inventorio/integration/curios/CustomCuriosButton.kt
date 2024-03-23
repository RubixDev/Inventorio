package de.rubixdev.inventorio.integration.curios

import de.rubixdev.inventorio.player.PlayerInventoryAddon
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.util.Identifier
import top.theillusivec4.curios.client.gui.CuriosScreen

class CustomCuriosButton(
    private val parentGui: HandledScreen<*>,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    textureOffsetX: Int,
    textureOffsetY: Int,
    yDiffText: Int,
    texture: Identifier,
    pressAction: PressAction?,
) : TexturedButtonWidget(x, y, width, height, textureOffsetX, textureOffsetY, yDiffText, texture, pressAction) {
    override fun renderButton(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        val offsets = CuriosScreen.getButtonOffset(false)
        x = parentGui.guiLeft + offsets.left
        y = parentGui.height / 2 + offsets.right - 9 * (PlayerInventoryAddon.Client.local?.getDeepPocketsRowCount() ?: 0)
        super.renderButton(context, mouseX, mouseY, delta)
    }
}
