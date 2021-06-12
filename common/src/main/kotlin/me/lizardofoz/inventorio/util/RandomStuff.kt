@file:JvmName("RandomStuff")

package me.lizardofoz.inventorio.util

import com.google.common.collect.ImmutableList
import me.lizardofoz.inventorio.config.PlayerSettings
import me.lizardofoz.inventorio.integration.InventorioModIntegration
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.*
import net.minecraft.tag.ServerTagManagerHolder
import net.minecraft.tag.SetTag
import net.minecraft.tag.TagGroup
import net.minecraft.util.Identifier

data class Point2I(@JvmField val x: Int, @JvmField val y: Int)
data class Point2F(@JvmField val x: Float, @JvmField val y: Float)
data class Rectangle(@JvmField val x: Int, @JvmField val y: Int, @JvmField val width: Int, @JvmField val height: Int)

enum class SegmentedHotbar
{
    OFF, ONLY_VISUAL, ON
}

interface ScreenHandlerDuck
{
    var screenHandlerAddon: PlayerScreenHandlerAddon?
}

interface InventoryDuck
{
    val inventorioAddon: PlayerInventoryAddon?
}

val ItemStack.isNotEmpty
    get() = !this.isEmpty

val toolBeltSlotFilters = generateToolBeltPredicates()

fun canRMBAsDisplayTool(itemStack: ItemStack): Boolean
{
    val itemClass = itemStack.item.javaClass
    return itemClass === AxeItem::class.java || itemClass === ShovelItem::class.java
}

fun canRMBItem(itemStack: ItemStack): Boolean
{
    return PlayerSettings.canThrowUnloyalTrident.boolValue
            || itemStack.item !is TridentItem
            || EnchantmentHelper.getLoyalty(itemStack) > 0
            || EnchantmentHelper.getRiptide(itemStack) > 0
}

private fun generateToolBeltPredicates(): List<(ItemStack) -> Boolean>
{
    val itemTags = ServerTagManagerHolder.getTagManager().items
    val platformDomain = if (InventorioModIntegration.isFabric) "fabric" else "forge"

    val pickaxes = getItems(itemTags, platformDomain, "pickaxes")
    val swords = getItems(itemTags, platformDomain, "swords")
    val axes = getItems(itemTags, platformDomain, "axes")
    val shovels = getItems(itemTags, platformDomain, "shovels")
    val hoes = getItems(itemTags, platformDomain, "hoes")
    val shears = getItems(itemTags, platformDomain, "shears")

    val toolBeltItems = mutableListOf<(ItemStack) -> Boolean>()
    toolBeltItems.add { (it.item is PickaxeItem || pickaxes.first.contains(it.item)) && !pickaxes.second.contains(it.item) }
    toolBeltItems.add { (it.item is SwordItem || it.item is TridentItem || swords.first.contains(it.item)) && !swords.second.contains(it.item) }
    toolBeltItems.add { (it.item is AxeItem || axes.first.contains(it.item)) && !axes.second.contains(it.item)  }
    toolBeltItems.add { (it.item is ShovelItem || shovels.first.contains(it.item)) && !shovels.second.contains(it.item) }
    toolBeltItems.add { (it.item is HoeItem || it.item is ShearsItem || hoes.first.contains(it.item) || shears.first.contains(it.item))
            && !hoes.second.contains(it.item) && !shears.second.contains(it.item) }

    return ImmutableList.copyOf(toolBeltItems)
}

private fun getItems(tags: TagGroup<Item>, platformDomain: String, path: String): Pair<List<Item>, List<Item>>
{
    val platformTag = (tags.getTag(Identifier(platformDomain, path)) as? SetTag<Item>)?.values() ?: emptyList()
    val inventorioWhiteListTag = (tags.getTag(Identifier("inventorio", path)) as? SetTag<Item>)?.values() ?: emptyList()
    val inventorioBlackListTag = (tags.getTag(Identifier("inventorio", "${path}_blacklist")) as? SetTag<Item>)?.values() ?: emptyList()
    return platformTag + inventorioWhiteListTag to inventorioBlackListTag
}