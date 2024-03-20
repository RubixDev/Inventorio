package de.rubixdev.inventorio.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import de.rubixdev.inventorio.player.InventorioScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @WrapWithCondition(
        method = "openHandledScreen",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;closeHandledScreen()V")
    )
    private boolean keepMousePositionWhenTogglingInventorioScreen(
        ServerPlayerEntity instance,
        NamedScreenHandlerFactory factory
    ) {
        return !(factory.createMenu(0, this.getInventory(), this) instanceof InventorioScreenHandler);
    }
}
