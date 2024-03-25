package de.rubixdev.inventorio.mixin.neoforge.curios;

import com.llamalad7.mixinextras.sugar.Local;
import de.rubixdev.inventorio.integration.curios.ICuriosContainer;
import de.rubixdev.inventorio.util.CuriosTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.network.client.CPacketScroll;
import top.theillusivec4.curios.common.network.server.CuriosServerPayloadHandler;

@Restriction(require = { @Condition("curios"), @Condition(type = Condition.Type.TESTER, tester = CuriosTester.class) })
@Mixin(CuriosServerPayloadHandler.class)
public class CuriosServerPayloadHandlerMixin {
    @Inject(
        method = "lambda$handleScroll$5",
        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                target = "Lnet/minecraft/entity/player/PlayerEntity;currentScreenHandler:Lnet/minecraft/screen/ScreenHandler;"
            )
        )
    )
    private static void inventorioScrollToIndex(
        CPacketScroll data,
        PlayerEntity player,
        CallbackInfo ci,
        @Local ScreenHandler container
    ) {
        if (container instanceof ICuriosContainer curiosContainer && container.syncId == data.windowId()) {
            curiosContainer.inventorio$scrollToIndex(data.index());
        }
    }
}
