package me.danetnaverno.inventorio.client.quickbar

import com.mojang.blaze3d.systems.RenderSystem
import me.danetnaverno.inventorio.client.config.InventorioConfigData
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.player.PlayerAddon.Client.selectedQuickBarSection
import me.danetnaverno.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.hud.BackgroundHelper
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.options.AttackIndicator
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Arm
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import kotlin.math.max
import kotlin.math.pow

@Environment(EnvType.CLIENT)
object QuickBarRenderer
{
    private val WIDGETS_TEXTURE = Identifier("inventorio", "textures/gui/widgets.png")
    private val client = MinecraftClient.getInstance()!!

    fun renderQuickBar(hud: InGameHud, tickDelta: Float, matrices: MatrixStack)
    {
        val playerAddon = PlayerAddon.Client.local
        val player = playerAddon.player

        val simplifiedQuickBarMode = InventorioConfigData.config().quickBarSimplifiedGlobal != QuickBarSimplified.OFF
        val utilBelt = playerAddon.inventoryAddon.getDisplayedUtilities()
        val activeTool = playerAddon.inventoryAddon.mainHandDisplayTool

        val arm = player.mainArm.opposite
        val scaledWidthHalfed = client.window.scaledWidth / 2 - 30
        val scaledHeight = client.window.scaledHeight

        val storedZOffset = hud.zOffset
        hud.zOffset = -90

        val splitOffset = if (simplifiedQuickBarMode) 6 else 0
        val groupOffset = if (simplifiedQuickBarMode) 4 else 0

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        client.textureManager.bindTexture(WIDGETS_TEXTURE)

        //Draw the quickbar itself
        if (!simplifiedQuickBarMode)
            hud.drawTexture(matrices,
                    scaledWidthHalfed - 91, scaledHeight - 22,
                    canvas_quickBarStartX, canvas_quickBarStartY,
                    canvas_quickBarWidth, 22)
        else
            hud.drawTexture(matrices,
                    scaledWidthHalfed - 97, scaledHeight - 22,
                    canvas_splitQuickBarStartX, canvas_splitQuickBarStartY,
                    canvas_splitQuickBarWidth, 22)

        //Draw the selected slot frame (or selected section in a simplified mode)
        if (selectedQuickBarSection == -1)
            hud.drawTexture(matrices,
                    scaledWidthHalfed - 92 - splitOffset + player.inventory.selectedSlot * 20 + groupOffset * (player.inventory.selectedSlot / 4),
                    scaledHeight - 23,
                    0, 22,
                    24, 24)
        else
            hud.drawTexture(matrices,
                    scaledWidthHalfed - 92 - splitOffset + 28 * selectedQuickBarSection * 3,
                    scaledHeight - 23,
                    canvas_qbSectionSelectionX, canvas_qbSectionSelectionY,
                    canvas_qbSectionSelectionW, canvas_qbSectionSelectionH)

        //Draw the frame for an active tool
        if (player.handSwinging && activeTool.isNotEmpty)
        {
            if (arm == Arm.LEFT)
                hud.drawTexture(matrices, scaledWidthHalfed + 151 + splitOffset, scaledHeight - 23, 53, 22, 29, 24)
            else
                hud.drawTexture(matrices, scaledWidthHalfed - 91 - 29 - splitOffset, scaledHeight - 23, 24, 22, 29, 24)
        }

        hud.zOffset = storedZOffset
        RenderSystem.enableRescaleNormal()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val handler = player.playerScreenHandler
        //Draw quickbar items
        for(i in 0 until 12)
        {
            val x = scaledWidthHalfed - 90 + i * 20 + 2 + groupOffset * (i / 4) - splitOffset
            val y = scaledHeight - 16 - 3
            val physicalSlot = handler.getSlot(quickBarPhysicalSlotsRange.first + i)
            if (physicalSlot.stack.isNotEmpty)
                renderPhysBarItem(x, y, tickDelta, player, physicalSlot.stack)
            else
                renderQuickBarItem(x, y, tickDelta, player, handler.getSlot(quickBarShortcutSlotsRange.first + i).stack, true)
        }

        //Draw utility belt
        if (utilBelt.any { it.isNotEmpty })
        {
            val p = scaledHeight - 16 - 3
            val p2 = MathHelper.ceil((scaledHeight - 18) / 0.8)
            if (arm == Arm.LEFT)
            {
                RenderSystem.pushMatrix()
                RenderSystem.scalef(0.8f, 0.8f, 0.8f)

                //Draw next and prev items
                client.itemRenderer.renderInGuiWithOverrides(player, utilBelt[0], (scaledWidthHalfed - splitOffset) * 10 / 8 - 177, p2)
                client.itemRenderer.renderInGuiWithOverrides(player, utilBelt[2], (scaledWidthHalfed - splitOffset) * 10 / 8 - 141, p2)

                RenderSystem.popMatrix()
                RenderSystem.enableBlend()
                RenderSystem.enableDepthTest()

                //Draw the frame of the utility hand
                client.textureManager.bindTexture(WIDGETS_TEXTURE)
                hud.zOffset = 210
                if (arm == Arm.LEFT)
                    hud.drawTexture(matrices, scaledWidthHalfed - 91 - 29 - 25 - splitOffset, scaledHeight - 22, 0, 68, 48, 22)
                else
                    hud.drawTexture(matrices, scaledWidthHalfed + 151 + splitOffset, scaledHeight - 23, 53, 22, 29, 24)
                hud.zOffset = -90

                //Draw the active utility item
                client.itemRenderer.zOffset += 90f
                renderOffHandItem(scaledWidthHalfed - 91 - 38 - splitOffset, p, tickDelta, player, utilBelt[1])
                client.itemRenderer.zOffset -= 90f
            }
            else //todo
                renderOffHandItem(scaledWidthHalfed + 91 + 10 + splitOffset, p, tickDelta, player, utilBelt[1])
        }

        //Draw the active tool
        if (player.handSwinging && activeTool.isNotEmpty)
        {
            val p = scaledHeight - 16 - 3
            if (arm == Arm.LEFT)
                renderOffHandItem(scaledWidthHalfed + 91 + 70 + splitOffset, p, tickDelta, player, activeTool)
            else
                renderOffHandItem(scaledWidthHalfed - 91 - 26 - splitOffset, p, tickDelta, player, activeTool)
        }

        //Draw the attack indicator
        if (client.options.attackIndicator == AttackIndicator.HOTBAR)
        {
            val f = client.player?.getAttackCooldownProgress(0.0f) ?: 0f
            if (f < 1.0f)
            {
                val q = scaledHeight - 20
                val r = if (arm == Arm.LEFT)
                    scaledWidthHalfed + 91 + 6
                else
                    scaledWidthHalfed - 91 - 22
                client.textureManager.bindTexture(DrawableHelper.GUI_ICONS_TEXTURE)
                val s = (f * 19.0f).toInt()
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
                hud.drawTexture(matrices, r, q, 0, 94, 18, 18)
                hud.drawTexture(matrices, r, q + 18 - s, 18, 112 - s, 18, s)
            }
        }

        RenderSystem.disableRescaleNormal()
        RenderSystem.disableBlend()
    }

