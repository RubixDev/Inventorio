package me.lizardofoz.inventorio.api;

import de.rubixdev.inventorio.player.PlayerInventoryAddon;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated Use {@link de.rubixdev.inventorio.api.InventorioTickHandler}
 *             instead.
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true, since = "1.10.0")
@FunctionalInterface
public interface InventorioTickHandler {
    /**
     * Note: The size of the ToolBelt and the order of slots is persistent ONLY
     * within the current play session.<br>
     * The size of the ToolBelt may depend on the mods currently installed and
     * can change across restarts.
     * 
     * @deprecated Use
     *             {@link de.rubixdev.inventorio.api.InventorioTickHandler#tick}
     *             instead.
     */
    @Deprecated(forRemoval = true, since = "1.10.0")
    void tick(
        @NotNull PlayerInventoryAddon playerInventoryAddon,
        @NotNull InventorioAddonSection addonSection,
        @NotNull ItemStack itemStack,
        int indexWithinSection
    );
}
