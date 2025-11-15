package de.rubixdev.inventorio.integration.curios

import de.rubixdev.inventorio.mixin.accessor.ScreenHandlerAccessor
import de.rubixdev.inventorio.packet.InventorioNetworking
import de.rubixdev.inventorio.player.InventorioScreenHandler
import de.rubixdev.inventorio.player.InventorioScreenHandler.Companion.deepPocketsRange
import de.rubixdev.inventorio.player.InventorioScreenHandler.Companion.mainInventoryRange
import de.rubixdev.inventorio.player.InventorioScreenHandler.Companion.toolBeltRange
import de.rubixdev.inventorio.util.insertItem
import de.rubixdev.inventorio.util.isNotEmpty
import de.rubixdev.inventorio.util.subList
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler
import top.theillusivec4.curios.common.CuriosConfig
import top.theillusivec4.curios.common.inventory.CurioSlot
import top.theillusivec4.curios.common.network.server.SPacketPage
import top.theillusivec4.curios.common.network.server.SPacketQuickMove

/**
 * This is basically a re-implementation of https://github.com/TheIllusiveC4/Curios/blob/ab847aab52213afd87c78f48ad9382212846f1b7/neoforge/src/main/java/top/theillusivec4/curios/common/inventory/container/CuriosContainer.java
 * with adjustments for the Inventorio screen (and in Kotlin).
 */
