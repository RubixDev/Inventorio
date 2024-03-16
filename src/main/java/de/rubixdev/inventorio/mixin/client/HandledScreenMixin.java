package de.rubixdev.inventorio.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.rubixdev.inventorio.util.MixinHelpers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = HandledScreen.class)
@Environment(EnvType.CLIENT)
public class HandledScreenMixin {
    @Shadow
    @Nullable protected Slot focusedSlot;

    /**
     * This injection allows to use the vanilla Swap Offhand key (F by default)
     * to move items to the Utility Belt in the inventory (the injected method
     * is an edge-case handler when you have "swap offhand" on mouse buttons)
     */
    @WrapOperation(
        method = "onMouseClick(I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
            ordinal = 0
        ),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/option/GameOptions;swapHandsKey:Lnet/minecraft/client/option/KeyBinding;"
            )
        )
    )
    private void inventorioOffhandSwapWithMouse(
        HandledScreen<?> instance,
        Slot slot,
        int slotId,
        int button,
        SlotActionType actionType,
        Operation<Void> original
    ) {
        MixinHelpers.withScreenHandler(
            MinecraftClient.getInstance().player,
            screenHandler -> screenHandler.tryTransferToUtilityBeltSlot(focusedSlot),
            player -> original.call(instance, slot, slotId, button, actionType, original)
        );
    }

    /**
     * This injection allows to use the vanilla Swap Offhand key (F by default)
     * to move items to the Utility Belt in the inventory
     */
    @WrapOperation(
        method = "handleHotbarKeyPressed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V",
            ordinal = 0
        ),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/option/GameOptions;swapHandsKey:Lnet/minecraft/client/option/KeyBinding;"
            )
        )
    )
    private void inventorioOffhandSwapWithKeyboard(
        HandledScreen<?> instance,
        Slot slot,
        int slotId,
        int button,
        SlotActionType actionType,
        Operation<Void> original
    ) {
        MixinHelpers.withScreenHandler(
            MinecraftClient.getInstance().player,
            screenHandler -> screenHandler.tryTransferToUtilityBeltSlot(focusedSlot),
            player -> original.call(instance, slot, slotId, button, actionType)
        );
    }
}