    fun renderOffHandItem(x: Int, y: Int, tickDelta: Float, player: PlayerEntity, stack: ItemStack)
    {
        if (stack.isNotEmpty)
        {
            client.itemRenderer.renderInGuiWithOverrides(player, stack, x, y)
            RenderSystem.enableBlend()
            client.itemRenderer.renderGuiItemOverlay(client.textRenderer, stack, x, y)
            RenderSystem.disableBlend()
        }
    }

    fun renderPhysBarItem(x: Int, y: Int, tickDelta: Float, player: PlayerEntity, stack: ItemStack)
    {
        client.itemRenderer.renderInGuiWithOverrides(player, stack, x, y)
        client.itemRenderer.renderGuiItemOverlay(client.textRenderer, stack, x, y)
    }

    fun renderQuickBarItem(x: Int, y: Int, tickDelta: Float, player: PlayerEntity, stack: ItemStack, sumTotalAmount: Boolean)
    {
        if (stack.isNotEmpty)
        {
            client.itemRenderer.renderInGuiWithOverrides(player, stack, x, y)
            if (!MathStuffConstants.canPlayerStoreItemStackPhysicallyInQuickBar(player, stack))
            {
                RenderSystem.enableBlend()
                renderGuiItemOverlay(player, client.textRenderer, stack, x, y, sumTotalAmount)
                RenderSystem.disableBlend()
            }
        }
    }

    fun renderGuiItemOverlay(player: PlayerEntity, renderer: TextRenderer, stack: ItemStack, x: Int, y: Int, sumTotalAmount: Boolean)
    {
        val totalAmount = if (sumTotalAmount)
            PlayerAddon.Client.local.inventoryAddon.getTotalAmount(stack)
        else
            stack.count

        val matrixStack = MatrixStack()
        matrixStack.push()
        if (totalAmount != 0)
        {
            val string = totalAmount.toString()

            val scaleFactor = 1.0f - (((max(2, string.length) - 2.0) / string.length).pow(1.8)).toFloat()
            matrixStack.translate((x + 19 - 2 - renderer.getWidth(string) * scaleFactor).toDouble(), (y + 6 + 3 / scaleFactor).toDouble(), 200.0)
            matrixStack.scale(scaleFactor, scaleFactor, scaleFactor)

            val color =
                    if (totalAmount == 0)
                        BackgroundHelper.ColorMixer.getArgb(255, 255, 0, 0)
                    else
                        16777215

            val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
            renderer.draw(string, 0f, 0f, color, true, matrixStack.peek().model, immediate, false, 0, 15728880)
            immediate.draw()
        }
        else if (!player.isCreative)
        {
            matrixStack.translate(0.0, 0.0, 200.0)
            DrawableHelper.fill(matrixStack, x, y, x + 16, y + 16, BackgroundHelper.ColorMixer.getArgb(140, 64, 64, 64))
        }

        val cooldown = player.itemCooldownManager.getCooldownProgress(stack.item, MinecraftClient.getInstance().tickDelta)
        if (cooldown > 0.0f)
        {
            matrixStack.pop()
            DrawableHelper.fill(matrixStack, x, y + 16, x + 16, y + 16 - MathHelper.ceil(16.0f * cooldown), BackgroundHelper.ColorMixer.getArgb(127, 255, 255, 255))
        }
    }
}