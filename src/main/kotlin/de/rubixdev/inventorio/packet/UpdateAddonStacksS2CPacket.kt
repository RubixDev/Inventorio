package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon
import de.rubixdev.inventorio.util.id
import java.util.concurrent.Executor
import net.minecraft.item.ItemStack
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload

data class UpdateAddonStacksS2CPacket(val updatedStacks: Map<Int, ItemStack>) : CustomPayload {
    companion object {
        val ID = CustomPayload.Id<UpdateAddonStacksS2CPacket>("update_addon_stacks".id)
        private val MAP_CODEC: PacketCodec<RegistryByteBuf, Map<Int, ItemStack>> =
            PacketCodecs.map(::LinkedHashMap, PacketCodecs.VAR_INT, ItemStack.OPTIONAL_PACKET_CODEC)
        val CODEC: PacketCodec<RegistryByteBuf, UpdateAddonStacksS2CPacket> =
            MAP_CODEC.xmap(::UpdateAddonStacksS2CPacket) { it.updatedStacks }
    }

    fun consume(executor: Executor) {
        executor.execute { PlayerInventoryAddon.Client.local?.receiveStacksUpdateS2C(updatedStacks) }
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID
}
