package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.extra.InventorioServerConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BowItem.class, priority = 500)
public class BowItemMixin
{
    /**
     * [This option is disabled by default]
     * This fixes a bug (yes, it's a bug! check out BowItem#onStoppedUsing method!) that Infinity Bow requires an arrow to shoot.
     */
    @Redirect(method = "use",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getArrowType(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack displayUtilityInOffhand(PlayerEntity playerEntity, ItemStack bowStack)
    {
        if (InventorioServerConfig.INSTANCE.getInfinityBowNeedsNoArrow() && EnchantmentHelper.getLevel(Enchantments.INFINITY, bowStack) > 0)
            return new ItemStack(Items.ARROW);
        else
            return playerEntity.getArrowType(bowStack);
    }
}
