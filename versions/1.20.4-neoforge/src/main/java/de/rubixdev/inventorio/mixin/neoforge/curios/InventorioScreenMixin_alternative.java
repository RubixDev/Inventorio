package de.rubixdev.inventorio.mixin.neoforge.curios;

import de.rubixdev.inventorio.client.ui.InventorioScreen;
import de.rubixdev.inventorio.integration.curios.CustomCuriosButton;
import de.rubixdev.inventorio.player.InventorioScreenHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.client.gui.CuriosButton;
import top.theillusivec4.curios.client.gui.CuriosScreen;
import top.theillusivec4.curios.common.network.client.CPacketOpenCurios;

import static de.rubixdev.inventorio.integration.curios.InventorioScreenHandlerMixinHelperKt.sendToServer;

@SuppressWarnings("UnresolvedMixinReference") // the Minecraft Dev plugin
                                              // doesn't seem to like Kotlin
                                              // target classes
@Restriction(
    require = @Condition("curios"),
    conflict = @Condition(
        type = Condition.Type.MIXIN,
        value = "de.rubixdev.inventorio.mixin.neoforge.curios.InventorioScreenMixin"
    )
)
@Mixin(InventorioScreen.class)
public abstract class InventorioScreenMixin_alternative extends AbstractInventoryScreen<InventorioScreenHandler> {
    public InventorioScreenMixin_alternative(InventorioScreenHandler arg, PlayerInventory arg2, Text arg3) {
        super(arg, arg2, arg3);
    }

    @Shadow
    private RecipeBookWidget recipeBook;

    @Inject(method = "init", at = @At("RETURN"))
    private void curios$init(CallbackInfo ci) {
        Pair<Integer, Integer> offsets = CuriosScreen.getButtonOffset(false);
        addDrawableChild(
            new CustomCuriosButton(
                this,
                x + offsets.getLeft() + 2,
                height / 2 + offsets.getRight() + 2,
                10,
                10,
                CuriosButton.BIG,
                button -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null) {
                        ItemStack stack = client.player.currentScreenHandler.getCursorStack();
                        client.player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);

                        if (recipeBook.isOpen()) recipeBook.toggleOpen();
                        sendToServer(new CPacketOpenCurios(stack));
                    }
                }
            )
        );
    }
}
