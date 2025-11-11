package de.rubixdev.inventorio.integration.curios

import com.mojang.blaze3d.systems.RenderSystem
import de.rubixdev.inventorio.client.ui.InventorioScreen
import de.rubixdev.inventorio.config.PlayerSettings
import de.rubixdev.inventorio.mixin.client.accessor.HandledScreenAccessor
import de.rubixdev.inventorio.packet.InventorioNetworking
import de.rubixdev.inventorio.util.MixinDelegate
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget
import net.minecraft.client.gui.widget.TexturedButtonWidget
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.client.gui.CuriosButton
import top.theillusivec4.curios.client.gui.CuriosScreen
import top.theillusivec4.curios.client.gui.PageButton
import top.theillusivec4.curios.client.gui.RenderButton
import top.theillusivec4.curios.common.inventory.CosmeticCurioSlot
import top.theillusivec4.curios.common.inventory.CurioSlot
import top.theillusivec4.curios.common.network.client.CPacketPage
import top.theillusivec4.curios.common.network.client.CPacketToggleRender

/**
 * This is basically a re-implementation of https://github.com/TheIllusiveC4/Curios/blob/ab847aab52213afd87c78f48ad9382212846f1b7/neoforge/src/main/java/top/theillusivec4/curios/client/gui/CuriosScreen.java
 * for the Inventorio screen (and in Kotlin).
 */
