package me.lizardofoz.inventorio.api;

import com.google.common.collect.ImmutableList;
import de.rubixdev.inventorio.config.GlobalSettings;
import me.lizardofoz.inventorio.client.ui.InventorioScreen;
import me.lizardofoz.inventorio.player.InventorioScreenHandler;
import me.lizardofoz.inventorio.player.PlayerInventoryAddon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

/**
 * @deprecated Use {@link de.rubixdev.inventorio.api.InventorioAPI} instead.
 */
@SuppressWarnings({ "unused", "removal" })
@Deprecated(forRemoval = true, since = "1.10.0")
public final class InventorioAPI {
    private InventorioAPI() {}

    /**
     * Items within vanilla Player Inventory can tick
     * ({@link ItemStack#inventoryTick}).<br>
     * This method opens mod authors the ability to make their items tick within
     * Inventorio Addon slots.<br>
     * Note: each tick handler gets ran within its own try-catch block.<br>
     * Unfortunately, because the vanilla ticking needs to be supplied with a
     * VANILLA slot id, it denies the possibility to inject Inventorio ticking
     * into it.
     *
     * @deprecated Use
     *             {@link de.rubixdev.inventorio.api.InventorioAPI#registerInventoryTickHandler}
     *             instead
     */
    @Deprecated(forRemoval = true, since = "1.10.0")
    public static void registerInventoryTickHandler(
        @NotNull Identifier customIdentifier,
        @NotNull InventorioTickHandler tickHandler
    ) {
        de.rubixdev.inventorio.api.InventorioAPI.registerInventoryTickHandler(
            customIdentifier,
            (addon, section, stack, index) -> tickHandler
                .tick(PlayerInventoryAddon.ofNonNull(addon), InventorioAddonSection.from(section), stack, index)
        );
    }

    /**
     * Note: each consumer get ran within its own try-catch block
     * 
     * @deprecated Use
     *             {@link de.rubixdev.inventorio.api.InventorioAPI#registerScreenHandlerOpenConsumer}
     *             instead
     */
    @Deprecated(forRemoval = true, since = "1.10.0")
    public static void registerScreenHandlerOpenConsumer(
        @NotNull Identifier customIdentifier,
        Consumer<InventorioScreenHandler> screenHandlerConsumer
    ) {
        de.rubixdev.inventorio.api.InventorioAPI.registerScreenHandlerOpenConsumer(
            customIdentifier,
            handler -> screenHandlerConsumer.accept(InventorioScreenHandler.of(handler))
        );
    }

    /**
     * Note: each consumer get ran within its own try-catch block
     * 
     * @deprecated Use
     *             {@link de.rubixdev.inventorio.api.InventorioAPI#registerInventoryUIInitConsumer}
     *             instead
     */
    @Deprecated(forRemoval = true, since = "1.10.0")
    @Environment(EnvType.CLIENT)
    public static void registerInventoryUIInitConsumer(
        @NotNull Identifier customIdentifier,
        Consumer<InventorioScreen> uiConsumer
    ) {
        de.rubixdev.inventorio.api.InventorioAPI.registerInventoryUIInitConsumer(
            customIdentifier,
            screen -> uiConsumer.accept(InventorioScreen.of(screen))
        );
    }

    /**
     * @param slotName  Unique string id for the slot. Default slots available
     *                  at {@link InventorioAPI} constants.
     * @param emptyIcon Identifier/ResourceLocation that leads to an icon, e.g.
     *                  <code>new Identifier("your_mod", "textures/gui/empty/your_tool_slot.png")</code>
     * @return If <code>slotName</code> has already been taken, returns the
     *         existing {@link ToolBeltSlotTemplate}.<br>
     *         Creates a new one and returns it otherwise.<br>
     *         Returns NULL if {@link GlobalSettings#toolBeltMode} is set to
     *         <code>DISABLED</code>
     * @throws IllegalStateException when attempted to add a new Template post
     *                               initialization.<br>
     *                               No new ToolBelt slots can be added after
     *                               the first player has been spawned.
     * @deprecated Use
     *             {@link de.rubixdev.inventorio.api.InventorioAPI#registerToolBeltSlotIfNotExists}
     *             instead
     */
    @Deprecated(forRemoval = true, since = "1.10.0")
    @Nullable public static ToolBeltSlotTemplate registerToolBeltSlotIfNotExists(
        @NotNull String slotName,
        @NotNull Identifier emptyIcon
    ) {
        return ToolBeltSlotTemplate
            .of(de.rubixdev.inventorio.api.InventorioAPI.registerToolBeltSlotIfNotExists(slotName, emptyIcon));
    }

