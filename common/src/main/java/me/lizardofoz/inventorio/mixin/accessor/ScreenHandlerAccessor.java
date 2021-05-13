package me.lizardofoz.inventorio.mixin.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerAccessor
{
    @Invoker("addSlot")
    Slot addASlot(Slot slot);

    @Invoker("insertItem")
    boolean insertAnItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast);

    @Accessor("trackedStacks")
    DefaultedList<ItemStack> getTrackedSlots();
}
