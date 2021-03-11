package me.danetnaverno.inventorio.mixin.client;

import me.danetnaverno.inventorio.player.PlayerAddon;
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

@Mixin(ClientPlayerInteractionManager.class)
@Environment(EnvType.CLIENT)
public class ClientPlayerInteractionManagerMixin
{
    @Inject(method = "interactBlock", at = @At(value = "HEAD"))
    private void displayBlockWhileDigging(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir)
    {
        PlayerAddon.Companion.get(player).getInventoryAddon().setMainHandDisplayTool(ItemStack.EMPTY);
    }
}
