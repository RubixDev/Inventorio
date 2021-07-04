package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.control.InventorioKeyHandler;
import me.lizardofoz.inventorio.packet.InventorioNetworking;
import me.lizardofoz.inventorio.util.MixinHelpers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
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
    @Shadow @Nullable public Screen currentScreen;

    /**
     * This inject opens the Inventorio Screen instead of the Vanilla Player Screen
     */
    @Inject(method = "handleInputEvents",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
                    ordinal = 1),
            cancellable = true)
    private void inventorioOpenReplacingScreen(CallbackInfo ci)
    {
        InventorioNetworking.getInstance().c2sOpenInventorioScreen();
        ci.cancel();
    }
    /**
     * This inject replaces vanilla Hotbar slot selection with ours (Segmented Hotbar)
     */
    @Redirect(method = "handleInputEvents",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/options/KeyBinding;wasPressed()Z",
            ordinal = 2))
    private boolean inventorioHandleHotbarSlotSelection(KeyBinding keyBinding)
    {
        if (!keyBinding.wasPressed())
            return false;
        if (player.isSpectator())
            return true;

        if (!player.isCreative() || currentScreen != null || (!options.keySaveToolbarActivator.isPressed() && !options.keyLoadToolbarActivator.isPressed())
        )
            for (int i = 0; i < 9; ++i)
            {
                if (keyBinding == options.keysHotbar[i])
                    return !InventorioKeyHandler.INSTANCE.handleSegmentedHotbarSlotSelection(player.inventory, i);
            }
        return true;
    }

    /**
     * This inject prevents swapping the items between hands while there is a display tool active,
     * because that causes a non-duping glitch when a tool in-use gets pulled off from a tool belt
     */
    @Inject(method = "handleInputEvents",
            at = @At(value = "NEW",
                    target = "net/minecraft/network/packet/c2s/play/PlayerActionC2SPacket"),
            cancellable = true)
    private void inventorioPreventOffhandSwapForDisplayTool(CallbackInfo ci)
    {
        MixinHelpers.withInventoryAddon(player, addon -> {
            if (!addon.getDisplayTool().isEmpty())
                ci.cancel();
        });
    }

    /**
     * This redirect enables the ability to bind the Offhand/Utility to a separate button.
     * <p>
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
}