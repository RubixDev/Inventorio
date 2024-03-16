package de.rubixdev.inventorio.packet

import com.google.gson.Gson
import com.google.gson.JsonObject
import de.rubixdev.inventorio.config.GlobalSettings
import net.minecraft.network.PacketByteBuf
import net.neoforged.neoforge.network.NetworkEvent

class GlobalSettingsS2CPacket {
    private var settingsJson: JsonObject

    // Sender's constructor
    constructor() {
        this.settingsJson = GlobalSettings.asJson()
    }

    // Receiver's constructor
    constructor(buf: PacketByteBuf) {
        this.settingsJson = Gson().fromJson(buf.readString(), JsonObject::class.java)
    }

    // Sender's writer
    fun write(buf: PacketByteBuf) {
        buf.writeString(settingsJson.toString())
    }

    // Receiver's consumer
    fun consume(context: NetworkEvent.Context) {
        context.enqueueWork {
            GlobalSettings.syncFromServer(settingsJson)
        }
        context.packetHandled = true
    }
}
