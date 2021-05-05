package me.lizardofoz.inventorio.client.quickbar

import com.mojang.blaze3d.systems.RenderSystem
import me.lizardofoz.inventorio.client.config.InventorioConfigData
import me.lizardofoz.inventorio.mixin.client.accessor.InGameHudAccessor
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

@Environment(EnvType.CLIENT)
object QuickBarHUDRenderer
{
    private val WIDGETS_TEXTURE = Identifier("inventorio", "textures/gui/widgets.png")
    private val client = MinecraftClient.getInstance()!!

    fun renderHotbarItself(hud: InGameHud, tickDelta: Float, matrices: MatrixStack)
    {
        if (InventorioConfigData.config().quickBarSimplified == QuickBarSimplified.OFF)
        {
            (hud as InGameHudAccessor).renderHotBar(tickDelta, matrices)
            return
        }
    }

    fun renderHotbarAddons(hud: InGameHud, tickDelta: Float, matrices: MatrixStack)
    {
        val playerAddon = PlayerAddon.Client.local
        val player = playerAddon.player

        val utilBelt = playerAddon.inventoryAddon.getDisplayedUtilities()
        val activeTool = playerAddon.inventoryAddon.mainHandDisplayTool
        val selectedItem = player.inventory.getStack(player.inventory.selectedSlot)

        val scaledWidthHalved = client.window.scaledWidth / 2 - 30
        val scaledHeight = client.window.scaledHeight

        val simplifiedQuickBarMode = InventorioConfigData.config().quickBarSimplified != QuickBarSimplified.OFF
        val simplifiedModeOffset = if (simplifiedQuickBarMode) 6 else 0

        client.textureManager.bindTexture(WIDGETS_TEXTURE)

        //Draw the active tool frame
        if (player.handSwinging && activeTool.isNotEmpty && activeTool != selectedItem)
        {
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalved + HUD_ACTIVE_TOOL_FRAME.x + simplifiedModeOffset, scaledHeight - HUD_ACTIVE_TOOL_FRAME.y,
                    CANVAS_ACTIVE_TOOL_FRAME.x, CANVAS_ACTIVE_TOOL_FRAME.y,
                    HUD_ACTIVE_TOOL_FRAME.width, HUD_ACTIVE_TOOL_FRAME.height,
                    256, 64)
        }

        //Draw utility belt
        if (utilBelt.any { it.isNotEmpty })
        {
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalved - HUD_UTILITY_BELT.x - simplifiedModeOffset, scaledHeight - HUD_UTILITY_BELT.y,
                    CANVAS_UTILITY_BELT_BCG.x, CANVAS_UTILITY_BELT_BCG.y,
                    HUD_UTILITY_BELT.width, HUD_UTILITY_BELT.height,
                    256, 64)

            RenderSystem.pushMatrix()
            RenderSystem.scalef(0.8f, 0.8f, 0.8f)

            //Draw next and prev items
            client.itemRenderer.renderInGuiWithOverrides(player,
                    utilBelt[0],
                    (scaledWidthHalved - simplifiedModeOffset) * 10 / 8 - SLOT_UTILITY_BELT_1.x,
                    MathHelper.ceil((scaledHeight - SLOT_UTILITY_BELT_1.y) / 0.8))

            client.itemRenderer.renderInGuiWithOverrides(player,
                    utilBelt[2],
                    (scaledWidthHalved - simplifiedModeOffset) * 10 / 8 - SLOT_UTILITY_BELT_2.x,
                    MathHelper.ceil((scaledHeight - SLOT_UTILITY_BELT_2.y) / 0.8))

            RenderSystem.popMatrix()
            RenderSystem.disableDepthTest()
            RenderSystem.enableBlend()

