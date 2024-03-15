package me.lizardofoz.inventorio.mixin.accessor;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("unused")
@Mixin(Slot.class)
public interface SlotAccessor {
    @Accessor("x")
    int getX();

    @Accessor("x")
    @Mutable
    void setX(int x);

    @Accessor("y")
    int getY();

    @Accessor("y")
    @Mutable
    void setY(int y);
}
