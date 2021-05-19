package me.lizardofoz.inventorio.mixin.enderchest;

import com.mojang.authlib.GameProfile;
import me.lizardofoz.inventorio.mixin.accessor.SimpleInventoryAccessor;
import me.lizardofoz.inventorio.util.GeneralConstantsKt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin
{
    @Shadow public abstract EnderChestInventory getEnderChestInventory();

    /**
     * This inject enlarges the Ender Chest's capacity to 6 rows.
     */
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void inventorioResizeEnderChest(World world, BlockPos pos, float yaw, GameProfile profile, CallbackInfo ci)
    {
        SimpleInventoryAccessor accessor = ((SimpleInventoryAccessor) getEnderChestInventory());
        accessor.setSize(GeneralConstantsKt.VANILLA_ROW_LENGTH * 6);
        accessor.setStacks(DefaultedList.ofSize(GeneralConstantsKt.VANILLA_ROW_LENGTH * 6, ItemStack.EMPTY));
    }
}