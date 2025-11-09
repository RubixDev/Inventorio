package de.rubixdev.inventorio.mixin.neoforge.curios;

import de.rubixdev.inventorio.util.CuriosTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import top.theillusivec4.curios.common.network.server.CuriosServerPayloadHandler;

@Restriction(require = { @Condition("curios"), @Condition(type = Condition.Type.TESTER, tester = CuriosTester.class) })
@Mixin(CuriosServerPayloadHandler.class)
public class CuriosServerPayloadHandlerMixin {
    // TODO: curios compat
//    @Inject(
//        method = "lambda$handleScroll$5",
//        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
//        slice = @Slice(
//            from = @At(
//                value = "FIELD",
//                target = "Lnet/minecraft/entity/player/PlayerEntity;currentScreenHandler:Lnet/minecraft/screen/ScreenHandler;"
//            )
//        )
//    )
//    private static void inventorioScrollToIndex(
//        CPacketScroll data,
//        PlayerEntity player,
//        CallbackInfo ci,
//        @Local ScreenHandler container
//    ) {
//        if (container instanceof ICuriosContainer curiosContainer && container.syncId == data.windowId()) {
//            curiosContainer.inventorio$scrollToIndex(data.index());
//        }
//    }
}
