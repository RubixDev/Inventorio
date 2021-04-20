package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.player.PlayerAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin empties the main hand when a player starts to mine a block,
 * causing a correct tool to be display instead of the currently selected quickbar item
 */
@Mixin(ClientPlayerInteractionManager.class)
@Environment(EnvType.CLIENT)
public class ClientPlayerInteractionManagerMixin
{
    @Inject(method = "interactBlock", at = @At(value = "HEAD"))
    private void displayToolWhileDigging(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir)
    {
        PlayerAddon.get(player).getInventoryAddon().setMainHandDisplayTool(ItemStack.EMPTY);
    }
}
