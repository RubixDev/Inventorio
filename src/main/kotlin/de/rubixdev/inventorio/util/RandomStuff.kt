@file:JvmName("RandomStuff")

package de.rubixdev.inventorio.util

import de.rubixdev.inventorio.config.PlayerSettings
import de.rubixdev.inventorio.mixin.accessor.ScreenHandlerAccessor
import de.rubixdev.inventorio.player.PlayerInventoryAddon
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.ItemStack
import net.minecraft.item.TridentItem
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager

//#if MC >= 12101
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.Entity
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.entry.RegistryEntry
//#endif

data class Point2I(@JvmField val x: Int, @JvmField val y: Int)
data class Point2F(@JvmField val x: Float, @JvmField val y: Float)
data class Rectangle(@JvmField val x: Int, @JvmField val y: Int, @JvmField val width: Int, @JvmField val height: Int)

enum class SegmentedHotbar {
    OFF,
    ONLY_VISUAL,
    ON,
    ONLY_FUNCTION,
}

enum class ScrollWheelUtilityBeltMode {
    OFF,
    REGULAR,
    REVERSE,
}

enum class ToolBeltMode {
    ENABLED,
    NO_VANILLA_SLOTS_ONLY,
    DISABLED,
}

interface PlayerDuck {
    @Suppress("INAPPLICABLE_JVM_NAME") // see https://stackoverflow.com/questions/47504279/java-interop-apply-jvmname-to-getters-of-properties-in-interface-or-abstract-c
    @get:JvmName("inventorio${'$'}getInventorioAddon")
    val inventorioAddon: PlayerInventoryAddon?
}

val logger = LogManager.getLogger("Inventorio")!!

val ItemStack.isNotEmpty
    get() = !this.isEmpty

fun canRMBItem(itemStack: ItemStack, registries: DynamicRegistryManager): Boolean {
    return PlayerSettings.canThrowUnloyalTrident.boolValue
        || itemStack.item !is TridentItem
        //#if MC >= 12101
        || EnchantmentHelper.getLevel(registries.getEnchantment(Enchantments.LOYALTY), itemStack) > 0
        || EnchantmentHelper.getLevel(registries.getEnchantment(Enchantments.RIPTIDE), itemStack) > 0
        //#else
        //$$ || EnchantmentHelper.getLoyalty(itemStack) > 0
        //$$ || EnchantmentHelper.getRiptide(itemStack) > 0
        //#endif
}

//#if MC >= 12101
fun RegistryEntry<Enchantment>.getLevelOn(stack: ItemStack): Int =
//#else
//$$ fun Enchantment.getLevelOn(stack: ItemStack): Int =
//#endif
    EnchantmentHelper.getLevel(this, stack)

fun <E> MutableList<E>.subList(indices: IntRange) = subList(indices.first, indices.last + 1)

fun ScreenHandler.insertItem(stack: ItemStack, indices: IntRange, fromLast: Boolean = false): Boolean =
    (this as ScreenHandlerAccessor).callInsertItem(stack, indices.first, indices.last + 1, fromLast)

class MixinDelegate<T>(
    private val getter: Supplier<T>,
    private val setter: Consumer<T>,
) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = getter.get()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = setter.accept(value)
}

/** Turn this String into an [Identifier] in this mod's namespace */
//#if MC >= 12101
val String.id get() = Identifier.of(MOD_ID, this)!!
//#else
//$$ val String.id get() = Identifier(MOD_ID, this)
//#endif

/** Turn this String into an [Identifier] in the vanilla namespace */
//#if MC >= 12101
fun String.id() = Identifier.ofVanilla(this)!!
//#else
//$$ fun String.id() = Identifier(this)
//#endif

/** Turn this String into an [Identifier] in the given namespace */
//#if MC >= 12101
fun String.id(namespace: String) = Identifier.of(namespace, this)!!
//#else
//$$ fun String.id(namespace: String) = Identifier(namespace, this)
//#endif

//#if MC >= 12101
fun DynamicRegistryManager.getEnchantment(key: RegistryKey<Enchantment>) =
    get(RegistryKeys.ENCHANTMENT).entryOf(key)!!

fun RegistryWrapper.WrapperLookup.getEnchantment(key: RegistryKey<Enchantment>) =
    getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(key)!!

fun Entity.getEnchantment(key: RegistryKey<Enchantment>) =
    registryManager.getEnchantment(key)
//#endif
