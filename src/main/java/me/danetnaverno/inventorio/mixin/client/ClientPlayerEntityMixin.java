package me.danetnaverno.inventorio.mixin.client;

import me.danetnaverno.inventorio.client.config.InventorioConfigData;
import me.danetnaverno.inventorio.player.PlayerAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
@Environment(EnvType.CLIENT)
public class ClientPlayerEntityMixin
{
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void setIgnoredScreens(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting, CallbackInfo ci)
    {
        PlayerAddon playerAddon = PlayerAddon.Companion.get((ClientPlayerEntity)(Object)this);
        playerAddon.setAllIgnoredScreenHandlers(InventorioConfigData.Companion.config().getIgnoredScreensGlobal());
    }
}