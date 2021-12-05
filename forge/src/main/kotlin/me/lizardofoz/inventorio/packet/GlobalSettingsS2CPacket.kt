package me.lizardofoz.inventorio.packet

import com.google.gson.Gson
import com.google.gson.JsonObject
import me.lizardofoz.inventorio.config.GlobalSettings
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fmllegacy.network.NetworkEvent
import java.util.function.Supplier

class GlobalSettingsS2CPacket
{
    private var settingsJson: JsonObject

    //Sender's constructor
    constructor()
    {
        this.settingsJson = GlobalSettings.asJson()
    }

    //Receiver's constructor
    constructor(buf: PacketByteBuf)
    {
        this.settingsJson = Gson().fromJson(buf.readString(), JsonObject::class.java)
    }

    //Sender's writer
    fun write(buf: PacketByteBuf)
    {
        buf.writeString(settingsJson.toString())
    }

    //Receiver's consumer
    fun consume(supplier: Supplier<NetworkEvent.Context>)
    {
        supplier.get().enqueueWork {
            GlobalSettings.syncFromServer(settingsJson)
        }
        supplier.get().packetHandled = true
    }
}