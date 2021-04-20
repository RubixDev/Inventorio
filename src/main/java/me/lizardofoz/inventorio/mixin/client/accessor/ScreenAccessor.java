package me.lizardofoz.inventorio.mixin.client.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
@Environment(EnvType.CLIENT)
public interface ScreenAccessor
{
    @Accessor("buttons")
    List<AbstractButtonWidget> getButtons();

    @Invoker("addButton")
    <T extends AbstractButtonWidget> T addAButton(T button);
}
