package de.rubixdev.inventorio.integration.curios

import de.rubixdev.inventorio.mixin.accessor.ScreenHandlerAccessor
import de.rubixdev.inventorio.player.InventorioScreenHandler
import de.rubixdev.inventorio.util.insertItem
import de.rubixdev.inventorio.util.subList
import kotlin.math.max
import kotlin.math.min
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.CustomPayload
import net.minecraft.server.network.ServerPlayerEntity
import net.neoforged.neoforge.network.PacketDistributor
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler
import top.theillusivec4.curios.common.inventory.CosmeticCurioSlot
import top.theillusivec4.curios.common.inventory.CurioSlot
import top.theillusivec4.curios.common.network.client.CPacketScroll
import top.theillusivec4.curios.common.network.server.SPacketScroll

//#if MC >= 12004
fun sendToServer(packet: CustomPayload) {
    PacketDistributor.SERVER.noArg().send(packet)
}
fun sendToPlayer(player: ServerPlayerEntity, packet: CustomPayload) {
    PacketDistributor.PLAYER.with(player).send(packet)
}
//#else
//$$ fun <MSG> sendToServer(packet: MSG) {
//$$    top.theillusivec4.curios.common.network.NetworkHandler.INSTANCE.send(
//$$        PacketDistributor.SERVER.noArg(),
//$$        packet,
//$$    )
//$$ }
//$$ fun <MSG> sendToPlayer(player: ServerPlayerEntity, packet: MSG) {
//$$    top.theillusivec4.curios.common.network.NetworkHandler.INSTANCE.send(
//$$        PacketDistributor.PLAYER.with { player },
//$$        packet,
//$$    )
//$$ }
//#endif

/**
 * This is basically a re-implementation of https://github.com/TheIllusiveC4/Curios/blob/7bd447467d1a881d9217ebf938337723fafaabf4/neoforge/src/main/java/top/theillusivec4/curios/common/inventory/container/CuriosContainer.java
 * with adjustments for the Inventorio screen (and in Kotlin).
 */
@Suppress("FunctionName")
class InventorioScreenHandlerMixinHelper(
    thiz: InventorioScreenHandler,
) {
    val player: PlayerEntity = thiz.inventory.player

    //#if MC >= 12004
    val curiosHandler: ICuriosItemHandler? = CuriosApi.getCuriosInventory(player).orElse(null)
    //#else
    //$$ val curiosHandler: ICuriosItemHandler? = CuriosApi.getCuriosInventory(player).resolve().orElse(null)
    //#endif

    private val isLocalWorld = player.world.isClient
    private var lastScrollIndex = 0
    private var curiosSlotRange = thiz.toolBeltRange.last + 1 until thiz.slots.size
    @get:JvmName("hasCosmeticColumn")
    var hasCosmeticColumn = false
        private set

    private val thiss = thiz as ScreenHandlerAccessor

    fun InventorioScreenHandler.`curios$init`() {
        `curios$scrollToIndex`(0)
    }

    fun InventorioScreenHandler.`curios$resetSlots`() = `curios$scrollToIndex`(lastScrollIndex)

    fun InventorioScreenHandler.`curios$scrollToIndex`(indexIn: Int) {
        val curioMap = curiosHandler?.curios ?: return
        var slotCount = 0
        var yOffset = 12
        var index = 0
        var startingIndex = indexIn
        slots.subList(curiosSlotRange).clear()
        thiss.trackedStacks.subList(curiosSlotRange).clear()
        thiss.previousTrackedStacks.subList(curiosSlotRange).clear()

        val curiosSlotStart = slots.size

        for (stacksHandler in curioMap.values) {
            val stackHandler = stacksHandler.stacks
            if (stacksHandler.isVisible) {
                for (i in 0 until stackHandler.slots) {
                    if (slotCount >= 8) break
                    if (index >= startingIndex) slotCount++
                    index++
                }
            }
        }
        startingIndex = min(startingIndex, max(0, index - 8))
        index = 0
        slotCount = 0

        for ((identifier, stacksHandler) in curioMap.entries) {
            val stackHandler = stacksHandler.stacks
            if (stacksHandler.isVisible) {
                for (i in 0 until stackHandler.slots) {
                    if (slotCount >= 8) break
                    if (index >= startingIndex) {
                        thiss.callAddSlot(
                            CurioSlot(
                                player,
                                stackHandler,
                                i,
                                identifier,
                                -18,
                                yOffset,
                                stacksHandler.renders,
                                stacksHandler.canToggleRendering(),
                            ),
                        )
                        yOffset += 18
                        slotCount++
                    }
                    index++
                }
            }
        }
        index = 0
        slotCount = 0
        yOffset = 12

        for ((identifier, stacksHandler) in curioMap.entries) {
            val stackHandler = stacksHandler.stacks
            if (stacksHandler.isVisible) {
                for (i in 0 until stackHandler.slots) {
                    if (slotCount >= 8) break
                    if (index >= startingIndex) {
                        if (stacksHandler.hasCosmetic()) {
                            val cosmeticHandler = stacksHandler.cosmeticStacks
                            hasCosmeticColumn = true
                            thiss.callAddSlot(CosmeticCurioSlot(player, cosmeticHandler, i, identifier, -37, yOffset))
                        }
                        yOffset += 18
                        slotCount++
                    }
                    index++
                }
            }
        }

        if (!isLocalWorld) {
            sendToPlayer(player as ServerPlayerEntity, SPacketScroll(syncId, indexIn))
        }
        lastScrollIndex = indexIn
        curiosSlotRange = curiosSlotStart until slots.size
    }

    fun InventorioScreenHandler.`curios$scrollTo`(pos: Float) {
        if (curiosHandler != null) {
            val k = curiosHandler.visibleSlots - 8
            val j = (pos * k + 0.5).toInt().coerceAtLeast(0)
            if (j == lastScrollIndex) return
            if (isLocalWorld) {
                sendToServer(CPacketScroll(syncId, j))
            }
        }
    }

    @get:JvmName("canScroll")
    val canScroll get() = (curiosHandler?.visibleSlots ?: 0) > 8

    fun InventorioScreenHandler.`curios$setStackInSlot`(slot: Int, ci: CallbackInfo) {
        if (slots.size <= slot) ci.cancel()
    }

    fun InventorioScreenHandler.`curios$quickMoveInner`(sourceIndex: Int, cir: CallbackInfoReturnable<ItemStack>) {
        // TODO: quick move also works while the client has the curios stuff closed, but to fix that I'd have to sync the
        //  open status using custom packets which is a bit too much for just mod compat
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
                && CuriosApi.getItemStackSlots(stack).isNotEmpty()
                && insertItem(stack, curiosSlotRange)
            ) {
                cir.returnValue = ItemStack.EMPTY
            }
        }
    }
}
