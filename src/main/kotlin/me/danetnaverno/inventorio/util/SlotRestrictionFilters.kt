package me.danetnaverno.inventorio.util

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import net.minecraft.block.ShulkerBoxBlock
import net.minecraft.item.*
import net.minecraft.screen.AnvilScreenHandler
import net.minecraft.screen.EnchantmentScreenHandler
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.registry.Registry
import java.awt.Point

object SlotRestrictionFilters
{
    private lateinit var quickBarItems: List<Item>
    private lateinit var utilityBeltItems: List<Item>

    val quickBar: (ItemStack) -> Boolean = { quickBarItems.contains(it.item) }
    val utilityBelt: (ItemStack) -> Boolean = { utilityBeltItems.contains(it.item) }
    val toolBelt: List<(ItemStack) -> Boolean>
    val physicalUtilityBar: (ItemStack) -> Boolean = {
        ((it.item as? BlockItem)?.block is ShulkerBoxBlock)
                || (it.item == Items.WRITABLE_BOOK || it.item == Items.WRITABLE_BOOK)
                || it.item is ToolItem
    }

    val screenHandlerOffsets: Map<Class<out ScreenHandler>, Point>

    init
    {
        val quickBarItems = mutableListOf<Item>()

        //Items allowed in the QuickBar. Only things you place in-world are allowed (mostly building blocks)
        quickBarItems.add(Items.AIR)
        quickBarItems.addAll(Registry.ITEM.filterIsInstance<BlockItem>())
        quickBarItems.addAll(Registry.ITEM.filterIsInstance<BoatItem>())
        quickBarItems.addAll(Registry.ITEM.filterIsInstance<MinecartItem>())
        quickBarItems.addAll(Registry.ITEM.filterIsInstance<AliasedBlockItem>())
        quickBarItems.addAll(listOf(Items.END_CRYSTAL, Items.ITEM_FRAME, Items.PAINTING, Items.ARMOR_STAND))

        //Items allowed in the ToolBelt. Pickaxes, axes, etc
        val utilityBarItems = mutableListOf<Item>()
        utilityBarItems.addAll(Registry.ITEM.filterIsInstance<BucketItem>())
        utilityBarItems.addAll(Registry.ITEM.filter { it.isFood })
        utilityBarItems.add(Items.FIREWORK_ROCKET)
        utilityBarItems.add(Items.ENDER_PEARL)
        utilityBarItems.add(Items.ENDER_EYE)
        utilityBarItems.add(Items.TOTEM_OF_UNDYING)
        utilityBarItems.add(Items.LEAD)
        utilityBarItems.add(Items.SHIELD)
        utilityBarItems.add(Items.POTION)
        utilityBarItems.add(Items.SPLASH_POTION)
        utilityBarItems.add(Items.LINGERING_POTION)
        utilityBarItems.add(Items.TORCH)
        utilityBarItems.add(Items.SHEARS)

        //Questionable items
        val unsuredItems = mutableListOf<Item>()
        unsuredItems.add(Items.FLINT_AND_STEEL)
        unsuredItems.add(Items.BONE_MEAL)
        unsuredItems.add(Items.DEBUG_STICK)
        unsuredItems.add(Items.NAME_TAG)
        unsuredItems.add(Items.WRITTEN_BOOK)
        unsuredItems.add(Items.WRITABLE_BOOK)
        unsuredItems.addAll(Registry.ITEM.filter { (it as? BlockItem)?.block is ShulkerBoxBlock })
        unsuredItems.addAll(Registry.ITEM.filterIsInstance<SpawnEggItem>())


        //Items allowed in the ToolBelt. Pickaxes, axes, etc
        val toolBeltItems = mutableListOf<(ItemStack) -> Boolean>()
        toolBeltItems.add { it.item is PickaxeItem }
        toolBeltItems.add { it.item is SwordItem || it.item is TridentItem }
        toolBeltItems.add { it.item is AxeItem }
        toolBeltItems.add { it.item is ShovelItem }
        toolBeltItems.add { it.item is HoeItem || it.item is ShearsItem }

        //Items which shall by physically stored in the QuickBar
        val physicalQuickBarItems = mutableListOf<(ItemStack) -> Boolean>()
        physicalQuickBarItems.add {
            ((it.item as? BlockItem)?.block is ShulkerBoxBlock)
                    || (it.item == Items.WRITABLE_BOOK || it.item == Items.WRITABLE_BOOK)
                    || it.item is ToolItem
        }

        this.quickBarItems = ImmutableList.copyOf(quickBarItems + unsuredItems)
        this.utilityBeltItems = ImmutableList.copyOf(utilityBarItems + unsuredItems)
        this.toolBelt = ImmutableList.copyOf(toolBeltItems)

        val screenHandlerOffsets = mutableMapOf<Class<out ScreenHandler>, Point>()
        screenHandlerOffsets[EnchantmentScreenHandler::class.java] = Point(0, 5)
        screenHandlerOffsets[AnvilScreenHandler::class.java] = Point(0, 12)

        this.screenHandlerOffsets = ImmutableMap.copyOf(screenHandlerOffsets)
    }
}