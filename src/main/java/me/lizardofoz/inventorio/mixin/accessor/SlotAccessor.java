package me.lizardofoz.inventorio.mixin.accessor;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotAccessor
{
    @Accessor("x")
    int getX();

    @Accessor("x")
    void setX(int x);

    @Accessor("y")
    int getY();

    @Accessor("y")
    void setY(int y);

    @Accessor("index")
    int getIndex();
}
