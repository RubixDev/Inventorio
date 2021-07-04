package me.lizardofoz.inventorio.client.ui

import com.mojang.blaze3d.systems.RenderSystem
import me.lizardofoz.inventorio.config.PlayerSettings
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Arm
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.world.GameMode

@Environment(EnvType.CLIENT)
object HotbarHUDRenderer
{
    private val WIDGETS_TEXTURE = Identifier("inventorio", "textures/gui/widgets.png")
    private val client = MinecraftClient.getInstance()!!

    fun renderSegmentedHotbar(matrices: MatrixStack): Boolean
    {
        if (PlayerSettings.segmentedHotbar.value == SegmentedHotbar.OFF || isHidden())
            return false

        val playerEntity = client.cameraEntity as? PlayerEntity ?: return false
        val inventory = playerEntity.inventory
        val scaledWidthHalved = client.window.scaledWidth / 2 - 30
        val scaledHeight = client.window.scaledHeight

        client.textureManager.bindTexture(WIDGETS_TEXTURE)
        //Draw the hotbar itself
        DrawableHelper.drawTexture(matrices,
                scaledWidthHalved - HUD_SEGMENTED_HOTBAR.x,
                scaledHeight - HUD_SEGMENTED_HOTBAR.y,
                CANVAS_SEGMENTED_HOTBAR.x,
                CANVAS_SEGMENTED_HOTBAR.y,
                HUD_SEGMENTED_HOTBAR.width,
                HUD_SEGMENTED_HOTBAR.height,
                CANVAS_WIDGETS_TEXTURE_SIZE.x, CANVAS_WIDGETS_TEXTURE_SIZE.y)

        val selectedSection = PlayerInventoryAddon.Client.selectedHotbarSection
        if (selectedSection == -1) //Draw the regular vanilla selection box
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalved - HUD_SECTION_SELECTION.x - HUD_SEGMENTED_HOTBAR_GAP
                            + (inventory.selectedSlot * SLOT_HOTBAR_SIZE.width) + (HUD_SEGMENTED_HOTBAR_GAP * (inventory.selectedSlot / 3)),
                    scaledHeight - HUD_SECTION_SELECTION.y,
                    CANVAS_VANILLA_SELECTION_FRAME_POS.x,
                    CANVAS_VANILLA_SELECTION_FRAME_POS.y,
                    CANVAS_VANILLA_SELECTION_FRAME_SIZE.width,
                    CANVAS_VANILLA_SELECTION_FRAME_SIZE.height,
                    CANVAS_WIDGETS_TEXTURE_SIZE.x, CANVAS_WIDGETS_TEXTURE_SIZE.y)
        else //Draw the section-wide selection box
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalved - HUD_SECTION_SELECTION.x - HUD_SEGMENTED_HOTBAR_GAP
                            + (HUD_SECTION_SELECTION.width * selectedSection),
                    scaledHeight - HUD_SECTION_SELECTION.y,
                    CANVAS_SECTION_SELECTION_FRAME.x,
                    CANVAS_SECTION_SELECTION_FRAME.y,
                    HUD_SECTION_SELECTION.width,
                    HUD_SECTION_SELECTION.height,
                    CANVAS_WIDGETS_TEXTURE_SIZE.x, CANVAS_WIDGETS_TEXTURE_SIZE.y)

        //Draw hotbar items
        for (slotNum in 0 until VANILLA_ROW_LENGTH)
        {
            val x = scaledWidthHalved - HUD_SECTION_SELECTION.x + (slotNum * SLOT_HOTBAR_SIZE.width) + (HUD_SEGMENTED_HOTBAR_GAP * (slotNum / 3))
            val y = scaledHeight - SLOT_HOTBAR_SIZE.height
            val itemStack = inventory.getStack(slotNum)
            client.itemRenderer.renderInGuiWithOverrides(playerEntity, itemStack, x, y)
            client.itemRenderer.renderGuiItemOverlay(client.textRenderer, itemStack, x, y)
        }
        return true
    }

    private fun isHidden(): Boolean
    {
        if (client.interactionManager == null
            || client.interactionManager?.currentGameMode == GameMode.SPECTATOR
            || client.options.hudHidden)
            return true

        val player = client.cameraEntity as? PlayerEntity
        return player == null || !player.isAlive || player.playerScreenHandler == null
    }

    @Suppress("DEPRECATION")
    fun renderHotbarAddons(matrices: MatrixStack)
    {
        if (isHidden())
            return

        val inventoryAddon = PlayerInventoryAddon.Client.local ?: return
        val player = inventoryAddon.player

        val utilBeltDisplay = inventoryAddon.getDisplayedUtilities()
        val selectedHotbarItem = inventoryAddon.getSelectedHotbarStack()

        val scaledWidthHalved = client.window.scaledWidth / 2 - 30
        val scaledHeight = client.window.scaledHeight

        val segmentedHotbarMode = PlayerSettings.segmentedHotbar.value != SegmentedHotbar.OFF
        val segmentedModeOffset = if (segmentedHotbarMode) HUD_SEGMENTED_HOTBAR_GAP else 0

        var rightHanded = player.mainArm == Arm.RIGHT
        if (inventoryAddon.swappedHands)
            rightHanded = !rightHanded

        val leftHandedUtilityBeltOffset = if (rightHanded) 0 else (LEFT_HANDED_UTILITY_BELT_OFFSET + segmentedModeOffset * 2)
        val leftHandedDisplayToolOffset = if (rightHanded) 0 else (LEFT_HANDED_DISPLAY_TOOL_OFFSET - segmentedModeOffset * 2)

        client.textureManager.bindTexture(WIDGETS_TEXTURE)

        //Draw the frame of a tool currently in use (one on the opposite side from the offhand)

        RenderSystem.disableDepthTest()
        RenderSystem.enableBlend()
        if (inventoryAddon.displayTool.isNotEmpty && inventoryAddon.displayTool != selectedHotbarItem)
        {
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalved + leftHandedDisplayToolOffset + HUD_ACTIVE_TOOL_FRAME.x + segmentedModeOffset,
                    scaledHeight - HUD_ACTIVE_TOOL_FRAME.y,
                    CANVAS_ACTIVE_TOOL_FRAME.x,
                    CANVAS_ACTIVE_TOOL_FRAME.y,
                    HUD_ACTIVE_TOOL_FRAME.width,
                    HUD_ACTIVE_TOOL_FRAME.height,
                    CANVAS_WIDGETS_TEXTURE_SIZE.x, CANVAS_WIDGETS_TEXTURE_SIZE.y)
        }

        //Draw utility belt (both the frame and the items)
        if (!PlayerSettings.skipEmptyUtilitySlots.boolValue || utilBeltDisplay.any { it.isNotEmpty })
        {
            //Draw the semi-transparent background (needed to paint next and prev utility belt items dimmed,
            //   while keeping the resulting slot opacity akin to other hotbar slots)
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalved + leftHandedUtilityBeltOffset - HUD_UTILITY_BELT.x - segmentedModeOffset,
                    scaledHeight - HUD_UTILITY_BELT.y,
                    CANVAS_UTILITY_BELT_BCG.x,
                    CANVAS_UTILITY_BELT_BCG.y,
                    HUD_UTILITY_BELT.width,
                    HUD_UTILITY_BELT.height,
                    CANVAS_WIDGETS_TEXTURE_SIZE.x, CANVAS_WIDGETS_TEXTURE_SIZE.y)

            //Next and prev items are drawn at 80% scale.
            //This isn't how Minecraft usually does things, but what's possible to do within a mod is limited
            RenderSystem.pushMatrix()
            RenderSystem.scalef(0.8f, 0.8f, 0.8f)

            //Draw next and prev utility belt items
            client.itemRenderer.renderInGuiWithOverrides(player,
                    utilBeltDisplay[0],
                    (scaledWidthHalved + leftHandedUtilityBeltOffset - segmentedModeOffset) * 10 / 8 - SLOT_UTILITY_BELT_1.x,
                    MathHelper.ceil((scaledHeight - SLOT_UTILITY_BELT_1.y) / 0.8))

            client.itemRenderer.renderInGuiWithOverrides(player,
                    utilBeltDisplay[2],
                    (scaledWidthHalved + leftHandedUtilityBeltOffset - segmentedModeOffset) * 10 / 8 - SLOT_UTILITY_BELT_2.x,
                    MathHelper.ceil((scaledHeight - SLOT_UTILITY_BELT_2.y) / 0.8))

            //Reverse the scaling. Also, rendering an item disables the blending for some reason and changes the binded texture
            RenderSystem.popMatrix()
            RenderSystem.disableDepthTest()
            RenderSystem.enableBlend()
            client.textureManager.bindTexture(WIDGETS_TEXTURE)

            //Draw the utility belt frame
            DrawableHelper.drawTexture(matrices,
                    scaledWidthHalved + leftHandedUtilityBeltOffset - HUD_UTILITY_BELT.x - segmentedModeOffset,
                    scaledHeight - HUD_UTILITY_BELT.y,
                    CANVAS_UTILITY_BELT.x,
                    CANVAS_UTILITY_BELT.y,
                    HUD_UTILITY_BELT.width,
                    HUD_UTILITY_BELT.height,
                    CANVAS_WIDGETS_TEXTURE_SIZE.x, CANVAS_WIDGETS_TEXTURE_SIZE.y)

            //Draw the active utility item
            renderItem(player,
                    utilBeltDisplay[1],
                    scaledWidthHalved + leftHandedUtilityBeltOffset - SLOT_UTILITY_BELT_3.x - segmentedModeOffset,
                    scaledHeight - SLOT_UTILITY_BELT_3.y)
        }

        //Draw the active tool item itself
        if (inventoryAddon.displayTool.isNotEmpty && inventoryAddon.displayTool != selectedHotbarItem)
        {
            renderItem(
                    player,
                inventoryAddon.displayTool,
                    scaledWidthHalved + leftHandedDisplayToolOffset + SLOT_ACTIVE_TOOL_FRAME.x + segmentedModeOffset,
                    scaledHeight - SLOT_ACTIVE_TOOL_FRAME.y,
            )
        }
        RenderSystem.enableBlend()
    }

    private fun renderItem(player: PlayerEntity, stack: ItemStack, x: Int, y: Int)
    {
        if (stack.isNotEmpty)
        {
            client.itemRenderer.renderInGuiWithOverrides(player, stack, x, y)
            client.itemRenderer.renderGuiItemOverlay(client.textRenderer, stack, x, y)
        }
    }
}