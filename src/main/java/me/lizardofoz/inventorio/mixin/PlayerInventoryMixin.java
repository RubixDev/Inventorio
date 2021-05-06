package me.lizardofoz.inventorio.mixin;

import me.lizardofoz.inventorio.mixin.accessor.PlayerInventoryAccessor;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import me.lizardofoz.inventorio.util.InventoryDuck;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is genuinely painful to look at and breaks all sorts of OOP conventions,
 *  but hey, this is what you gotta do when you deal with mixins, APIs and mod compatibility.
 */
@Mixin(value = PlayerInventory.class, priority = 9999)
public class PlayerInventoryMixin implements InventoryDuck
{
    @Unique public PlayerInventoryAddon addon;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private <E> void createInventoryAddon(PlayerEntity player, CallbackInfo ci)
    {
        addon = new PlayerInventoryAddon((PlayerInventory)(Object)this);
        ((PlayerInventoryAccessor)this).setCombinedInventory(addon.getCombinedInventory());
    }

    /**
     * @reason Inventorio changes the size of the Player's Inventory.
     * It adds the Utility Belt, the Tool Belt and the Extension Slots from the Deep Pockets Enchantment
     *
     * If you see this error, another mod tries to modify the player's inventory capacity
     * and require compatibility changes by the mod Developers
     * @author LizardOfOz
     */
    @Overwrite
    public int size()
    {
        return addon.size();
    }

    @Override
    public PlayerInventoryAddon getAddon()
    {
        return addon;
    }
}
