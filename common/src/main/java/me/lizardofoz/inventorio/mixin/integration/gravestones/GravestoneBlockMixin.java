package me.lizardofoz.inventorio.mixin.integration.gravestones;

import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import net.guavy.gravestones.block.GravestoneBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GravestoneBlock.class)
public class GravestoneBlockMixin
{
    /**
     * Since the Gravestones Mod appears to be abandoned, we need to take compatibility into out own hands.
     * These 2 redirects fix a bug when items from Inventorio slots on your CURRENT character disappear when you pick up a grave.
     */
    @Inject(method = "RetrieveGrave",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/collection/DefaultedList;addAll(Ljava/util/Collection;)Z",
                    shift = At.Shift.AFTER,
                    ordinal = 0),
            require = 0,
            remap = false)
    private void inventorioSaveAddonSlots(PlayerEntity playerEntity, World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        PlayerInventoryAddon addon = PlayerInventoryAddon.getInventoryAddon(playerEntity);
        if (addon == null)
            return;

        for (ItemStack stack : addon.stacks)
        {
            ItemEntity itemEntity = playerEntity.dropItem(stack, true);
            if (itemEntity != null)
                itemEntity.setPickupDelay(0);
        }
    }
}
