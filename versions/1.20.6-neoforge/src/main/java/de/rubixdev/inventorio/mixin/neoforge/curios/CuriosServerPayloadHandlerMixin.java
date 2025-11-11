package de.rubixdev.inventorio.mixin.neoforge.curios;

import com.llamalad7.mixinextras.sugar.Local;
import de.rubixdev.inventorio.integration.curios.ICuriosContainer;
import de.rubixdev.inventorio.util.CuriosTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.screen.ScreenHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.network.client.CPacketPage;
import top.theillusivec4.curios.common.network.client.CPacketToggleCosmetics;
import top.theillusivec4.curios.common.network.server.CuriosServerPayloadHandler;

@Restriction(require = { @Condition("curios"), @Condition(type = Condition.Type.TESTER, tester = CuriosTester.class) })
@Mixin(CuriosServerPayloadHandler.class)
public class CuriosServerPayloadHandlerMixin {
    @Inject(method = "lambda$handlerToggleCosmetics$4", at = @At("RETURN"))
    private static void inventorioToggleCosmetics(
        IPayloadContext ctx,
        CPacketToggleCosmetics data,
        CallbackInfo ci,
        @Local ScreenHandler container
    ) {
        if (container instanceof ICuriosContainer curiosContainer && container.syncId == data.windowId()) {
            curiosContainer.inventorio$toggleCosmetics();
        }
    }

    @Inject(method = "lambda$handlePage$3", at = @At("RETURN"))
    private static void inventorioSetPage(
        IPayloadContext ctx,
        CPacketPage data,
        CallbackInfo ci,
        @Local ScreenHandler container
    ) {
        if (container instanceof ICuriosContainer curiosContainer && container.syncId == data.windowId()) {
            if (data.next()) {
                curiosContainer.inventorio$nextPage();
            } else {
                curiosContainer.inventorio$prevPage();
            }
        }
    }
}
