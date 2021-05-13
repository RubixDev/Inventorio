package me.lizardofoz.inventorio.mixin.client;

import me.lizardofoz.inventorio.util.GeneralConstantsKt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
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
    public int removeExtraPlayerSlotsFromInventory(List<Slot> list)
    {
        return GeneralConstantsKt.getHANDLER_ADDON_DUD_OFFHAND_RANGE().getLast();
    }
}