package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon
import java.util.function.Supplier
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.network.NetworkEvent

class UpdateAddonStacksS2CPacket {
    private var updatedStacks: Map<Int, ItemStack>

    // Sender's constructor
    constructor(updatedStacks: Map<Int, ItemStack>) {
        this.updatedStacks = updatedStacks
    }

    // Receiver's constructor
    constructor(buf: PacketByteBuf) {
        val size = buf.readInt()
        val updatedStacks = mutableMapOf<Int, ItemStack>()
        for (i in 0 until size)
            updatedStacks[buf.readInt()] = buf.readItemStack()
        this.updatedStacks = updatedStacks
    }

    // Sender's writer
    fun write(buf: PacketByteBuf) {
        buf.writeInt(updatedStacks.size)
        for ((index, stack) in updatedStacks) {
            buf.writeInt(index)
            buf.writeItemStack(stack)
        }
    }

    // Receiver's consumer
    fun consume(supplier: Supplier<NetworkEvent.Context>) {
        supplier.get().enqueueWork {
            PlayerInventoryAddon.Client.local?.receiveStacksUpdateS2C(updatedStacks)
        }
        supplier.get().packetHandled = true
    }
}
