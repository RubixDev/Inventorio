package de.rubixdev.inventorio.packet

import com.google.gson.Gson
import com.google.gson.JsonObject
import de.rubixdev.inventorio.config.GlobalSettings
import io.netty.buffer.ByteBuf
import java.util.concurrent.Executor
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

data class GlobalSettingsS2CPacket(val settings: JsonObject = GlobalSettings.asJson()) : CustomPayload {
    companion object {
        val ID = CustomPayload.Id<GlobalSettingsS2CPacket>(Identifier("inventorio", "global_settings"))
        val CODEC: PacketCodec<ByteBuf, GlobalSettingsS2CPacket> = PacketCodecs.STRING.xmap(
            { jsonString -> GlobalSettingsS2CPacket(Gson().fromJson(jsonString, JsonObject::class.java)) },
            { packet -> packet.settings.toString() },
        )
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    fun consume(executor: Executor) {
        executor.execute { GlobalSettings.syncFromServer(settings) }
    }
}
