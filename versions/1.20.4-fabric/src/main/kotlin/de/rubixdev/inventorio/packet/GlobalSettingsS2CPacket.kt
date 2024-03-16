package de.rubixdev.inventorio.packet

import com.google.gson.Gson
import com.google.gson.JsonObject
import de.rubixdev.inventorio.config.GlobalSettings
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

@Suppress("UNUSED_PARAMETER")
object GlobalSettingsS2CPacket {
    val identifier = Identifier("inventorio", "global_settings")

    fun consume(client: MinecraftClient, handler: ClientPlayNetworkHandler, buf: PacketByteBuf, responseSender: PacketSender) {
        val jsonString = buf.readString()
        val settingsJson = Gson().fromJson(jsonString, JsonObject::class.java)
        client.execute {
            GlobalSettings.syncFromServer(settingsJson)
        }
    }

    fun write(buf: PacketByteBuf) {
        buf.writeString(GlobalSettings.asJson().toString())
    }
}
