package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import net.neoforged.neoforge.network.handling.PlayPayloadContext

class UpdateAddonStacksS2CPacket : CustomPayload {
    companion object {
        val identifier = Identifier("inventorio", "update_addon_stacks")
    }
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

    override fun id(): Identifier = identifier

    // Sender's writer
    override fun write(buf: PacketByteBuf) {
        buf.writeInt(updatedStacks.size)
        for ((index, stack) in updatedStacks) {
            buf.writeInt(index)
            buf.writeItemStack(stack)
        }
    }

    // Receiver's consumer
    fun consume(context: PlayPayloadContext) {
        context.workHandler.execute {
            PlayerInventoryAddon.Client.local?.receiveStacksUpdateS2C(updatedStacks)
        }
    }
}
