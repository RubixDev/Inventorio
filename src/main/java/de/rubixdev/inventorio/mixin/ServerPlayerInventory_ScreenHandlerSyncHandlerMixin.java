package de.rubixdev.inventorio.mixin;

import de.rubixdev.inventorio.player.inventory.PlayerInventoryExtension;
import de.rubixdev.inventorio.util.MixinHelpers;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.network.ServerPlayerEntity$1")
public class ServerPlayerInventory_ScreenHandlerSyncHandlerMixin {
    @Shadow
    @Final
    ServerPlayerEntity field_29182;

    @Inject(method = "updateState", at = @At("HEAD"))
    private void updateAddonState(
        ScreenHandler handler,
        DefaultedList<ItemStack> stacks,
        ItemStack cursorStack,
        int[] properties,
        CallbackInfo ci
    ) {
        MixinHelpers.withInventoryAddon(this.field_29182, PlayerInventoryExtension::updateState);
    }
}
