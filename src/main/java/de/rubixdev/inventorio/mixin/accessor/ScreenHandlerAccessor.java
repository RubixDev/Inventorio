package de.rubixdev.inventorio.mixin.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("unused")
@Mixin(ScreenHandler.class)
public interface ScreenHandlerAccessor {
    @Accessor
    DefaultedList<ItemStack> getTrackedStacks();

    @Accessor
    DefaultedList<ItemStack> getPreviousTrackedStacks();

    @Invoker
    Slot callAddSlot(Slot slot);

    @Invoker
    boolean callInsertItem(ItemStack stack, int firstIndex, int lastIndex, boolean fromLast);
}
