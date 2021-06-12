package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.packet.InventorioNetworking;
import me.lizardofoz.inventorio.util.MixinHelpers;
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
        InventorioNetworking.getInstance().s2cSelectUtilitySlot(player);
        InventorioNetworking.getInstance().s2cGlobalSettings(player);
    }

    /**
     * This inject sends the last utility slot, saved by the server, from the server to the client
     */
    @Inject(method = "respawnPlayer", at = @At(value = "RETURN"), require = 0)
    private void inventorioSetPlayerSettings(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir)
    {
        ServerPlayerEntity newPlayer = cir.getReturnValue();
        MixinHelpers.withInventoryAddon(newPlayer, newAddon -> {
            MixinHelpers.withInventoryAddon(oldPlayer, oldAddon -> {
                newAddon.setSwappedHands(oldAddon.getSwappedHands());
                newAddon.setSelectedUtility(oldAddon.getSelectedUtility());
            });
        });
        InventorioNetworking.getInstance().s2cSelectUtilitySlot(newPlayer);
    }
}