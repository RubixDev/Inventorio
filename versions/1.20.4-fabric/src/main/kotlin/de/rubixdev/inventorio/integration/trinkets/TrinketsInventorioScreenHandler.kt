package de.rubixdev.inventorio.integration.trinkets

import de.rubixdev.inventorio.config.GlobalSettings
import de.rubixdev.inventorio.player.InventorioScreenHandler
import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.toolBeltTemplates
import de.rubixdev.inventorio.slot.ToolBeltSlot
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
import dev.emi.trinkets.mixin.accessor.ScreenHandlerAccessor
import kotlin.math.max
import kotlin.math.pow
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack

/**
 * This is basically a re-implementation of https://github.com/emilyploszaj/trinkets/blob/3e9747f95890501e1fed8c84e1f3a413647370e8/src/main/java/dev/emi/trinkets/mixin/PlayerScreenHandlerMixin.java
 * with some adjustments for the Inventorio screen (and in Kotlin).
 */
class TrinketsInventorioScreenHandler(syncId: Int, inventory: PlayerInventory) :
    InventorioScreenHandler(syncId, inventory), TrinketPlayerScreenHandler {

    private val groupNums = mutableMapOf<SlotGroup, Int>()
    private val groupPos = mutableMapOf<SlotGroup, Point>()
    private val slotHeights = mutableMapOf<SlotGroup, MutableList<Point>>()
    private val slotTypes = mutableMapOf<SlotGroup, MutableList<SlotType>>()
    private val slotWidths = mutableMapOf<SlotGroup, Int>()
    private var trinketSlotStart = 0
    private var trinketSlotEnd = 0
    private var groupCount = 0

    init {
        `trinkets$updateTrinketSlots`(true)
    }

    override fun `trinkets$updateTrinketSlots`(slotsChanged: Boolean) {
        TrinketsApi.getTrinketComponent(inventory.player).ifPresent { trinkets ->
            if (slotsChanged) trinkets.update()
            val groups = trinkets.groups
            groupPos.clear()
            while (trinketSlotStart < trinketSlotEnd) {
                slots.removeAt(trinketSlotStart)
                @Suppress("CAST_NEVER_SUCCEEDS")
                (this as ScreenHandlerAccessor).trackedStacks.removeAt(trinketSlotStart)
                @Suppress("CAST_NEVER_SUCCEEDS")
                (this as ScreenHandlerAccessor).previousTrackedStacks.removeAt(trinketSlotStart)
                trinketSlotEnd--
            }

            var groupNum = 0

            for (group in groups.values.sortedBy { it.order }) {
                if (!hasSlots(trinkets, group)) continue
                val id = group.slotId
                if (id != -1) {
                    if (slots.size > id) {
                        val slot = slots[id]
                        if (slot !is SurvivalTrinketSlot) {
                            groupPos[group] = Point(slot.x, slot.y)
                            groupNums[group] = -id
                        }
                    }
                } else {
                    val pos = if (inventoryAddon.getDeepPocketsRowCount() == 0 && GlobalSettings.utilityBeltShortDefaultSize.boolValue) {
                        // there is space right next to the 4 default utility belt slots.
                        // place groups that aren't assigned to another slot there
                        Point(98, 62 - groupNum * 18)
                    } else {
                        // otherwise place such groups above the tool belt with a small gap
                        val pos = ToolBeltSlot.getSlotPosition(
                            inventoryAddon.getDeepPocketsRowCount(),
                            0,
                            toolBeltTemplates.size + groupNum + 1,
                        )
                        Point(pos.x, pos.y - 4)
                    }
                    groupPos[group] = pos
                    groupNums[group] = groupNum
                    groupNum++
                }
            }
            groupCount = max(0, groupNum - 4)
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
                        addSlot(SurvivalTrinketSlot(stacks, i, x + pos.x, y, group, stacks.slotType, i, groupOffset == 1 && i == 0))
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
    override fun `trinkets$getGroupCount`(): Int = groupCount
    override fun `trinkets$getTrinketSlotStart`(): Int = trinketSlotStart
    override fun `trinkets$getTrinketSlotEnd`(): Int = trinketSlotEnd

    override fun onClosed(player: PlayerEntity) {
        if (player.world.isClient) {
            TrinketsClient.activeGroup = null
            TrinketsClient.activeType = null
            TrinketsClient.quickMoveGroup = null
        }
        if (!player.world.isClient) {
            (player.playerScreenHandler as TrinketPlayerScreenHandler).`trinkets$updateTrinketSlots`(true)
        }
        super.onClosed(player)
    }

    override fun quickMove(player: PlayerEntity, sourceIndex: Int): ItemStack {
        val slot = slots[sourceIndex]

        if (slot.hasStack()) {
            val stack = slot.stack
            if (sourceIndex in trinketSlotStart until trinketSlotEnd) {
                val availableDeepPocketsRange = getAvailableDeepPocketsRange()
                return if (!insertItem(stack, mainInventoryRange.first, mainInventoryRange.last + 1, false)
                    && !(
                        !availableDeepPocketsRange.isEmpty()
                            && insertItem(stack, availableDeepPocketsRange.first, availableDeepPocketsRange.last + 1, false)
                        )
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
                        if (res && insertItem(stack, i, i + 1, false) && player.world.isClient) {
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

        return super.quickMove(player, sourceIndex)
    }
}
