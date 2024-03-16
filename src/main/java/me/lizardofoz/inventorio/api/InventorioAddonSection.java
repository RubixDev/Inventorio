package me.lizardofoz.inventorio.api;

/**
 * @deprecated Use {@link de.rubixdev.inventorio.api.InventorioAddonSection}
 *             instead.
 */
@Deprecated(forRemoval = true, since = "1.10.0")
public enum InventorioAddonSection {
    DEEP_POCKETS,
    TOOLBELT,
    UTILITY_BELT;

    static InventorioAddonSection from(de.rubixdev.inventorio.api.InventorioAddonSection section) {
        return switch (section) {
            case DEEP_POCKETS -> DEEP_POCKETS;
            case TOOLBELT -> TOOLBELT;
            case UTILITY_BELT -> UTILITY_BELT;
        };
    }
}
