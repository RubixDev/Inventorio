package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.control.InventorioControls;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin
{
    /**
     * The default implementation stops an item from being used if the vanilla "use" key isn't pressed.
     * This mods adds an optional secondary use key, and this prevents the secondary item usage from being cancelled
     */
    @Inject(method = "stopUsingItem", at = @At(value = "HEAD"), cancellable = true)
    private void inventorioStopUsingItem(PlayerEntity player, CallbackInfo ci)
    {
        if (PlayerInventoryAddon.Client.isUsingUtility)
        {
            if (InventorioControls.keyUseUtility.isPressed())
                ci.cancel();
            else
                PlayerInventoryAddon.Client.isUsingUtility = false;
        }
    }
}