package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.player.PlayerAddon;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin empties the main hand when a player starts to mine a block,
 * causing a correct tool to be display instead of the currently selected quickbar item
 */
@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin
{
    @Inject(method = "interactBlock", at = @At(value = "HEAD"))
    private void displayToolWhileDigging(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir)
    {
        PlayerAddon.get(player).getInventoryAddon().setMainHandDisplayTool(ItemStack.EMPTY);
    }
}