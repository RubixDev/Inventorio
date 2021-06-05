package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.packet.InventorioNetworking;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
    /**
     * When a player joins, this mixin sends a packet to the server, telling if the player uses Swapped Hands or not
     */
    @Inject(method = "onGameJoin", at = @At(value = "RETURN"), cancellable = true)
    private void inventorioSendSwappedHandsStatus(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        PlayerInventoryAddon addon = PlayerInventoryAddon.Client.INSTANCE.getLocal();
        if (addon != null)
            InventorioNetworking.Companion.getINSTANCE().c2sSetSwappedHands(addon.getSwappedHands());
    }
}