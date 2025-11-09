package de.rubixdev.inventorio.integration.trinkets

import de.rubixdev.inventorio.mixin.accessor.ScreenHandlerAccessor
import de.rubixdev.inventorio.player.InventorioScreenHandler
import de.rubixdev.inventorio.player.InventorioScreenHandler.Companion.deepPocketsRange
import de.rubixdev.inventorio.player.InventorioScreenHandler.Companion.mainInventoryRange
import de.rubixdev.inventorio.player.InventorioScreenHandler.Companion.utilityBeltRange
import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.toolBeltTemplates
import de.rubixdev.inventorio.slot.ToolBeltSlot
import de.rubixdev.inventorio.util.insertItem
import dev.emi.trinkets.Point
import dev.emi.trinkets.SurvivalTrinketSlot
import dev.emi.trinkets.TrinketPlayerScreenHandler
import dev.emi.trinkets.TrinketsClient
import dev.emi.trinkets.api.SlotGroup
import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.SlotType
import dev.emi.trinkets.api.TrinketComponent
import dev.emi.trinkets.api.TrinketInventory
import dev.emi.trinkets.api.TrinketsApi
import kotlin.math.pow
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

/**
 * This is basically a re-implementation of https://github.com/emilyploszaj/trinkets/blob/3e9747f95890501e1fed8c84e1f3a413647370e8/src/main/java/dev/emi/trinkets/mixin/PlayerScreenHandlerMixin.java
 * with some adjustments for the Inventorio screen (and in Kotlin).
 */
