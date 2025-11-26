package de.rubixdev.inventorio.slot

import com.mojang.datafixers.util.Pair
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.Identifier

//#if MC >= 12101
import net.minecraft.component.EnchantmentEffectComponentTypes
//#else
//$$ import net.minecraft.entity.mob.MobEntity
//#endif

class ArmorSlot(
    inventory: Inventory,
    private val entity: LivingEntity,
    private val equipmentSlot: EquipmentSlot,
    index: Int,
    x: Int,
    y: Int,
) : Slot(inventory, index, x, y) {
    //#if MC >= 12101
    override fun setStack(stack: ItemStack?, previousStack: ItemStack?) {
        entity.onEquipStack(equipmentSlot, previousStack, stack)
        super.setStack(stack, previousStack)
        // TODO: update deep pockets?
    }
    //#endif

    override fun getMaxItemCount(): Int = 1

    override fun canInsert(stack: ItemStack): Boolean =
        //#if MC >= 12101
        equipmentSlot == entity.getPreferredEquipmentSlot(stack)
        //#else
        //$$ equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack)
        //#endif

    override fun canTakeItems(playerEntity: PlayerEntity): Boolean {
        val itemStack = this.stack
        return !(
            !itemStack.isEmpty
                && !playerEntity.isCreative
                //#if MC >= 12101
                && EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE)
                //#else
                //$$ && EnchantmentHelper.hasBindingCurse(itemStack)
                //#endif
            ) && super.canTakeItems(playerEntity)
    }

    override fun getBackgroundSprite(): Pair<Identifier, Identifier>? {
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot])
    }

    companion object {
        private val EMPTY_ARMOR_SLOT_TEXTURES = mapOf(
            EquipmentSlot.FEET to PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE,
            EquipmentSlot.LEGS to PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE,
            EquipmentSlot.CHEST to PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE,
            EquipmentSlot.HEAD to PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE,
        )
    }
}
