package me.danetnaverno.inventorio.mixin.client;

import me.danetnaverno.inventorio.player.CreativeScreenHandlerAddon;
import me.danetnaverno.inventorio.player.PlayerAddon;
import me.danetnaverno.inventorio.util.GeneralConstantsKt;
import me.danetnaverno.inventorio.util.HandlerDuck;
import me.danetnaverno.inventorio.util.ScreenHandlerAddon;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.CreativeScreenHandler.class)
public class CreativeScreenHandlerMixin implements HandlerDuck
{
    @Shadow
    @Final
    public DefaultedList<ItemStack> itemList;
    @Unique
    public CreativeScreenHandlerAddon addon;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void createHandlerAddon(PlayerEntity playerEntity, CallbackInfo ci)
    {
        PlayerAddon playerAddon = PlayerAddon.get(playerEntity);
        addon = new CreativeScreenHandlerAddon((CreativeInventoryScreen.CreativeScreenHandler) (Object) this);
        addon.initialize(playerAddon);
    }

    @Overwrite
    public void scrollItems(float position)
    {
        if (addon != null)
            addon.scrollItems(itemList, position);
    }

    @Overwrite
    public boolean shouldShowScrollbar()
    {
        return this.itemList.size() > GeneralConstantsKt.inventorioRowLength * 5;
    }

    @Override
    public ScreenHandlerAddon getAddon()
    {
        return addon;
    }

    @Override
    public void setAddon(ScreenHandlerAddon addon)
    {
        this.addon = (CreativeScreenHandlerAddon) addon;
    }
}