package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.util.MixinHelpers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = CreativeInventoryScreen.class, priority = 100)
@Environment(EnvType.CLIENT)
public class CreativeInventoryScreenMixin
{
    /**
     * This mixin fixes a visual bug when Player's Inventory Tab in the Creative GUI causes
     * addon slots (deep pockets, tool belt etc) to appear on top of the creative gui's hotbar
     */
    @Redirect(method = "setSelectedTab", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"), require = 0)
    private int inventorioRemoveExtraSlotsFromCreativeInventory(List<Slot> list)
    {
        int[] result = new int[1];
        MixinHelpers.withScreenHandlerAddon(MinecraftClient.getInstance().player,
                addon -> result[0] = addon.deepPocketsRange.getFirst(),
                player -> result[0] = player == null ? 45 : player.playerScreenHandler.slots.size());
        return result[0];
    }
}