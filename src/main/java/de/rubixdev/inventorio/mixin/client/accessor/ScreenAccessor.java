package de.rubixdev.inventorio.mixin.client.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
@Environment(EnvType.CLIENT)
public interface ScreenAccessor {
    @Accessor("children")
    List<Element> getChildren();

    @Accessor("drawables")
    List<Drawable> getDrawables();

    @Accessor("selectables")
    List<Selectable> getSelectables();

    @Accessor
    @Nullable MinecraftClient getClient();

    @Accessor
    TextRenderer getTextRenderer();

    @Invoker
    <T extends Element & Drawable> T callAddDrawableChild(T drawableElement);
}
