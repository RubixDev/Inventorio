package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.packet.InventorioNetworking;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin
{
    @Shadow public ServerPlayerEntity player;

    /**
     * This inject sends gameplay-impacting Player's settings (QuickBar Mode and UtilityBelt Mode) from the server to the player
     */
    @Inject(method = "onClientSettings", at = @At(value = "RETURN"))
    private void setPlayerSettingsBack(ClientSettingsC2SPacket packet, CallbackInfo ci)
    {
        InventorioNetworking.INSTANCE.S2CSendPlayerSettings(this.player);
    }
}