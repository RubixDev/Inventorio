package me.lizardofoz.inventorio.mixin.client.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * This mixin overwrites the Quickbar rendering on the HUD
 */
@Mixin(value = InGameHud.class)
@Environment(EnvType.CLIENT)
public interface InGameHudAccessor
{
    @Invoker("renderHotbar")
    void renderHotBar(float tickDelta, MatrixStack matrices);
}
