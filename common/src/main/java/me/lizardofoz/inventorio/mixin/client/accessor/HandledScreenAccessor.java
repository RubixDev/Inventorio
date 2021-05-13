package me.lizardofoz.inventorio.mixin.client.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
@Environment(EnvType.CLIENT)
public interface HandledScreenAccessor
{
    @Accessor("backgroundHeight")
    int getBackgroundHeight();

    @Accessor("backgroundHeight")
    void setBackgroundHeight(int value);

    @Accessor("backgroundWidth")
    int getBackgroundWidth();

    @Accessor("backgroundWidth")
    void setBackgroundWidth(int value);

    @Accessor("x")
    int getX();

    @Accessor("x")
    void setX(int x);

    @Accessor("y")
    int getY();

    @Accessor("y")
    void setY(int y);

    @Accessor("titleX")
    int getTitleX();

    @Accessor("titleX")
    void setTitleX(int value);
}
