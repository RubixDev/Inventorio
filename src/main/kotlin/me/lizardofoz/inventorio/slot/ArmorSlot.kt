package me.lizardofoz.inventorio.slot

import com.mojang.datafixers.util.Pair
import me.lizardofoz.inventorio.mixin.accessor.PlayerScreenHandlerAccessor
import me.lizardofoz.inventorio.util.isNotEmpty
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.PlayerScreenHandler.BLOCK_ATLAS_TEXTURE
import net.minecraft.screen.slot.Slot
import net.minecraft.util.Identifier

class ArmorSlot(playerInventory: PlayerInventory, private val equipmentSlot: EquipmentSlot, index: Int, x: Int, y: Int): Slot(playerInventory, index, x, y)
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
        val itemStack = stack
        return if (itemStack.isNotEmpty && !playerEntity.isCreative && EnchantmentHelper.hasBindingCurse(itemStack))
            false
        else
            super.canTakeItems(playerEntity)
    }

    @Environment(EnvType.CLIENT)
    override fun getBackgroundSprite(): Pair<Identifier, Identifier>?
    {
        return Pair.of(BLOCK_ATLAS_TEXTURE, PlayerScreenHandlerAccessor.getEmptyArmorSlotTextures()[equipmentSlot.entitySlotId])
    }
}
   