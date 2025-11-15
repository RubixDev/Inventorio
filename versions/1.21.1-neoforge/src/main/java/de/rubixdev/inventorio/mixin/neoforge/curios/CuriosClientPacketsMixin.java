package de.rubixdev.inventorio.mixin.neoforge.curios;

import com.llamalad7.mixinextras.sugar.Local;
import de.rubixdev.inventorio.integration.curios.ICuriosContainer;
import de.rubixdev.inventorio.integration.curios.ICuriosScreen;
import de.rubixdev.inventorio.util.CuriosTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.common.network.client.CuriosClientPackets;
import top.theillusivec4.curios.common.network.server.SPacketPage;
import top.theillusivec4.curios.common.network.server.SPacketQuickMove;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncModifiers;

@Restriction(require = { @Condition("curios"), @Condition(type = Condition.Type.TESTER, tester = CuriosTester.class) })
@Mixin(CuriosClientPackets.class)
public class CuriosClientPacketsMixin {
    @Inject(method = "handle(Ltop/theillusivec4/curios/common/network/server/SPacketQuickMove;)V", at = @At("RETURN"))
    private static void inventorio$quickMove(SPacketQuickMove data, CallbackInfo ci, @Local ClientPlayerEntity player) {
        if (player != null && player.currentScreenHandler instanceof ICuriosContainer) {
            player.currentScreenHandler.quickMove(player, data.moveIndex());
        }
    }

    @Inject(method = "handle(Ltop/theillusivec4/curios/common/network/server/SPacketPage;)V", at = @At("RETURN"))
    private static void inventorio$setPage(
        SPacketPage data,
        CallbackInfo ci,
        @Local ClientPlayerEntity player,
        @Local Screen screen
    ) {
        if (
            player != null
                && player.currentScreenHandler instanceof ICuriosContainer curiosContainer
                && player.currentScreenHandler.syncId == data.windowId()
        ) {
            curiosContainer.inventorio$setPage(data.page());
        }
        if (screen instanceof ICuriosScreen curiosScreen) {
            curiosScreen.inventorio$updateRenderButtons();
        }
    }

    @Inject(method = "lambda$handle$5", at = @At("RETURN"))
    private static void inventorio$syncModifiers(
        SPacketSyncModifiers data,
        LivingEntity livingEntity,
        Entity entity,
        MinecraftClient mc,
        ICuriosItemHandler handler,
        CallbackInfo ci
    ) {
        if (entity instanceof ClientPlayerEntity && mc.currentScreen instanceof ICuriosScreen curiosScreen) {
            curiosScreen.inventorio$updateRenderButtons();
        }
    }
}