@Suppress("FunctionName")
class InventorioScreenHandlerMixinHelper(
    private val thiz: InventorioScreenHandler,
) : TrinketPlayerScreenHandler {
    private val thiss = thiz as ScreenHandlerAccessor

    private val groupNums = mutableMapOf<SlotGroup, Int>()
    private val groupPos = mutableMapOf<SlotGroup, Point>()
    private val slotHeights = mutableMapOf<SlotGroup, MutableList<Point>>()
    private val slotTypes = mutableMapOf<SlotGroup, MutableList<SlotType>>()
    private val slotWidths = mutableMapOf<SlotGroup, Int>()
    private var trinketSlotStart = 0
    private var trinketSlotEnd = 0

    companion object {
        @JvmStatic
        fun getGroupCount(player: PlayerEntity): Int = TrinketsApi.getTrinketComponent(player).map { trinkets ->
            trinkets.groups.values.count { it.slotId == -1 }
        }.orElse(0)
    }

    override fun `trinkets$updateTrinketSlots`(slotsChanged: Boolean): Unit = thiz.run {
        TrinketsApi.getTrinketComponent(inventory.player).ifPresent { trinkets ->
            if (slotsChanged) trinkets.update()
            val groups = trinkets.groups
            groupPos.clear()
            while (trinketSlotStart < trinketSlotEnd) {
                slots.removeAt(trinketSlotStart)
                thiss.trackedStacks.removeAt(trinketSlotStart)
                thiss.previousTrackedStacks.removeAt(trinketSlotStart)
                trinketSlotEnd--
            }

            var groupNum = 0

            for (group in groups.values.sortedBy { it.order }) {
                if (!hasSlots(trinkets, group)) continue
                val id = group.slotId.let {
                    if (it == PlayerScreenHandler.OFFHAND_ID) utilityBeltRange.first else it
                }
                if (id != -1) {
                    if (slots.size > id) {
                        val slot = slots[id]
                        if (slot !is SurvivalTrinketSlot) {
                            groupPos[group] = Point(slot.x, slot.y)
                            groupNums[group] = -id
                        }
                    }
                } else {
                    val pos = ToolBeltSlot.getSlotPosition(
                        inventoryAddon.getDeepPocketsRowCount(),
                        toolBeltTemplates.size + groupNum,
                        getToolBeltSlotCount(),
                    )
                    groupPos[group] = Point(pos.x, pos.y)
                    groupNums[group] = groupNum
                    groupNum++
                }
            }
            trinketSlotStart = slots.size
            slotWidths.clear()
            slotHeights.clear()
            slotTypes.clear()
            for (entry in trinkets.inventory.entries) {
                val groupId = entry.key
                val group = groups[groupId]!!
                var groupOffset = 1

                if (group.slotId != -1) groupOffset++
                var width = 0
                val pos = `trinkets$getGroupPos`(group) ?: continue
                for (slot in entry.value.entries.sortedBy { it.value.slotType.order }) {
                    val stacks = slot.value
                    if (stacks.size() == 0) continue
                    var slotOffset = 1
                    val x = ((groupOffset / 2) * 18 * (-1.0).pow(groupOffset)).toInt()
                    slotHeights.computeIfAbsent(group) { mutableListOf() }.add(Point(x, stacks.size()))
                    slotTypes.computeIfAbsent(group) { mutableListOf() }.add(stacks.slotType)
                    for (i in 0 until stacks.size()) {
                        val y = (pos.y + (slotOffset / 2) * 18 * (-1.0).pow(slotOffset)).toInt()
                        thiss.callAddSlot(
                            SurvivalTrinketSlot(stacks, i, x + pos.x, y, group, stacks.slotType, i, groupOffset == 1 && i == 0),
                        )
                        slotOffset++
                    }
                    groupOffset++
                    width++
                }
                slotWidths[group] = width
            }

            trinketSlotEnd = slots.size
        }
    }

    private fun hasSlots(component: TrinketComponent, group: SlotGroup) =
        component.inventory[group.name]?.values?.any { it.size() > 0 } ?: false

    override fun `trinkets$getGroupNum`(group: SlotGroup?): Int = groupNums[group] ?: 0
    override fun `trinkets$getGroupPos`(group: SlotGroup?): Point? = groupPos[group]
    override fun `trinkets$getSlotHeights`(group: SlotGroup?): List<Point> = slotHeights[group] ?: listOf()
    override fun `trinkets$getSlotHeight`(group: SlotGroup?, i: Int): Point? = `trinkets$getSlotHeights`(group).getOrNull(i)
    override fun `trinkets$getSlotTypes`(group: SlotGroup?): List<SlotType> = slotTypes[group] ?: listOf()
    override fun `trinkets$getSlotWidth`(group: SlotGroup?): Int = slotWidths[group] ?: 0
    override fun `trinkets$getGroupCount`(): Int = 0
    override fun `trinkets$getTrinketSlotStart`(): Int = trinketSlotStart
    override fun `trinkets$getTrinketSlotEnd`(): Int = trinketSlotEnd

    fun onClosed(player: PlayerEntity) {
        if (player.world.isClient) {
            TrinketsClient.activeGroup = null
            TrinketsClient.activeType = null
            TrinketsClient.quickMoveGroup = null
        }
        if (!player.world.isClient) {
            (player.playerScreenHandler as TrinketPlayerScreenHandler).`trinkets$updateTrinketSlots`(true)
        }
    }

    fun InventorioScreenHandler.`trinkets$quickMove`(
        player: PlayerEntity,
        sourceIndex: Int,
        cir: CallbackInfoReturnable<ItemStack>,
    ) {
        val slot = slots[sourceIndex]

        if (slot.hasStack()) {
            val stack = slot.stack
            if (sourceIndex in trinketSlotStart until trinketSlotEnd) {
                val availableDeepPocketsRange = getAvailableDeepPocketsRange()
                cir.returnValue = if (!insertItem(stack, mainInventoryRange)
                    && !(!availableDeepPocketsRange.isEmpty() && insertItem(stack, availableDeepPocketsRange))
                ) {
                    ItemStack.EMPTY
                } else {
                    stack
                }
            } else if (sourceIndex in mainInventoryRange || sourceIndex in deepPocketsRange) {
                TrinketsApi.getTrinketComponent(player).ifPresent {
                    for (i in trinketSlotStart until trinketSlotEnd) {
                        val s = slots[i]
                        if (s !is SurvivalTrinketSlot || !s.canInsert(stack)) continue

                        val type = s.type
                        val ref = SlotReference(s.inventory as TrinketInventory, s.index)

                        val res = TrinketsApi.evaluatePredicateSet(type.quickMovePredicates, stack, ref, player)
                        if (res && insertItem(stack, i..i) && player.world.isClient) {
                            TrinketsClient.quickMoveTimer = 20
                            TrinketsClient.quickMoveGroup = TrinketsApi.getPlayerSlots(inventory.player)[type.group]
                            TrinketsClient.quickMoveType = if (ref.index > 0) {
                                type
                            } else {
                                null
                            }
                        }
                    }
                }
            }
        }
    }
}
