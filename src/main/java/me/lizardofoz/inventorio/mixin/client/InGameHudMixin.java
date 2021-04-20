package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.quickbar.QuickBarHUDRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * This mixin overwrites the Quickbar rendering on the HUD
 */
@Mixin(value = InGameHud.class, priority = 9000)
@Environment(EnvType.CLIENT)
public abstract class InGameHudMixin
{
    @Shadow protected abstract PlayerEntity getCameraPlayer();

    /**
     * @reason Redirect to our renderer
     * @author LizardOfOz
     */
    @Overwrite
    public void renderHotbar(float tickDelta, MatrixStack matrices)
    {
        PlayerEntity playerEntity = getCameraPlayer();
        if (playerEntity != null && playerEntity.isAlive() && playerEntity.playerScreenHandler != null)
            QuickBarHUDRenderer.INSTANCE.renderQuickBar((InGameHud)(Object)this, tickDelta, matrices);
    }
}
