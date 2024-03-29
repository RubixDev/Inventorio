package de.rubixdev.inventorio.mixin.fabric.trinkets;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.rubixdev.inventorio.client.ui.InventorioScreen;
import de.rubixdev.inventorio.player.InventorioScreenHandler;
import de.rubixdev.inventorio.util.TrinketsTester;
import dev.emi.trinkets.api.SlotGroup;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
    require = { @Condition("trinkets"), @Condition(type = Condition.Type.TESTER, tester = TrinketsTester.class) }
)
@Mixin(SlotGroup.class)
public class SlotGroupMixin {
    @ModifyReturnValue(method = "getSlotId", at = @At("RETURN"), remap = false)
    private int overwriteOffhandId(int original) {
        // when injecting Trinkets in the Inventorio screen we change groups
        // associated with the offhand slot to the first utility belt slot
        // instead
        if (
            original == PlayerScreenHandler.OFFHAND_ID
                && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                && MinecraftClient.getInstance().currentScreen instanceof InventorioScreen
        ) {
            return InventorioScreenHandler.utilityBeltRange.getFirst();
        }
        return original;
    }
}
