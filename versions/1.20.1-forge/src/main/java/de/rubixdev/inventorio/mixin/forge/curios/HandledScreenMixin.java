package de.rubixdev.inventorio.mixin.forge.curios;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.rubixdev.inventorio.integration.curios.ICuriosScreen;
import de.rubixdev.inventorio.util.CuriosTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.inventory.CurioSlot;

@Restriction(require = { @Condition("curios"), @Condition(type = Condition.Type.TESTER, tester = CuriosTester.class) })
@Mixin(HandledScreen.class)
public class HandledScreenMixin extends Screen {
    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @ModifyReturnValue(method = "isPointOverSlot", at = @At("RETURN"))
    private boolean dontRenderCuriosWhenClosed(boolean original, Slot slot) {
        if (
            this instanceof ICuriosScreen curiosScreen
                && slot instanceof CurioSlot
                && !curiosScreen.getInventorio$isCuriosOpen()
        ) {
            return false;
        }
        return original;
    }

    @Inject(method = "drawSlot", at = @At("HEAD"), cancellable = true)
    private void dontRenderCuriosWhenClosed(DrawContext context, Slot slot, CallbackInfo ci) {
        if (
            this instanceof ICuriosScreen curiosScreen
                && slot instanceof CurioSlot
                && !curiosScreen.getInventorio$isCuriosOpen()
        ) {
            ci.cancel();
        }
    }
}
