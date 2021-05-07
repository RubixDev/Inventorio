package me.lizardofoz.inventorio.packet

import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object UseBoostRocketC2SPacket
{
    val identifier = Identifier("inventorio","fire_boost_rocket_c2s")

    fun consume(context: PacketContext, buf: PacketByteBuf)
    {
        context.player.inventoryAddon.fireRocketFromInventory()
    }
}