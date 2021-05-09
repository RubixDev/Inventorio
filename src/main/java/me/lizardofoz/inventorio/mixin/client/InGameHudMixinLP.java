package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.ui.HotbarHUDRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = InGameHud.class, priority = 500)
@Environment(EnvType.CLIENT)
public abstract class InGameHudMixinLP
{
    @Shadow protected abstract PlayerEntity getCameraPlayer();

    /**
     * This mixin redirects rendering the hotbar itself in case if Segmented Hotbar is selected.
     */
    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"),
            require = 0)
    public void renderHotbarRedirect(InGameHud inGameHud, float tickDelta, MatrixStack matrices)
    {
        PlayerEntity playerEntity = getCameraPlayer();
        if (playerEntity != null && playerEntity.isAlive() && playerEntity.playerScreenHandler != null)
            HotbarHUDRenderer.INSTANCE.renderHotbarItself(playerEntity, inGameHud, tickDelta, matrices);
    }
}