@Suppress("FunctionName")
class InventorioScreenHandlerMixinHelper(
    thiz: InventorioScreenHandler,
) {
    val player: PlayerEntity = thiz.inventory.player

    private val curiosHandler: ICuriosItemHandler? = CuriosApi.getCuriosInventory(player).orElse(null)

    private val isLocalWorld = player.world.isClient
    private var curiosSlotRange = toolBeltRange.last + 1..<thiz.slots.size
    var currentPage = 0
        private set
    var totalPages = 0
        private set
    val grid = mutableListOf<Int>()
    private val proxySlots = mutableListOf<ProxySlot>()
    private var moveToPage = -1
    private var moveFromIndex = -1
    var hasCosmetics = false
        private set
    var isViewingCosmetics = false
        private set
    var panelWidth = 0

    private val thiss = thiz as ScreenHandlerAccessor

    fun InventorioScreenHandler.`curios$init`() {
        `curios$resetSlots`()
    }

    fun InventorioScreenHandler.`curios$resetSlots`() = `curios$setPage`(currentPage)

    fun InventorioScreenHandler.`curios$setPage`(page: Int) {
        slots.subList(curiosSlotRange).clear()
        thiss.trackedStacks.subList(curiosSlotRange).clear()
        thiss.previousTrackedStacks.subList(curiosSlotRange).clear()
        panelWidth = 0
        var visibleSlots: Int
        val maxSlotsPerPage: Int = CuriosConfig.SERVER.maxSlotsPerPage.get()
        val startingIndex = page * maxSlotsPerPage
        var columns: Int
        val curiosSlotStart = slots.size

        if (curiosHandler != null) {
            visibleSlots = curiosHandler.visibleSlots
            val slotsOnPage = min(maxSlotsPerPage, visibleSlots - startingIndex)
            val calculatedColumns = ceil(slotsOnPage.toDouble() / 8).toInt()
            val minimumColumns = min(slotsOnPage, CuriosConfig.SERVER.minimumColumns.get())
            columns = calculatedColumns.coerceIn(minimumColumns, 8)
            panelWidth = 14 + 18 * columns

            val curioMap = curiosHandler.curios
            totalPages = ceil(visibleSlots.toDouble() / maxSlotsPerPage).toInt()
            var index = 0
            var yOffset = 8

            if (totalPages > 1) {
                yOffset += 8
            }
            var currentColumn = 1
            var currentRow = 1
            var slots = 0
            grid.clear()
            proxySlots.clear()
            var currentPage = 0
            val endingIndex = startingIndex + maxSlotsPerPage

            for ((identifier, stacksHandler) in curioMap.entries) {
                var isCosmetic = false
                var stackHandler = stacksHandler.stacks

                if (stacksHandler.hasCosmetic()) {
                    hasCosmetics = true

                    if (isViewingCosmetics) {
                        isCosmetic = true
                        stackHandler = stacksHandler.cosmeticStacks
                    }
                }

                if (stacksHandler.isVisible) {
                    for (i in 0..<stackHandler.slots) {
                        if (index in startingIndex..<endingIndex) {
                            thiss.callAddSlot(
                                CurioSlot(
                                    player,
                                    stackHandler,
                                    i,
                                    identifier,
                                    (currentColumn - 1) * 18 + 7 - panelWidth,
                                    yOffset + (currentRow - 1) * 18,
                                    stacksHandler.renders,
                                    stacksHandler.canToggleRendering(),
                                    isCosmetic,
                                    isCosmetic,
                                ),
                            )

                            if (grid.size < currentColumn) {
                                grid.add(1)
                            } else {
                                grid[currentColumn - 1] = grid[currentColumn - 1] + 1
                            }

                            if (currentColumn == columns) {
                                currentColumn = 1
                                currentRow++
                            } else {
                                currentColumn++
                            }
                        } else {
                            proxySlots.add(
                                ProxySlot(
                                    currentPage,
                                    CurioSlot(
                                        player,
                                        stackHandler,
                                        i,
                                        identifier,
                                        (currentColumn - 1) * 18 + 7 - panelWidth,
                                        yOffset + (currentRow - 1) * 18,
                                        stacksHandler.renders,
                                        stacksHandler.canToggleRendering(),
                                        isCosmetic,
                                        isCosmetic,
                                    ),
                                ),
                            )
                        }
                        slots++

                        if (slots >= maxSlotsPerPage) {
                            slots = 0
                            currentPage++
                        }
                        index++
                    }
                }
            }

            if (!isLocalWorld) {
                InventorioNetworking.INSTANCE.sendToPlayer(player as ServerPlayerEntity, SPacketPage(syncId, page))
            }
        }
        currentPage = page
        curiosSlotRange = curiosSlotStart..<slots.size
    }

    fun InventorioScreenHandler.`curios$toggleCosmetics`() {
        isViewingCosmetics = !isViewingCosmetics
        `curios$resetSlots`()
    }

    fun InventorioScreenHandler.`curios$setStackInSlot`(slot: Int, ci: CallbackInfo) {
        if (slots.size <= slot) ci.cancel()
    }

    fun InventorioScreenHandler.`curios$quickMove`(player: PlayerEntity, sourceIndex: Int, cir: CallbackInfoReturnable<ItemStack>) {
        // TODO: quick move also works while the client has the curios stuff closed, but to fix that I'd have to sync the
        //  open status using custom packets which is a bit too much for just mod compat
        // TODO: open curios when quick moving there
        val slot = slots[sourceIndex]
        if (slot.hasStack()) {
            val stack = slot.stack
            if (sourceIndex in curiosSlotRange) {
                val availableDeepPocketsRange = getAvailableDeepPocketsRange()
                cir.returnValue = if (!insertItem(stack, mainInventoryRange)
                    && !(!availableDeepPocketsRange.isEmpty() && insertItem(stack, availableDeepPocketsRange))
                ) {
                    ItemStack.EMPTY
                } else {
                    stack
                }
            } else if ((sourceIndex in mainInventoryRange || sourceIndex in deepPocketsRange)
                && CuriosApi.getItemStackSlots(stack, player.world).isNotEmpty()
                && !insertItem(stack, curiosSlotRange)
            ) {
                val page = findAvailableSlot(stack)

                if (page != 1) {
                    moveToPage = page
                    moveFromIndex = sourceIndex
                }
                cir.returnValue = ItemStack.EMPTY
            }
        }
    }

    private fun findAvailableSlot(stack: ItemStack): Int {
        var result = -1

        if (stack.isStackable) {
            for (proxySlot in proxySlots) {
                val slot = proxySlot.slot
                val itemStack = slot.stack

                if (itemStack.isNotEmpty && ItemStack.areItemsAndComponentsEqual(stack, itemStack)) {
                    val j = itemStack.count + stack.count
                    val maxSize = min(slot.maxItemCount, stack.maxCount)

                    if (j <= maxSize || itemStack.count < maxSize) {
                        result = proxySlot.page
                        break
                    }
                }
            }
        }

        if (stack.isNotEmpty && result == -1) {
            for (proxySlot in proxySlots) {
                val slot1 = proxySlot.slot
                val itemStack1 = slot1.stack
                if (itemStack1.isEmpty && slot1.canInsert(stack)) {
                    result = proxySlot.page
                    break
                }
            }
        }
        return result
    }

    fun InventorioScreenHandler.`curios$nextPage`() {
        `curios$setPage`(min(currentPage + 1, totalPages - 1))
    }

    fun InventorioScreenHandler.`curios$prevPage`() {
        `curios$setPage`(max(currentPage - 1, 0))
    }

    fun InventorioScreenHandler.`curios$checkQuickMove`() {
        if (moveToPage != -1) {
            `curios$setPage`(moveToPage)
            quickMove(player, moveFromIndex)
            moveToPage = -1

            if (!isLocalWorld) {
                InventorioNetworking.INSTANCE.sendToPlayer(player as ServerPlayerEntity, SPacketQuickMove(syncId, moveFromIndex))
            }
        }
    }

    private data class ProxySlot(val page: Int, val slot: Slot)
}
