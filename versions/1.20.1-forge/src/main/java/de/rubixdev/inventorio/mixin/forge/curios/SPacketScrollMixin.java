package de.rubixdev.inventorio.mixin.forge.curios;

import com.llamalad7.mixinextras.sugar.Local;
import de.rubixdev.inventorio.integration.curios.ICuriosContainer;
import de.rubixdev.inventorio.integration.curios.ICuriosScreen;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.network.server.SPacketScroll;

@Restriction(require = @Condition("curios"))
@Mixin(SPacketScroll.class)
public abstract class SPacketScrollMixin {
    @Shadow
    private int windowId;

    @Shadow
    private int index;

    @Inject(
        method = "lambda$handle$0",
        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Ltop/theillusivec4/curios/common/inventory/container/CuriosContainer;scrollToIndex(I)V"
            )
        )
    )
    private static void inventorioUpdateRenderButtons(SPacketScroll msg, CallbackInfo ci, @Local Screen screen) {
        if (screen instanceof ICuriosScreen curiosScreen) {
            curiosScreen.inventorio$updateRenderButtons();
        }
    }

    @Inject(
        method = "lambda$handle$0",
        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/client/network/ClientPlayerEntity;currentScreenHandler:Lnet/minecraft/screen/ScreenHandler;"
            )
        )
    )
    private static void inventorioScrollToIndex(SPacketScroll msg, CallbackInfo ci, @Local ScreenHandler container) {
        SPacketScrollMixin accessor = ((SPacketScrollMixin) (Object) msg);
        // noinspection DataFlowIssue
        if (container instanceof ICuriosContainer curiosContainer && container.syncId == accessor.windowId) {
            curiosContainer.inventorio$scrollToIndex(accessor.index);
        }
    }
}
