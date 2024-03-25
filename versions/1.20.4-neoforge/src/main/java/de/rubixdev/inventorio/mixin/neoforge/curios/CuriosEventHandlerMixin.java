package de.rubixdev.inventorio.mixin.neoforge.curios;

import de.rubixdev.inventorio.integration.curios.ICuriosContainer;
import de.rubixdev.inventorio.util.CuriosTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.network.ServerPlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.common.event.CuriosEventHandler;

@Restriction(require = { @Condition("curios"), @Condition(type = Condition.Type.TESTER, tester = CuriosTester.class) })
@Mixin(CuriosEventHandler.class)
public class CuriosEventHandlerMixin {
    //#if MC >= 12004
    @Inject(
        method = "lambda$onDatapackSync$4",
        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/server/network/ServerPlayerEntity;currentScreenHandler:Lnet/minecraft/screen/ScreenHandler;"
            )
        )
    )
    private static void inventorioResetSlots(ServerPlayerEntity mp, ICuriosItemHandler handler, CallbackInfo ci) {
        if (mp.currentScreenHandler instanceof ICuriosContainer curiosContainer) {
            curiosContainer.inventorio$resetSlots();
        }
    }
    //#endif

    @Inject(
        //#if MC >= 12004
        method = "lambda$onDatapackSync$2",
        //#else
        //$$ method = "lambda$onDatapackSync$5",
        //#endif
        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/server/network/ServerPlayerEntity;currentScreenHandler:Lnet/minecraft/screen/ScreenHandler;"
            )
        )
    )
    private static void inventorioResetSlots2(ServerPlayerEntity player, ICuriosItemHandler handler, CallbackInfo ci) {
        if (player.currentScreenHandler instanceof ICuriosContainer curiosContainer) {
            curiosContainer.inventorio$resetSlots();
        }
    }
}
