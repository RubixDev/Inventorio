package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.player.PlayerScreenHandlerAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = CreativeInventoryScreen.class, priority = 500)
@Environment(EnvType.CLIENT)
public abstract class CreativeInventoryScreenMixin
{
    /**
     * This mixin fixes a visual bug when Player's Inventory Tab in the Creative GUI causes
     * addon slots (deep pockets, tool belt etc) to appear on top of the gui's hotbar
     */
    @Redirect(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"), require = 0)
    public int inventorioRemoveExtraPlayerSlotsFromInventory(List<Slot> list)
    {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null)
            return 45;
        PlayerScreenHandlerAddon addon = PlayerScreenHandlerAddon.Companion.getScreenHandlerAddon(player);
        if (addon == null)
            return player.playerScreenHandler.slots.size();
        return addon.getDeepPocketsRange().getFirst();
    }
}