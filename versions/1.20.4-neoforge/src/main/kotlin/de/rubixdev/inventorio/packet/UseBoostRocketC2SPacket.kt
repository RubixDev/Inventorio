package de.rubixdev.inventorio.packet

import de.rubixdev.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import kotlin.jvm.optionals.getOrNull
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import net.neoforged.neoforge.network.handling.PlayPayloadContext

class UseBoostRocketC2SPacket : CustomPayload {
    companion object {
        val identifier = Identifier("inventorio", "fire_boost_rocket_c2s")
    }

    override fun id(): Identifier = identifier

    override fun write(buf: PacketByteBuf?) {}

    fun consume(context: PlayPayloadContext) {
        val sender = context.player.getOrNull() ?: return
        context.workHandler.execute {
            sender.inventoryAddon?.fireRocketFromInventory()
        }
    }
}
