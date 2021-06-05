package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.ui.HotbarHUDRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 1500)
@Environment(EnvType.CLIENT)
public class InGameHudMixinHP
{
    /**
     * This mixin calls the renderer of hotbar addons.
     * Note: this mixin doesn't work in Forge and substituted with a Forge event.
     */
    @Inject(method = "render", at = @At(value = "RETURN"))
    private void inventorioRenderHotbarAddons(MatrixStack matrices, float tickDelta, CallbackInfo ci)
    {
        HotbarHUDRenderer.INSTANCE.renderHotbarAddons(matrices);
    }

    /**
     * This mixin removes the vanilla offhand display (both the item frame and the item)
     */
    @Redirect(method = "renderHotbar",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getOffHandStack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack inventorioRemoveOffhandDisplayFromHotbar(PlayerEntity playerEntity)
    {
        return ItemStack.EMPTY;
    }
}