            client.textureManager.bindTexture(WIDGETS_TEXTURE)
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalved - HUD_UTILITY_BELT.x - simplifiedModeOffset, scaledHeight - HUD_UTILITY_BELT.y,
                    CANVAS_UTILITY_BELT.x, CANVAS_UTILITY_BELT.y,
                    HUD_UTILITY_BELT.width, HUD_UTILITY_BELT.height,
                    256, 64)

            //Draw the active utility item
            renderOffHandItem(player,
                    utilBelt[1],
                    scaledWidthHalved - SLOT_UTILITY_BELT_3.x - simplifiedModeOffset,
                    scaledHeight - SLOT_UTILITY_BELT_3.y)

        }

        //Draw the active tool
        if (player.handSwinging && activeTool.isNotEmpty && activeTool != selectedItem)
        {
            renderOffHandItem(
                    player,
                    activeTool,
                    scaledWidthHalved + SLOT_ACTIVE_TOOL_FRAME.x + simplifiedModeOffset,
                    scaledHeight - SLOT_ACTIVE_TOOL_FRAME.y,
            )
        }
        RenderSystem.enableBlend()
    }

    /*fun renderHotbar(hud: InGameHud, tickDelta: Float, matrices: MatrixStack)
    {
        val playerAddon = PlayerAddon.Client.local
        val player = playerAddon.player

        val simplifiedQuickBarMode = InventorioConfigData.config().quickBarSimplified != QuickBarSimplified.OFF
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
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalfed - 91, scaledHeight - 22,
                    CANVAS_QUICK_BAR_START_X, CANVAS_QUICK_BAR_START_Y,
                    CANVAS_QUICK_BAR_WIDTH, 22)
        else
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalfed - 97, scaledHeight - 22,
                    CANVAS_QUICK_BAR_SPLIT_START_X, CANVAS_QUICK_BAR_SPLIT_START_Y,
                    CANVAS_QUICK_BAR_SPLIT_WIDTH, 22)

        //Draw the selected slot frame (or selected section in a simplified mode)
        if (selectedQuickBarSection == -1)
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalfed - 92 - splitOffset + player.inventory.selectedSlot * 20 + groupOffset * (player.inventory.selectedSlot / 4),
                    scaledHeight - 23,
                    0, 22,
                    24, 24)
        else
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalfed - 92 - splitOffset + 28 * selectedQuickBarSection * 3,
                    scaledHeight - 23,
                    CANVAS_QUICK_BAR_SECSEL_X, CANVAS_QUICK_BAR_SECSEL_Y,
                    CANVAS_QUICK_BAR_SECSEL_W, CANVAS_QUICK_BAR_SECSEL_H,
            256, 64)

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
        if (handler.slots.size < 85)
            return
        //Draw quickbar items
        for (i in 0 until VANILLA_ROW_LENGTH)
        {
            val x = scaledWidthHalfed - 90 + i * 20 + 2 + groupOffset * (i / 4) - splitOffset
            val y = scaledHeight - 16 - 3
            val slot = handler.getSlot(i)
            renderPhysBarItem(x, y, tickDelta, player, slot.stack)
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
                    DrawableHelper.drawTexture(matrices, scaledWidthHalfed - 91 - 29 - 25 - splitOffset, scaledHeight - 22, 0f, 68f, 48, 22,256,64)
                else
                    DrawableHelper.drawTexture(matrices, scaledWidthHalfed + 151 + splitOffset, scaledHeight - 23, 53f, 22f, 29, 24,256,64)
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
    }*/

    fun renderOffHandItem(player: PlayerEntity, stack: ItemStack, x: Int, y: Int)
    {
        if (stack.isNotEmpty)
        {
            client.itemRenderer.renderInGuiWithOverrides(player, stack, x, y)
            client.itemRenderer.renderGuiItemOverlay(client.textRenderer, stack, x, y)
        }
    }

    fun renderPhysBarItem(x: Int, y: Int, tickDelta: Float, player: PlayerEntity, stack: ItemStack)
    {
        client.itemRenderer.renderInGuiWithOverrides(player, stack, x, y)
        client.itemRenderer.renderGuiItemOverlay(client.textRenderer, stack, x, y)
    }
}