    /**
     * Please consider the existence of {@link GlobalSettings#toolBeltMode} when
     * calling this method.<br>
     * When set to anything but <code>ENABLED</code>, the standard tool belt
     * slots might be missing.
     * 
     * @param slotName Unique string id for the slot. Default slots available at
     *                 {@link InventorioAPI} constants.
     * @deprecated Use
     *             {@link de.rubixdev.inventorio.api.InventorioAPI#getToolBeltSlotTemplate}
     *             instead
     */
    @Deprecated(forRemoval = true, since = "1.10.0")
    @Nullable public static ToolBeltSlotTemplate getToolBeltSlotTemplate(@NotNull String slotName) {
        return ToolBeltSlotTemplate.of(de.rubixdev.inventorio.api.InventorioAPI.getToolBeltSlotTemplate(slotName));
    }

    /**
     * Please consider the existence of {@link GlobalSettings#toolBeltMode} when
     * calling this method.<br>
     * When set to anything but <code>ENABLED</code>, the standard tool belt
     * slots might be missing.
     * 
     * @return ItemStack.Empty if no fitting slot was found
     * @deprecated Use
     *             {@link de.rubixdev.inventorio.api.InventorioAPI#findFittingToolBeltStack}
     *             instead
     */
    @Deprecated(forRemoval = true, since = "1.10.0")
    @NotNull public static ItemStack findFittingToolBeltStack(
        @NotNull PlayerInventoryAddon playerInventoryAddon,
        @NotNull ItemStack sampleStack
    ) {
        return de.rubixdev.inventorio.api.InventorioAPI.findFittingToolBeltStack(playerInventoryAddon, sampleStack);
    }

    /**
     * Please consider the existence of {@link GlobalSettings#toolBeltMode} when
     * calling this method.<br>
     * When set to anything but <code>ENABLED</code>, the standard tool belt
     * slots might be missing.
     * 
     * @return -1 if no fitting slot was found
     * @deprecated Use
     *             {@link de.rubixdev.inventorio.api.InventorioAPI#findFittingToolBeltIndex}
     *             instead
     */
    @Deprecated(forRemoval = true, since = "1.10.0")
    public static int findFittingToolBeltIndex(
        @NotNull PlayerInventoryAddon playerInventoryAddon,
        @NotNull ItemStack sampleStack
    ) {
        return de.rubixdev.inventorio.api.InventorioAPI.findFittingToolBeltIndex(playerInventoryAddon, sampleStack);
    }

    /**
     * @see InventorioAPI#registerToolBeltSlotIfNotExists
     * @deprecated Use
     *             {@link de.rubixdev.inventorio.api.InventorioAPI#getToolBeltTemplates}
     *             instead
     */
    @Deprecated(forRemoval = true, since = "1.10.0")
    @NotNull public static ImmutableList<ToolBeltSlotTemplate> getToolBeltTemplates() {
        return ImmutableList.copyOf(
            de.rubixdev.inventorio.api.InventorioAPI.getToolBeltTemplates()
                .stream()
                .map(ToolBeltSlotTemplate::of)
                .toList()
        );
    }

    /**
     * @deprecated Use
     *             {@link de.rubixdev.inventorio.api.InventorioAPI#getInventoryAddon}
     *             instead
     */
    @Deprecated(forRemoval = true, since = "1.10.0")
    @Nullable public static PlayerInventoryAddon getInventoryAddon(@NotNull PlayerEntity playerEntity) {
        return PlayerInventoryAddon.of(de.rubixdev.inventorio.api.InventorioAPI.getInventoryAddon(playerEntity));
    }
}
