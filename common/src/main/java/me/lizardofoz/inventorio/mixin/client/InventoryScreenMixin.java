package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.ui.InventorioScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryScreen.class)
@Environment(EnvType.CLIENT)
public class InventoryScreenMixin
{
    @Inject(method = "init", at = @At(value = "RETURN"))
    private void inventorioAddToggle(CallbackInfo ci)
    {
        InventorioScreen.addToggleButton((InventoryScreen) (Object) this);
    }
}