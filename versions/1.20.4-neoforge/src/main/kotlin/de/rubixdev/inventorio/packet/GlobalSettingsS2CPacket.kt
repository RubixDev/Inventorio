package de.rubixdev.inventorio.packet

import com.google.gson.Gson
import com.google.gson.JsonObject
import de.rubixdev.inventorio.config.GlobalSettings
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import net.neoforged.neoforge.network.handling.PlayPayloadContext

class GlobalSettingsS2CPacket : CustomPayload {
    companion object {
        val identifier = Identifier("inventorio", "global_settings")
    }

    private var settingsJson: JsonObject

    // Sender's constructor
    constructor() {
        this.settingsJson = GlobalSettings.asJson()
    }

    // Receiver's constructor
    constructor(buf: PacketByteBuf) {
        this.settingsJson = Gson().fromJson(buf.readString(), JsonObject::class.java)
    }

    override fun id(): Identifier = identifier

    // Sender's writer
    override fun write(buf: PacketByteBuf) {
        buf.writeString(settingsJson.toString())
    }

    // Receiver's consumer
    fun consume(context: PlayPayloadContext) {
        context.workHandler.execute {
            GlobalSettings.syncFromServer(settingsJson)
        }
    }
}
