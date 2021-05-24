package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.packet.InventorioNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin
{
    /**
     * This inject sends the last utility slot, saved by the server, from the server to the client
     */
    @Inject(method = "onPlayerConnect", at = @At(value = "RETURN"), require = 0)
    private void inventorioSetPlayerSettings(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci)
    {
        InventorioNetworking.Companion.getINSTANCE().s2cSendSelectedUtilitySlot(player);
    }

    /**
     * This inject sends the last utility slot, saved by the server, from the server to the client
     */
    @Inject(method = "respawnPlayer", at = @At(value = "RETURN"), require = 0)
    private void inventorioSetPlayerSettings(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir)
    {
        InventorioNetworking.Companion.getINSTANCE().s2cSendSelectedUtilitySlot(player);
    }
}