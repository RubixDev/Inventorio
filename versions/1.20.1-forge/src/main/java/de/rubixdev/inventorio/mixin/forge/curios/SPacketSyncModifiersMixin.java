package de.rubixdev.inventorio.mixin.forge.curios;

import de.rubixdev.inventorio.integration.curios.ICuriosContainer;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncModifiers;

@Restriction(require = @Condition("curios"))
@Mixin(SPacketSyncModifiers.class)
public class SPacketSyncModifiersMixin {
    @Inject(
        method = "lambda$handle$0",
        at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 0),
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"
            )
        )
    )
    private static void inventorioResetSlots(
        SPacketSyncModifiers data,
        LivingEntity livingEntity,
        Entity entity,
        ICuriosItemHandler handler,
        CallbackInfo ci
    ) {
        if (
            entity instanceof ClientPlayerEntity player
                && player.currentScreenHandler instanceof ICuriosContainer curiosContainer
        ) {
            curiosContainer.inventorio$resetSlots();
        }
    }
}
