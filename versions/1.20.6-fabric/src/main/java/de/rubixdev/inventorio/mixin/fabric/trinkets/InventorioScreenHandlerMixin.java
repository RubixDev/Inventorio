package de.rubixdev.inventorio.mixin.fabric.trinkets;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.rubixdev.inventorio.integration.trinkets.InventorioScreenHandlerMixinHelper;
import de.rubixdev.inventorio.player.InventorioScreenHandler;
import de.rubixdev.inventorio.util.TrinketsTester;
import dev.emi.trinkets.Point;
import dev.emi.trinkets.TrinketPlayerScreenHandler;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@SuppressWarnings("UnresolvedMixinReference") // the Minecraft Dev plugin
                                              // doesn't seem to like Kotlin
                                              // target classes
@Restriction(
    require = { @Condition("trinkets"), @Condition(type = Condition.Type.TESTER, tester = TrinketsTester.class) }
)
@Mixin(InventorioScreenHandler.class)
public abstract class InventorioScreenHandlerMixin extends ScreenHandler implements TrinketPlayerScreenHandler {
    protected InventorioScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @SuppressWarnings("DataFlowIssue")
    @Unique private final InventorioScreenHandler thiz = (InventorioScreenHandler) (ScreenHandler) this;

    @Unique private InventorioScreenHandlerMixinHelper helper;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void trinkets$init(int syncId, PlayerInventory inventory, CallbackInfo ci) {
        helper = new InventorioScreenHandlerMixinHelper(thiz);
        helper.trinkets$updateTrinketSlots(true);
    }

    @Override
    public void trinkets$updateTrinketSlots(boolean slotsChanged) {
        helper.trinkets$updateTrinketSlots(slotsChanged);
    }

    @Override
    public int trinkets$getGroupNum(SlotGroup group) {
        return helper.trinkets$getGroupNum(group);
    }

    @Override
    public @Nullable Point trinkets$getGroupPos(SlotGroup group) {
        return helper.trinkets$getGroupPos(group);
    }

    @Override
    public @NotNull List<Point> trinkets$getSlotHeights(SlotGroup group) {
        return helper.trinkets$getSlotHeights(group);
    }

    @Override
    public @Nullable Point trinkets$getSlotHeight(SlotGroup group, int i) {
        return helper.trinkets$getSlotHeight(group, i);
    }

    @Override
    public @NotNull List<SlotType> trinkets$getSlotTypes(SlotGroup group) {
        return helper.trinkets$getSlotTypes(group);
    }

    @Override
    public int trinkets$getSlotWidth(SlotGroup group) {
        return helper.trinkets$getSlotWidth(group);
    }

    @Override
    public int trinkets$getGroupCount() {
        return helper.trinkets$getGroupCount();
    }

    @Override
    public int trinkets$getTrinketSlotStart() {
        return helper.trinkets$getTrinketSlotStart();
    }

    @Override
    public int trinkets$getTrinketSlotEnd() {
        return helper.trinkets$getTrinketSlotEnd();
    }

    @Inject(method = "onClosed", at = @At("HEAD"))
    private void trinkets$onClosed(PlayerEntity player, CallbackInfo ci) {
        helper.onClosed(player);
    }

    @Inject(method = "quickMove", at = @At("HEAD"), cancellable = true)
    private void trinkets$quickMove(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir) {
        helper.trinkets$quickMove(thiz, player, index, cir);
    }

    @Inject(method = "updateDeepPocketsCapacity", at = @At("TAIL"), remap = false)
    private void trinkets$updateDeepPocketsCapacity(CallbackInfo ci) {
        if (helper != null) {
            helper.trinkets$updateTrinketSlots(false);
        }
    }

    @ModifyReturnValue(method = "getToolBeltSlotCount", at = @At("RETURN"), remap = false)
    private int trinkets$addExtraGroups(int original) {
        return original + InventorioScreenHandlerMixinHelper.getGroupCount(thiz.getInventory().player);
    }
}
