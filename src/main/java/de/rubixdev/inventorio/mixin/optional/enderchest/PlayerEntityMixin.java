package de.rubixdev.inventorio.mixin.optional.enderchest;

import com.mojang.authlib.GameProfile;
import de.rubixdev.inventorio.mixin.accessor.SimpleInventoryAccessor;
import de.rubixdev.inventorio.util.EnderChestTester;
import de.rubixdev.inventorio.util.GeneralConstants;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
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

@Restriction(require = @Condition(type = Condition.Type.TESTER, tester = EnderChestTester.class))
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow
    public abstract EnderChestInventory getEnderChestInventory();

    /**
     * This inject enlarges the Ender Chest's capacity to 6 rows.
     */
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void inventorioResizeEnderChest(
        World world,
        BlockPos pos,
        float yaw,
        GameProfile gameProfile,
        CallbackInfo ci
    ) {
        SimpleInventoryAccessor accessor = ((SimpleInventoryAccessor) getEnderChestInventory());
        accessor.setSize(GeneralConstants.VANILLA_ROW_LENGTH * 6);
        accessor.setHeldStacks(DefaultedList.ofSize(GeneralConstants.VANILLA_ROW_LENGTH * 6, ItemStack.EMPTY));
    }
}
