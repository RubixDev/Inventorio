package de.rubixdev.inventorio.api;

import de.rubixdev.inventorio.player.PlayerInventoryAddon;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface InventorioTickHandler {
    /**
     * Note: The size of the ToolBelt and the order of slots is persistent ONLY
     * within the current play session.<br>
     * The size of the ToolBelt may depend on the mods currently installed and
     * can change across restarts.
     */
    void tick(
        @NotNull PlayerInventoryAddon playerInventoryAddon,
        @NotNull InventorioAddonSection addonSection,
        @NotNull ItemStack itemStack,
        int indexWithinSection
    );
}
