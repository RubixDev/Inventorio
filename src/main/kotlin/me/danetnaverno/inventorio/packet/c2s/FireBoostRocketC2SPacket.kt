package me.danetnaverno.inventorio.packet.c2s

import me.danetnaverno.inventorio.player.PlayerAddon
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object FireBoostRocketC2SPacket
{
    val identifier = Identifier("inventorio","fire_boost_rocket_c2s")

    fun consume(context: PacketContext, buf: PacketByteBuf)
    {
        PlayerAddon[context.player].fireRocketFromInventory()
    }
}