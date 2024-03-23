package de.rubixdev.inventorio.integration.curios

import de.rubixdev.inventorio.client.ui.AbstractInventorioScreen
import de.rubixdev.inventorio.mixin.client.accessor.HandledScreenAccessor
import de.rubixdev.inventorio.util.MixinDelegate
import kotlin.math.min
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
import top.theillusivec4.curios.client.gui.RenderButton
import top.theillusivec4.curios.common.inventory.CosmeticCurioSlot
import top.theillusivec4.curios.common.inventory.CurioSlot
import top.theillusivec4.curios.common.network.client.CPacketToggleRender

/**
 * This is basically a re-implementation of https://github.com/TheIllusiveC4/Curios/blob/7bd447467d1a881d9217ebf938337723fafaabf4/neoforge/src/main/java/top/theillusivec4/curios/client/gui/CuriosScreen.java
 * for the Inventorio screen (and in Kotlin).
 */
@Suppress("FunctionName")
class InventorioScreenMixinHelper(
    private val thiz: AbstractInventorioScreen,
    private val recipeBook: RecipeBookWidget,
) {
    companion object {
        private val CURIO_INVENTORY = Identifier("curios", "textures/gui/curios/inventory.png")
        private val SCROLLER = Identifier("container/creative_inventory/scroller")

        var currentScroll = 0f
    }

    var isCuriosOpen = false
        private set

    private var hasScrollbar = false
    private lateinit var buttonCurios: TexturedButtonWidget
    private var wasRecipeBookOpen = mutableListOf(false)
    private var wasCuriosOpen = mutableListOf(false)
    private var isScrolling = false
    private var buttonClicked = false
    private var isRenderButtonHovered = false

    private val handler get() = thiss.handler
    private val curioHandler get() = handler as ICuriosContainer

    private val thiss = thiz as HandledScreenAccessor<*>

    ///// private members of the mixin target class ////
    private var x by MixinDelegate(thiss::getX, thiss::setX)
    private var y by MixinDelegate(thiss::getY, thiss::setY)

    fun AbstractInventorioScreen.`curios$init`() {
        thiss.client?.also { client ->
            client.player?.also { player ->
                hasScrollbar = CuriosApi.getCuriosInventory(player).map { it.visibleSlots > 0 }.orElse(false)
                if (hasScrollbar) {
                    curioHandler.`inventorio$scrollTo`(currentScroll)
                }
            }

            val offsets = CuriosScreen.getButtonOffset(false)
            buttonCurios = CustomCuriosButton(
                thiz,
                x + offsets.left + 2,
                height / 2 + offsets.right + 2,
                10,
                10,
                CuriosButton.BIG,
            ) {
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
            thiss.callAddDrawableChild(buttonCurios)

            `curios$updateRenderButtons`()
        }
    }

    fun AbstractInventorioScreen.`curios$hideCuriosWhenOpeningRecipeBook`() {
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

    fun AbstractInventorioScreen.`curios$updateRenderButtons`() {
        thiss.selectables.removeIf { it is RenderButton }
        thiss.children.removeIf { it is RenderButton }
        drawables.removeIf { it is RenderButton }

        if (!isCuriosOpen) return

        for (inventorySlot in handler.slots) {
            if (inventorySlot is CurioSlot && inventorySlot !is CosmeticCurioSlot) {
                if (inventorySlot.canToggleRender()) {
                    thiss.callAddDrawableChild(
                        RenderButton(
                            inventorySlot,
                            x + inventorySlot.x + 11,
                            y + inventorySlot.y - 3,
                            8,
                            8,
                            75,
                            0,
                            CURIO_INVENTORY,
                        ) {
                            sendToServer(CPacketToggleRender(inventorySlot.identifier, inventorySlot.slotIndex))
                        },
                    )
                }
            }
        }
    }

    private fun inScrollbar(mouseX: Double, mouseY: Double): Boolean {
        if (!isCuriosOpen) return false
        val i = x
        val j = y
        var k = i - 34
        val l = j + 12
        var i1 = k + 14
        val j1 = l + 139
        if (curioHandler.`inventorio$hasCosmeticColumn`) {
            i1 -= 19
            k -= 19
        }
        return mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1
    }

    fun AbstractInventorioScreen.`curios$render`(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
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

    fun drawBackground(drawContext: DrawContext) {
        if (!isCuriosOpen) return
        thiss.client?.player?.let { player ->
            val i = x
            val j = y
            CuriosApi.getCuriosInventory(player).ifPresent { handler ->
                val slotCount = handler.visibleSlots
                if (slotCount <= 0) return@ifPresent
                val upperHeight = 7 + min(slotCount, 9) * 18
                var xTexOffset = 0
                var width = 27
                var xOffset = -26
                if (curioHandler.`inventorio$hasCosmeticColumn`) {
                    xTexOffset = 92
                    width = 46
                    xOffset -= 19
                }
                drawContext.drawTexture(
                    CURIO_INVENTORY,
                    i + xOffset,
                    j + 4,
                    xTexOffset,
                    0,
                    width,
                    upperHeight,
                )

                if (slotCount <= 8) {
                    drawContext.drawTexture(
                        CURIO_INVENTORY,
                        i + xOffset,
                        j + 4 + upperHeight,
                        xTexOffset,
                        151,
                        width,
                        7,
                    )
                } else {
                    drawContext.drawTexture(
                        CURIO_INVENTORY,
                        i + xOffset - 16,
                        j + 4,
                        27,
                        0,
                        23,
                        158,
                    )
                    drawContext.drawGuiTexture(
                        SCROLLER,
                        i + xOffset - 8,
                        j + 12 + (127f * currentScroll).toInt(),
                        12,
                        15,
                    )
                }

                for (slot in this.handler.slots.filterIsInstance<CosmeticCurioSlot>()) {
                    val x = x + slot.x - 1
                    val y = y + slot.y - 1
                    drawContext.drawTexture(
                        CURIO_INVENTORY,
                        x,
                        y,
                        138,
                        0,
                        18,
                        18,
                    )
                }
            }
        }
    }

    fun isPointWithinBounds(cir: CallbackInfoReturnable<Boolean>) {
        if (isRenderButtonHovered) cir.returnValue = false
    }

    fun mouseClicked(mouseX: Double, mouseY: Double, cir: CallbackInfoReturnable<Boolean>) {
        if (inScrollbar(mouseX, mouseY)) {
            isScrolling = curioHandler.`inventorio$canScroll`
            cir.returnValue = true
        }
    }

    fun mouseReleased(button: Int, cir: CallbackInfoReturnable<Boolean>) {
        if (button == 0) {
            isScrolling = false
        }
        if (buttonClicked) {
            buttonClicked = false
            cir.returnValue = true
        }
    }

    fun mouseDragged(mouseY: Double, cir: CallbackInfoReturnable<Boolean>) {
        if (isScrolling) {
            val i = y + 8
            val j = i + 148
            currentScroll = ((mouseY.toFloat() - i - 7.5f) / (j - i - 15f)).coerceIn(0f, 1f)
            curioHandler.`inventorio$scrollTo`(currentScroll)
            cir.returnValue = true
        }
    }

    fun mouseScrolled(verticalAmount: Double, cir: CallbackInfoReturnable<Boolean>) {
        if (curioHandler.`inventorio$canScroll` && isCuriosOpen) {
            var i = 1
            curioHandler.`inventorio$curiosHandler`?.let { handler ->
                i = handler.visibleSlots
            }
            currentScroll = (currentScroll - verticalAmount / i).toFloat().coerceIn(0f, 1f)
            curioHandler.`inventorio$scrollTo`(currentScroll)
            cir.returnValue = true
        }
    }
}
