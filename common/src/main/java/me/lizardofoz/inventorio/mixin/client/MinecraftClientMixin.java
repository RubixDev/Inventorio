package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.InventorioKeyHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class, priority = 9999)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin
{
    @Shadow public ClientPlayerEntity player;
    @Shadow @Final public GameOptions options;

    /**
     * This inject replaces vanilla Hotbar slot selection with ours (Segmented Hotbar)
     */
    @Inject(method = "handleInputEvents",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I"), cancellable = true)
    private void inventorioHandleHotbarSlotSelection(CallbackInfo ci)
    {
        for (int i = 0; i < 9; ++i)
        {
            if (this.options.keysHotbar[i].isPressed() && InventorioKeyHandler.INSTANCE.handleSegmentedHotbarSlotSelection(player.inventory, i))
            {
                ci.cancel();
                return;
            }
        }
    }

    /**
     * This redirect enables the ability to bind the Offhand/Utility to a separate button.
     *
     * If option is enabled, a regular RightClick won't attempt to use an Offhand item,
     * but a dedicated key will attempt to use ONLY an Offhand item.
     */
    @Redirect(method = "doItemUse",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;"))
    private Hand[] inventorioDoItemUse()
    {
        if (player == null)
            return new Hand[]{};
        return InventorioKeyHandler.INSTANCE.handleItemUsage(player);
    }

    /**
     * This is one of several injects to remove the offhand swap hotkey.
     * UPD: We actually allow the offhand swap outside of the UI, because UI swap relies on hardcoded slot ID,
     * while this swap does not, and thus, works perfectly fine.
     */
    @Redirect(method = "handleInputEvents",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void inventorioRemoveOffhandSwap(ClientPlayNetworkHandler clientPlayNetworkHandler, Packet<?> packet)
    {
        if (MinecraftClient.getInstance().currentScreen == null)
            clientPlayNetworkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
    }
}