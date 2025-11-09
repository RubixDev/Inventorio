package de.rubixdev.inventorio.mixin.neoforge.curios;

import de.rubixdev.inventorio.util.CuriosTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import top.theillusivec4.curios.common.network.client.CuriosClientPayloadHandler;

@Restriction(require = { @Condition("curios"), @Condition(type = Condition.Type.TESTER, tester = CuriosTester.class) })
@Mixin(CuriosClientPayloadHandler.class)
public class CuriosClientPayloadHandlerMixin {
    // TODO: curios compat
//    @Inject(
//        method = "lambda$handleScroll$2",
//        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
//        slice = @Slice(
//            from = @At(
//                value = "INVOKE",
//                target = "Ltop/theillusivec4/curios/common/inventory/container/CuriosContainer;scrollToIndex(I)V"
//            )
//        )
//    )
//    private static void inventorioUpdateRenderButtons(SPacketScroll data, CallbackInfo ci, @Local Screen screen) {
//        if (screen instanceof ICuriosScreen curiosScreen) {
//            curiosScreen.inventorio$updateRenderButtons();
//        }
//    }
//
//    @Inject(
//        method = "lambda$handleSyncCurios$13",
//        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
//        slice = @Slice(
//            from = @At(
//                value = "INVOKE",
//                target = "Ltop/theillusivec4/curios/api/type/capability/ICuriosItemHandler;setCurios(Ljava/util/Map;)V"
//            )
//        )
//    )
//    private static void inventorioResetSlots(
//        SPacketSyncCurios data,
//        Entity entity,
//        ICuriosItemHandler handler,
//        CallbackInfo ci
//    ) {
//        if (
//            entity instanceof ClientPlayerEntity player
//                && player.currentScreenHandler instanceof ICuriosContainer curiosContainer
//        ) {
//            curiosContainer.inventorio$resetSlots();
//        }
//    }
//
//    @Inject(
//        method = "lambda$handleSyncModifiers$10",
//        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
//        slice = @Slice(
//            from = @At(
//                value = "INVOKE",
//                target = "Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;"
//            )
//        )
//    )
//    private static void inventorioResetSlots(
//        SPacketSyncModifiers data,
//        LivingEntity livingEntity,
//        Entity entity,
//        ICuriosItemHandler handler,
//        CallbackInfo ci
//    ) {
//        if (
//            entity instanceof ClientPlayerEntity player
//                && player.currentScreenHandler instanceof ICuriosContainer curiosContainer
//        ) {
//            curiosContainer.inventorio$resetSlots();
//        }
//    }
//
//    @Inject(
//        method = "lambda$handleScroll$2",
//        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
//        slice = @Slice(
//            from = @At(
//                value = "FIELD",
//                target = "Lnet/minecraft/client/network/ClientPlayerEntity;currentScreenHandler:Lnet/minecraft/screen/ScreenHandler;"
//            )
//        )
//    )
//    private static void inventorioScrollToIndex(SPacketScroll data, CallbackInfo ci, @Local ScreenHandler container) {
//        if (container instanceof ICuriosContainer curiosContainer && container.syncId == data.windowId()) {
//            curiosContainer.inventorio$scrollToIndex(data.index());
//        }
//    }
}
