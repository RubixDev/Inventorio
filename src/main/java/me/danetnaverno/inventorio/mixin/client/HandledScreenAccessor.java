package me.danetnaverno.inventorio.mixin.client;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor extends ScreenAccessor
{
    @Accessor("backgroundWidth")
    int getBackgroundWidth();

    @Accessor("backgroundWidth")
    void setBackgroundWidth(int value);

    @Accessor("backgroundHeight")
    int getBackgroundHeight();

    @Accessor("backgroundHeight")
    void setBackgroundHeight(int value);

    @Accessor("playerInventoryTitleX")
    int etPlayerInventoryTitleX();

    @Accessor("playerInventoryTitleX")
    void setPlayerInventoryTitleX(int value);

    @Accessor("playerInventoryTitleY")
    void setPlayerInventoryTitleY(int value);

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

    @Accessor("titleY")
    int getTitleY();

    @Accessor("titleY")
    void setTitleY(int value);

}