@Suppress("FunctionName")
class InventorioScreenMixinHelper(
    private val thiz: InventorioScreen,
    private val recipeBook: RecipeBookWidget,
) {
    companion object {
        val CURIO_INVENTORY = Identifier("curios", "textures/gui/curios/inventory.png")

        private var scrollCooldown = 0
    }

    var isCuriosOpen = false
        private set

    private lateinit var buttonCurios: TexturedButtonWidget
    private lateinit var cosmeticButton: CustomCosmeticButton
    private lateinit var nextPage: CustomPageButton
    private lateinit var prevPage: CustomPageButton
    private var wasRecipeBookOpen = mutableListOf(false)
    private var wasCuriosOpen = mutableListOf(false)
    private var buttonClicked = false
    private var isRenderButtonHovered = false
    private var panelWidth = 0

    private val handler get() = thiss.handler
    private val curioHandler get() = handler as ICuriosContainer

    @Suppress("CAST_NEVER_SUCCEEDS")
    private val thiss = thiz as HandledScreenAccessor<*>

    ///// private members of the mixin target class ////
    private var x by MixinDelegate(thiss::getX, thiss::setX)
    private var y by MixinDelegate(thiss::getY, thiss::setY)

    fun InventorioScreen.`curios$init`() {
        thiss.client?.also { client ->
            panelWidth = curioHandler.`inventorio$panelWidth`

            val offsets = CuriosScreen.getButtonOffset(false)
            buttonCurios = CustomCuriosButton(
                thiz,
                guiLeft + offsets.left - 2,
                height / 2 + offsets.right + 2,
                10,
                10,
                CuriosButton.BIG,
            ) {
                toggleCurios()
            }
            thiss.callAddDrawableChild(buttonCurios)

            `curios$updateRenderButtons`()

            if (PlayerSettings.curiosOpenByDefault.boolValue) {
                toggleCurios()
            }
        }
    }

    private fun InventorioScreen.toggleCurios() {
        isCuriosOpen = !isCuriosOpen
        if (isCuriosOpen) wasRecipeBookOpen.add(recipeBook.isOpen)
        if (isCuriosOpen && recipeBook.isOpen) {
            // close the recipe book when opening the curios stuff, so we don't have to deal with that
            recipeBook.toggleOpen()
        } else if (!isCuriosOpen && wasRecipeBookOpen.last()) {
            // re-open the recipe book if it was open before
            recipeBook.toggleOpen()
            wasRecipeBookOpen.removeLast()
        }
        updateScreenPosition()
        `curios$updateRenderButtons`()
    }

    fun InventorioScreen.`curios$hideCuriosWhenOpeningRecipeBook`() {
        if (recipeBook.isOpen) wasCuriosOpen.add(isCuriosOpen)
        if (recipeBook.isOpen && isCuriosOpen) {
            // close curios when recipe book is being opened
            isCuriosOpen = false
        } else if (!recipeBook.isOpen && wasCuriosOpen.last()) {
            // re-open curios if it was open before
            isCuriosOpen = true
            wasCuriosOpen.removeLast()
        }
        updateScreenPosition()
        `curios$updateRenderButtons`()
    }

    fun InventorioScreen.`curios$updateRenderButtons`() {
        thiss.selectables.removeIf { it is RenderButton || it is CustomCosmeticButton || it is CustomPageButton }
        thiss.children.removeIf { it is RenderButton || it is CustomCosmeticButton || it is CustomPageButton }
        drawables.removeIf { it is RenderButton || it is CustomCosmeticButton || it is CustomPageButton }
        panelWidth = curioHandler.`inventorio$panelWidth`

        if (!isCuriosOpen) return

        if (curioHandler.`inventorio$hasCosmetics`) {
            cosmeticButton = CustomCosmeticButton(thiz, guiLeft + 17, guiTop - 18, 20, 17)
            thiss.callAddDrawableChild(cosmeticButton)
        }

        if (curioHandler.`inventorio$totalPages` > 1) {
            nextPage = CustomPageButton(thiz, guiLeft + 17, guiTop + 2, 11, 12, PageButton.Type.NEXT)
            thiss.callAddDrawableChild(nextPage)
            prevPage = CustomPageButton(thiz, guiLeft + 17, guiTop + 2, 11, 12, PageButton.Type.PREVIOUS)
            thiss.callAddDrawableChild(prevPage)
        }

        for (inventorySlot in handler.slots) {
            if (inventorySlot is CurioSlot && inventorySlot !is CosmeticCurioSlot) {
                if (inventorySlot.canToggleRender()) {
                    thiss.callAddDrawableChild(
                        RenderButton(
                            inventorySlot,
                            x + inventorySlot.x + 12,
                            y + inventorySlot.y - 1,
                            8,
                            8,
                            75,
                            0,
                            CURIO_INVENTORY,
                        ) {
                            InventorioNetworking.INSTANCE.sendToServer(CPacketToggleRender(inventorySlot.identifier, inventorySlot.slotIndex))
                        },
                    )
                }
            }
        }
    }

    fun InventorioScreen.`curios$render`(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        var isButtonHovered = false
        for (button in drawables.filterIsInstance<RenderButton>()) {
            button.renderButtonOverlay(context, mouseX, mouseY, delta)
            if (button.isHovered) isButtonHovered = true
        }
        isRenderButtonHovered = isButtonHovered
        val clientPlayer = MinecraftClient.getInstance().player
        if (!isRenderButtonHovered && clientPlayer != null && clientPlayer.playerScreenHandler.cursorStack.isEmpty) {
            slotUnderMouse?.let { slot ->
                if (slot is CurioSlot && !slot.hasStack()) {
                    context.drawTooltip(thiss.textRenderer, Text.literal(slot.slotName), mouseX, mouseY)
                }
            }
        }
    }

    fun drawMouseoverTooltip(context: DrawContext, x: Int, y: Int) {
        thiss.client?.player?.let { player ->
            if (player.playerScreenHandler.cursorStack.isEmpty && isRenderButtonHovered) {
                context.drawTooltip(thiss.textRenderer, Text.translatable("gui.curios.toggle"), x, y)
            }
        }
    }

    fun InventorioScreen.`curios$drawBackground`(drawContext: DrawContext) {
        if (!isCuriosOpen) return
        thiss.client?.player?.let { player ->
            if (scrollCooldown > 0 && player.age % 5 == 0) scrollCooldown--
            panelWidth = curioHandler.`inventorio$panelWidth`
            val i = x
            val j = y
            CuriosApi.getCuriosInventory(player).ifPresent {
                var xOffset = -33
                var yOffset = j
                val pageOffset = curioHandler.`inventorio$totalPages` > 1

                if (curioHandler.`inventorio$hasCosmetics`) {
                    drawContext.drawTexture(
                        CURIO_INVENTORY,
                        i + xOffset + 2,
                        yOffset - 23,
                        32,
                        0,
                        28,
                        24,
                    )
                }
                val grid = curioHandler.`inventorio$grid`
                xOffset -= (grid.size - 1) * 18

                // render backplate
                for (r in 0..<grid.size) {
                    val rows = grid.first()
                    var upperHeight = 7 + rows * 18
                    var xTexOffset = 91

                    if (pageOffset) {
                        upperHeight += 8
                    }

                    if (r != 0) {
                        xTexOffset += 7
                    }
                    drawContext.drawTexture(
                        CURIO_INVENTORY,
                        i + xOffset,
                        yOffset,
                        xTexOffset,
                        0,
                        25,
                        upperHeight,
                    )
                    drawContext.drawTexture(
                        CURIO_INVENTORY,
                        i + xOffset,
                        yOffset + upperHeight,
                        xTexOffset,
                        159,
                        25,
                        7,
                    )

                    if (grid.size == 1) {
                        xTexOffset += 7
                        drawContext.drawTexture(
                            CURIO_INVENTORY,
                            i + xOffset + 7,
                            yOffset,
                            xTexOffset,
                            0,
                            25,
                            upperHeight,
                        )
                        drawContext.drawTexture(
                            CURIO_INVENTORY,
                            i + xOffset + 7,
                            yOffset + upperHeight,
                            xTexOffset,
                            159,
                            25,
                            7,
                        )
                    }

                    xOffset += if (r == 0) 25 else 18
                }
                xOffset -= (grid.size) * 18
                if (pageOffset) yOffset += 8

                // render slots
                for (rows in grid) {
                    val upperHeight = rows * 18

                    drawContext.drawTexture(
                        CURIO_INVENTORY,
                        i + xOffset,
                        yOffset + 7,
                        7,
                        7,
                        18,
                        upperHeight,
                    )
                    xOffset += 18
                }
                RenderSystem.enableBlend()

                for (slot in handler.slots) {
                    if (slot is CurioSlot && slot.isCosmetic) {
                        drawContext.drawTexture(
                            CURIO_INVENTORY,
                            slot.x + guiLeft - 1,
                            slot.y + guiTop - 1,
                            32,
                            50,
                            18,
                            18,
                        )
                    }
                }
                RenderSystem.disableBlend()
            }
        }
    }

    fun isPointWithinBounds(cir: CallbackInfoReturnable<Boolean>) {
        if (isRenderButtonHovered) cir.returnValue = false
    }

    fun mouseReleased(cir: CallbackInfoReturnable<Boolean>) {
        if (buttonClicked) {
            buttonClicked = false
            cir.returnValue = true
        }
    }

    fun InventorioScreen.`curios$mouseScrolled`(
        mouseX: Double,
        mouseY: Double,
        verticalAmount: Double,
    ) {
        if (curioHandler.`inventorio$totalPages` > 1 && mouseX < guiLeft && mouseX > guiLeft - panelWidth
            && mouseY > guiTop && mouseY < guiTop + thiss.backgroundHeight && scrollCooldown <= 0
        ) {
            InventorioNetworking.INSTANCE.sendToServer(CPacketPage(handler.syncId, verticalAmount < 0))
            scrollCooldown = 2
        }
    }
}
