package me.lizardofoz.inventorio.slot

import com.mojang.datafixers.util.Pair
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.util.Identifier

class ArmorSlot(inventory: Inventory, index: Int, x: Int, y: Int, private val equipmentSlot: EquipmentSlot) : Slot(inventory, index, x, y)
{
    override fun getMaxItemCount(): Int
    {
        return 1
    }

    override fun canInsert(stack: ItemStack): Boolean
    {
        return equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack)
    }

    override fun canTakeItems(playerEntity: PlayerEntity): Boolean
    {
        val itemStack = this.stack
        return if (!itemStack.isEmpty && !playerEntity.isCreative && EnchantmentHelper.hasBindingCurse(itemStack)) false else super.canTakeItems(playerEntity)
    }

    @Environment(EnvType.CLIENT)
    override fun getBackgroundSprite(): Pair<Identifier, Identifier>?
    {
        return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.entitySlotId])
    }

    companion object
    {
        private val EMPTY_ARMOR_SLOT_TEXTURES = arrayOf(
            PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE,
            PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE,
            PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE,
            PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE)
    }
}