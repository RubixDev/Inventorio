package me.lizardofoz.inventorio.mixin.optional.enderchest;

import net.minecraft.block.EnderChestBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin enlarges the Ender Chest's Inventory Screen to 6 rows. To enlarge
 * the actual storage, {@link PlayerEntityMixin#inventorioResizeEnderChest} is
 * used
 */
@Mixin(value = EnderChestBlock.class)
public class EnderChestBlockMixin {
    @Redirect(
        method = "method_17468",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/screen/GenericContainerScreenHandler;createGeneric9x3(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;)Lnet/minecraft/screen/GenericContainerScreenHandler;"
        )
    )
    private static GenericContainerScreenHandler inventorioOnEnderChestOpen(
        int syncId,
        PlayerInventory playerInventory,
        Inventory inventory
    ) {
        return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, inventory);
    }
}
