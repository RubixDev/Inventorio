package de.rubixdev.inventorio.mixin.forge.curios;

import de.rubixdev.inventorio.integration.curios.ICuriosContainer;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.common.inventory.CurioStacksHandler;

@Restriction(require = @Condition("curios"))
@Mixin(CurioStacksHandler.class)
public class CurioStacksHandlerMixin {
    @Shadow
    @Final
    private ICuriosItemHandler itemHandler;

    @Inject(
        method = "update",
        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"
            )
        ),
            remap = false
    )
    private void inventorioResetSlots(CallbackInfo ci) {
        if (
            itemHandler.getWearer() instanceof PlayerEntity player
                && player.currentScreenHandler instanceof ICuriosContainer curiosContainer
        ) {
            curiosContainer.inventorio$resetSlots();
        }
    }
}
