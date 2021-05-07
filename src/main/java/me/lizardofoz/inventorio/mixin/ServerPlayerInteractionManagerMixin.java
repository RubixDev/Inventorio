package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin empties the {@link PlayerInventoryAddon#getMainHandDisplayTool()} when a player rightclicks a block,
 * because when we place blocks we don't want our custom main hand behavior to kick in
 */
@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin
{
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "interactBlock", at = @At(value = "HEAD"))
    private void displayToolWhileDigging(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir)
    {
        PlayerInventoryAddon.getInventoryAddon(player).setMainHandDisplayTool(ItemStack.EMPTY);
    }
}