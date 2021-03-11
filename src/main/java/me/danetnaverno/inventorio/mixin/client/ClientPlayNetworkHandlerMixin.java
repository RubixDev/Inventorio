package me.danetnaverno.inventorio.mixin.client;

import me.danetnaverno.inventorio.packet.InventorioNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandlerMixin
{
    @Inject(method = "onGameJoin", at = @At(value = "RETURN"))
    private void sendIgnoredScreensC2S(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        InventorioNetworking.INSTANCE.C2SSendIgnoredScreenHandlers();
    }
}