package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.client.ui.CreativeScreenHandlerAddon;
import me.lizardofoz.inventorio.player.PlayerAddon;
import me.lizardofoz.inventorio.util.GeneralConstantsKt;
import me.lizardofoz.inventorio.util.HandlerDuck;
import me.lizardofoz.inventorio.util.ScreenHandlerAddon;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin attaches {@link CreativeScreenHandlerAddon} to {@link CreativeInventoryScreen.CreativeScreenHandler}
 */
@Mixin(CreativeInventoryScreen.CreativeScreenHandler.class)
public class CreativeScreenHandlerMixin implements HandlerDuck
{
    @Unique public CreativeScreenHandlerAddon addon;
    @Shadow @Final public DefaultedList<ItemStack> itemList;

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
        return this.itemList.size() > GeneralConstantsKt.INVENTORIO_ROW_LENGTH * 5;
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