package me.danetnaverno.inventorio.mixin;

import me.danetnaverno.inventorio.packet.InventorioNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin
{
    @Shadow public ServerPlayerEntity player;

    /**
     * This inject sends gameplay-impacting Player's settings (QuickBar Mode and UtilityBelt Mode) from the server to the player
     */
    @Inject(method = "onClientSettings", at = @At(value = "RETURN"))
    private void setPlayerSettingsBack(ClientSettingsC2SPacket packet, CallbackInfo ci)
    {
        InventorioNetworking.INSTANCE.S2CSendPlayerSettings(this.player);
    }

    /**
     * This inject rebuilds the block list of the creative inventory.
     * If you know how to change the "45" constant instead of just making the same method again, please let me know.
     * //todo find a way
     */
    @Inject(method = "onCreativeInventoryAction", at = @At(value = "RETURN"))
    private void creativeSlotUpdateFix(CreativeInventoryActionC2SPacket packet, CallbackInfo ci)
    {
        if (this.player.interactionManager.isCreative())
        {
            ItemStack itemStack = packet.getItemStack();
            CompoundTag compoundTag = itemStack.getSubTag("BlockEntityTag");
            if (!itemStack.isEmpty() && compoundTag != null && compoundTag.contains("x") && compoundTag.contains("y") && compoundTag.contains("z"))
            {
                BlockPos blockPos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
                BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
                if (blockEntity != null)
                {
                    CompoundTag compoundTag2 = blockEntity.toTag(new CompoundTag());
                    compoundTag2.remove("x");
                    compoundTag2.remove("y");
                    compoundTag2.remove("z");
                    itemStack.putSubTag("BlockEntityTag", compoundTag2);
                }
            }

            PlayerScreenHandler handler = this.player.playerScreenHandler;
            boolean bl2 = packet.getSlot() >= 0 && packet.getSlot() < handler.slots.size();
            boolean bl3 = itemStack.isEmpty() || itemStack.getDamage() >= 0 && itemStack.getCount() <= 64 && !itemStack.isEmpty();
            if (bl2 && bl3)
            {
                if (itemStack.isEmpty())
                    handler.setStackInSlot(packet.getSlot(), ItemStack.EMPTY);
                else
                    handler.setStackInSlot(packet.getSlot(), itemStack);

                handler.setPlayerRestriction(this.player, true);
                handler.sendContentUpdates();
            }
        }
    }
}