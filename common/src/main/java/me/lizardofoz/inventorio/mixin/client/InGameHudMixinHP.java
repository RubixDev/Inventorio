package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.ui.HotbarHUDRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 1500)
@Environment(EnvType.CLIENT)
public abstract class InGameHudMixinHP
{
    @Shadow protected abstract PlayerEntity getCameraPlayer();
    @Shadow @Final private MinecraftClient client;

    /**
     * This mixin calls the hotbar addon rendering. Note: this mixin doesn't work in Forge and substituted with a Forge event.
     */
    @Inject(method = "render", at = @At(value = "RETURN"))
    public void inventorioRenderHotbarAddons(MatrixStack matrices, float tickDelta, CallbackInfo ci)
    {
        if (this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR && !this.client.options.hudHidden)
        {
            PlayerEntity playerEntity = getCameraPlayer();
            if (playerEntity != null && playerEntity.isAlive() && playerEntity.playerScreenHandler != null)
                HotbarHUDRenderer.INSTANCE.renderHotbarAddons(matrices);
        }
    }

    /**
     * This mixin removes the vanilla hotbar display (both the frame and the item)
     */
    @Redirect(method = "renderHotbar",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getOffHandStack()Lnet/minecraft/item/ItemStack;"))
    public ItemStack inventorioRemoveOffhandDisplayFromHotbar(PlayerEntity playerEntity)
    {
        return ItemStack.EMPTY;
    }
}