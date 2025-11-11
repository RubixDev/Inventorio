package de.rubixdev.inventorio.mixin.neoforge.curios;

import de.rubixdev.inventorio.integration.curios.ICuriosContainer;
import de.rubixdev.inventorio.integration.curios.InventorioScreenHandlerMixinHelper;
import de.rubixdev.inventorio.player.InventorioScreenHandler;
import de.rubixdev.inventorio.util.CuriosTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.type.ICuriosMenu;

import java.util.List;

@SuppressWarnings("UnresolvedMixinReference") // the Minecraft Dev plugin
                                              // doesn't seem to like Kotlin
                                              // target classes
@Restriction(require = { @Condition("curios"), @Condition(type = Condition.Type.TESTER, tester = CuriosTester.class) })
@Mixin(InventorioScreenHandler.class)
public abstract class InventorioScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory>
    implements ICuriosContainer, ICuriosMenu {
    public InventorioScreenHandlerMixin(ScreenHandlerType<?> arg, int i) {
        super(arg, i);
    }

    @SuppressWarnings("DataFlowIssue")
    @Unique private final InventorioScreenHandler thiz = (InventorioScreenHandler) (AbstractRecipeScreenHandler<?>) this;

    @Unique private InventorioScreenHandlerMixinHelper helper;

    @Override
    public void resetSlots() {
        inventorio$resetSlots();
    }

    @Override
    public void inventorio$resetSlots() {
        helper.curios$resetSlots(thiz);
    }

    @Override
    public void inventorio$setPage(int page) {
        helper.curios$setPage(thiz, page);
    }

    @Override
    public void inventorio$toggleCosmetics() {
        helper.curios$toggleCosmetics(thiz);
    }

    @Override
    public void inventorio$nextPage() {
        helper.curios$nextPage(thiz);
    }

    @Override
    public void inventorio$prevPage() {
        helper.curios$prevPage(thiz);
    }

    @Override
    public void inventorio$checkQuickMove() {
        helper.curios$checkQuickMove(thiz);
    }

    @Override
    public int getInventorio$currentPage() { return helper.getCurrentPage(); }

    @Override
    public int getInventorio$totalPages() { return helper.getTotalPages(); }

    @Override
    public @NotNull List<Integer> getInventorio$grid() { return helper.getGrid(); }

    @Override
    public boolean getInventorio$hasCosmetics() { return helper.getHasCosmetics(); }

    @Override
    public boolean getInventorio$isViewingCosmetics() { return helper.isViewingCosmetics(); }

    @Override
    public int getInventorio$panelWidth() { return helper.getPanelWidth(); }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;)V", at = @At("RETURN"))
    private void curios$init(int syncId, PlayerInventory inventory, CallbackInfo ci) {
        helper = new InventorioScreenHandlerMixinHelper(thiz);
        helper.curios$init(thiz);
    }

    @Inject(method = "setStackInSlot", at = @At("HEAD"), cancellable = true)
    private void curios$setStackInSlot(int slot, int revision, ItemStack stack, CallbackInfo ci) {
        helper.curios$setStackInSlot(thiz, slot, ci);
    }

    @Inject(method = "quickMove", at = @At("HEAD"), cancellable = true)
    private void curios$quickMove(PlayerEntity player, int sourceIndex, CallbackInfoReturnable<ItemStack> cir) {
        helper.curios$quickMove(thiz, player, sourceIndex, cir);
    }
}
