package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.util.MixinHelpers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin
{
    @Shadow public ServerPlayerEntity player;

    /**
     * These 3 injects remove the display tool (server-side) when a player tries to use (right-click) something from the main hand,
     * so that the item that's ACTUALLY in the main hand can be used immediately
     */
    @Inject(method = "onPlayerInteractBlock", at = @At(value = "HEAD"))
    private void inventorioRemoveDisplayHand(PlayerInteractBlockC2SPacket packet, CallbackInfo ci)
    {
        if (packet.getHand() == Hand.MAIN_HAND)
            MixinHelpers.withInventoryAddon(player, addon -> addon.setDisplayTool(ItemStack.EMPTY));
    }

    @Inject(method = "onPlayerInteractEntity", at = @At(value = "HEAD"))
    private void inventorioRemoveDisplayHand(PlayerInteractEntityC2SPacket packet, CallbackInfo ci)
    {
        if (packet.getHand() == Hand.MAIN_HAND)
            MixinHelpers.withInventoryAddon(player, addon -> addon.setDisplayTool(ItemStack.EMPTY));
    }

    @Inject(method = "onPlayerInteractItem", at = @At(value = "HEAD"))
    private void inventorioRemoveDisplayHand(PlayerInteractItemC2SPacket packet, CallbackInfo ci)
    {
        if (packet.getHand() == Hand.MAIN_HAND)
            MixinHelpers.withInventoryAddon(player, addon -> addon.setDisplayTool(ItemStack.EMPTY));
    }
}