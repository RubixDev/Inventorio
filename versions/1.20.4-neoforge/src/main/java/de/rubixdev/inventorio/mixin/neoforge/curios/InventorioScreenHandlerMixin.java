package de.rubixdev.inventorio.mixin.neoforge.curios;

import de.rubixdev.inventorio.integration.curios.ICuriosContainer;
import de.rubixdev.inventorio.integration.curios.InventorioScreenHandlerMixinHelper;
import de.rubixdev.inventorio.player.InventorioScreenHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@SuppressWarnings("UnresolvedMixinReference") // the Minecraft Dev plugin
                                              // doesn't seem to like Kotlin
                                              // target classes
@Restriction(require = @Condition("curios"))
@Mixin(InventorioScreenHandler.class)
public abstract class InventorioScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory>
    implements ICuriosContainer {
    public InventorioScreenHandlerMixin(ScreenHandlerType<?> arg, int i) {
        super(arg, i);
    }

    @SuppressWarnings("DataFlowIssue")
    @Unique private final InventorioScreenHandler thiz = (InventorioScreenHandler) (AbstractRecipeScreenHandler<?>) this;

    @Unique private InventorioScreenHandlerMixinHelper helper;

    @Override
    public void inventorio$resetSlots() {
        helper.curios$resetSlots(thiz);
    }

    @Override
    public void inventorio$scrollTo(float pos) {
        helper.curios$scrollTo(thiz, pos);
    }

    @Override
    public void inventorio$scrollToIndex(int indexIn) {
        helper.curios$scrollToIndex(thiz, indexIn);
    }

    @Override
    public boolean getInventorio$hasCosmeticColumn() { return helper.hasCosmeticColumn(); }

    @Override
    public boolean getInventorio$canScroll() { return helper.canScroll(); }

    @Nullable @Override
    public ICuriosItemHandler getInventorio$curiosHandler() { return helper.getCuriosHandler(); }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;)V", at = @At("RETURN"))
    private void curios$init(int syncId, PlayerInventory inventory, CallbackInfo ci) {
        helper = new InventorioScreenHandlerMixinHelper(thiz);
        helper.curios$init(thiz);
    }

    @Inject(method = "setStackInSlot", at = @At("HEAD"), cancellable = true)
    private void curios$setStackInSlot(int slot, int revision, ItemStack stack, CallbackInfo ci) {
        helper.curios$setStackInSlot(thiz, slot, ci);
    }

    @Inject(method = "quickMoveInner", at = @At("HEAD"), cancellable = true)
    private void curios$quickMoveInner(int sourceIndex, CallbackInfoReturnable<ItemStack> cir) {
        helper.curios$quickMoveInner(thiz, sourceIndex, cir);
    }
}
