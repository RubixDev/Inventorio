package de.rubixdev.inventorio.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.rubixdev.inventorio.client.control.InventorioKeyHandler;
import de.rubixdev.inventorio.client.ui.InventorioScreen;
import de.rubixdev.inventorio.packet.InventorioNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class, priority = 9999)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin {
    @Shadow
    public ClientPlayerEntity player;

    @Shadow
    @Nullable public Screen currentScreen;

    /**
     * This injection opens the Inventorio Screen instead of the Vanilla Player
     * Screen
     */
    @WrapOperation(
        method = "handleInputEvents",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
            ordinal = 0
        ),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onInventoryOpened()V")
        )
    )
    private void inventorioOpenReplacingScreen(MinecraftClient instance, Screen old, Operation<Void> original) {
        if (InventorioScreen.shouldOpenVanillaInventory) {
            original.call(instance, old);
        } else {
            InventorioNetworking.getInstance().c2sOpenInventorioScreen();
        }
    }

    /**
     * This injection replaces vanilla Hotbar slot selection with ours
     * (Segmented Hotbar)
     */
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @WrapOperation(
        method = "handleInputEvents",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I")
    )
    private void inventorioHandleHotbarSlotSelection(
        PlayerInventory instance,
        int value,
        Operation<Integer> original,
        @Local int i
    ) {
        if (!InventorioKeyHandler.INSTANCE.handleSegmentedHotbarSlotSelection(instance, i)) {
            original.call(instance, value);
        }
    }

    /**
     * This injection prevents swapping the items between hands while there is a
     * display tool active, because that causes a non-duping glitch when a tool
     * in-use gets pulled off from a tool belt
     */
    @Inject(
        method = "handleInputEvents",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket;"
        ),
        cancellable = true
    )
    private void inventorioPreventOffhandSwapForDisplayTool(CallbackInfo ci) {
        ci.cancel();
        InventorioNetworking.getInstance().c2sSwapItemsInHands();
    }

    /**
     * This redirect enables the ability to bind the Offhand/Utility to a
     * separate button.
     * <p>
     * If the option is enabled, a regular right click won't attempt to use an
     * Offhand item, but a dedicated key will attempt to use ONLY an Offhand
     * item.
     */
    @Redirect(
        method = "doItemUse",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;")
    )
    private Hand[] inventorioDoItemUse() {
        if (player == null) return new Hand[] {};
        return InventorioKeyHandler.INSTANCE.handleItemUsage(player);
    }

    /**
     * This injection stops the game from locking the cursor back to 0,0 when
     * swapping between the Inventorio and vanilla Inventory screens.
     */
    @Inject(
        method = "setScreen",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
            opcode = Opcodes.PUTFIELD
        ),
        cancellable = true
    )
    private void seamlessScreenTransition(Screen screen, CallbackInfo ci) {
        if (
            InventorioScreen.isSwappingInvScreens
                && screen == null
                && (currentScreen instanceof InventorioScreen || currentScreen instanceof InventoryScreen)
        ) {
            ci.cancel();
        }
    }
}
