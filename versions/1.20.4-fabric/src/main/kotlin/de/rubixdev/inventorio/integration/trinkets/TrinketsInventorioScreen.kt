package de.rubixdev.inventorio.integration.trinkets

import de.rubixdev.inventorio.client.ui.InventorioScreen
import de.rubixdev.inventorio.player.InventorioScreenHandler
import dev.emi.trinkets.TrinketPlayerScreenHandler
import dev.emi.trinkets.TrinketScreen
import dev.emi.trinkets.TrinketScreenManager
import dev.emi.trinkets.api.SlotGroup
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.Rect2i
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.slot.Slot

/**
 * This is basically a re-implementation of https://github.com/emilyploszaj/trinkets/blob/3e9747f95890501e1fed8c84e1f3a413647370e8/src/main/java/dev/emi/trinkets/mixin/InventoryScreenMixin.java
 * for the Inventorio screen (and in Kotlin).
 */
class TrinketsInventorioScreen(handler: InventorioScreenHandler, inventory: PlayerInventory) :
    InventorioScreen(
        handler,
        inventory,
    ),
    TrinketScreen {
    init {
        TrinketScreenManager.init(this)
    }

    override fun handledScreenTick() {
        super.handledScreenTick()
        TrinketScreenManager.tick()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        TrinketScreenManager.update(mouseX.toFloat(), mouseY.toFloat())
        super.render(context, mouseX, mouseY, delta)
    }

    override fun drawBackground(drawContext: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        super.drawBackground(drawContext, delta, mouseX, mouseY)
        TrinketScreenManager.drawExtraGroups(drawContext)
    }

    override fun drawForeground(drawContext: DrawContext, mouseX: Int, mouseY: Int) {
        super.drawForeground(drawContext, mouseX, mouseY)
        TrinketScreenManager.drawActiveGroup(drawContext)
    }

    override fun isClickOutsideBounds(mouseX: Double, mouseY: Double, left: Int, top: Int, button: Int): Boolean {
        return if (TrinketScreenManager.isClickInsideTrinketBounds(mouseX, mouseY)) {
            false
        } else {
            super.isClickOutsideBounds(mouseX, mouseY, left, top, button)
        }
    }

    override fun `trinkets$getHandler`() = handler as TrinketPlayerScreenHandler

    override fun `trinkets$getGroupRect`(group: SlotGroup?): Rect2i {
        return when (val pos = `trinkets$getHandler`().`trinkets$getGroupPos`(group)) {
            null -> Rect2i(0, 0, 0, 0)
            else -> Rect2i(pos.x - 1, pos.y - 1, 17, 17)
        }
    }

    override fun `trinkets$getFocusedSlot`(): Slot? = focusedSlot
    override fun `trinkets$getX`(): Int = x
    override fun `trinkets$getY`(): Int = y
}
