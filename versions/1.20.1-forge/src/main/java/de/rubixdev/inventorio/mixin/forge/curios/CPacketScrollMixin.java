package de.rubixdev.inventorio.mixin.forge.curios;

import com.llamalad7.mixinextras.sugar.Local;
import de.rubixdev.inventorio.integration.curios.ICuriosContainer;
import de.rubixdev.inventorio.util.CuriosTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.screen.ScreenHandler;
import net.minecraftforge.network.NetworkEvent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.network.client.CPacketScroll;

import java.util.function.Supplier;

@Restriction(require = { @Condition("curios"), @Condition(type = Condition.Type.TESTER, tester = CuriosTester.class) })
@Mixin(CPacketScroll.class)
public abstract class CPacketScrollMixin {
    @Shadow
    private int index;

    @Shadow
    private int windowId;

    @Inject(
        method = "lambda$handle$0",
        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/server/network/ServerPlayerEntity;currentScreenHandler:Lnet/minecraft/screen/ScreenHandler;"
            )
        )
    )
    private static void inventorioScrollToIndex(
        Supplier<NetworkEvent.Context> ctx,
        CPacketScroll msg,
        CallbackInfo ci,
        @Local ScreenHandler container
    ) {
        CPacketScrollMixin accessor = ((CPacketScrollMixin) (Object) msg);
        // noinspection DataFlowIssue
        if (container instanceof ICuriosContainer curiosContainer && container.syncId == accessor.windowId) {
            curiosContainer.inventorio$scrollToIndex(accessor.index);
        }
    }
}
