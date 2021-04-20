package me.lizardofoz.inventorio.mixin.accessor;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleInventory.class)
public interface SimpleInventoryAccessor
{
    @Accessor("size")
    void setSize(int size);

    @Accessor("stacks")
    void setStacks(DefaultedList<ItemStack> stacks);
}
