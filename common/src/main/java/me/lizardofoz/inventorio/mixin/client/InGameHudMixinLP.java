package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.ui.HotbarHUDRenderer;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 500)
@Environment(EnvType.CLIENT)
public class InGameHudMixinLP
{
    /**
     * This mixin redirects rendering the hotbar itself in case if Segmented Hotbar is selected.
     *
     * Forge has something going on with the "InGameHud#render" method that excludes the ability to inject into it.
     * While injecting there is better for mod compatibility than this solution, Forge forces my hand to do THIS.
     * P.S. Forge's event for hotbar rendering is uncancellable, thus, not an option.
     */
    @Inject(method = "renderHotbar", at = @At(value = "HEAD"), cancellable = true, require = 0)
    private void inventorioRenderSegmentedHotbar(float tickDelta, MatrixStack matrixStack, CallbackInfo ci)
    {
        if (HotbarHUDRenderer.INSTANCE.renderSegmentedHotbar(matrixStack))
            ci.cancel();
    }

    /**
     * In vanilla, when you look at an entity with a sword, it shows an attack indicator.
     * This mixin restores this feature if you have a sword in your toolbelt.
     */
    @Redirect(method = "renderCrosshair",
            at = @At(value = "INVOKE",
                target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgressPerTick()F"),
            require = 0)
    private float inventorioShowAttackIndicator(ClientPlayerEntity clientPlayerEntity)
    {
        PlayerInventoryAddon addon = PlayerInventoryAddon.Client.INSTANCE.getLocal();
        if (addon != null && !addon.findFittingToolBeltStack(new ItemStack(Items.DIAMOND_SWORD)).isEmpty())
            return 20.0f;
        return clientPlayerEntity.getAttackCooldownProgressPerTick();
    }
}