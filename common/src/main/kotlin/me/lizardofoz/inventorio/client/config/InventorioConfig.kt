package me.lizardofoz.inventorio.client.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import me.lizardofoz.inventorio.player.PlayerInventoryAddon
import me.lizardofoz.inventorio.player.PlayerInventoryAddon.Companion.inventoryAddon
import me.lizardofoz.inventorio.util.SegmentedHotbar
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import java.io.File
import java.io.FileReader
import java.io.FileWriter

@Environment(EnvType.CLIENT)
object InventorioConfig
{
    private lateinit var file: File

    var segmentedHotbar = SegmentedHotbar.OFF
        set(value)
        {
            field = value
            PlayerInventoryAddon.Client.selectedHotbarSection = -1
        }
    var scrollWheelUtilityBelt = false
    var canThrowUnloyalTrident = false
    var useItemAppliesToOffhand = false
    var swappedHands = false
        set(value)
        {
            field = value
            MinecraftClient.getInstance().player?.inventoryAddon?.swappedHands = value
        }

    fun toggleSegmentedHotbarMode(): SegmentedHotbar
    {
        segmentedHotbar = when (segmentedHotbar)
        {
            SegmentedHotbar.OFF -> SegmentedHotbar.ONLY_VISUAL
            SegmentedHotbar.ONLY_VISUAL -> SegmentedHotbar.ON
            else -> SegmentedHotbar.OFF
        }
        save()
        return segmentedHotbar
    }

    fun toggleScrollWheelUtilityBeltMode(): Boolean
    {
        scrollWheelUtilityBelt = !scrollWheelUtilityBelt
        save()
        return scrollWheelUtilityBelt
    }

    fun toggleCanThrowUnloyalTrident(): Boolean
    {
        canThrowUnloyalTrident = !canThrowUnloyalTrident
        save()
        return canThrowUnloyalTrident
    }

    fun toggleUseItemAppliesToOffhand(): Boolean
    {
        useItemAppliesToOffhand = !useItemAppliesToOffhand
        save()
        return useItemAppliesToOffhand
    }

    fun toggleSwappedHands(): Boolean
    {
        swappedHands = !swappedHands
        save()
        return swappedHands
    }

    fun save()
    {
        try
        {
            FileWriter(file).use { writer ->
                val configRoot = JsonObject()
                configRoot.addProperty("SegmentedHotbar", segmentedHotbar.name)
                configRoot.addProperty("ScrollWheelUtilityBelt", scrollWheelUtilityBelt)
                configRoot.addProperty("CanThrowUnloyalTrident", canThrowUnloyalTrident)
                configRoot.addProperty("UseItemAppliesToOffhand", useItemAppliesToOffhand)
                configRoot.addProperty("SwappedHands", swappedHands)
                Gson().toJson(configRoot, writer)
            }
        }
        catch (ignored: Exception)
        {
        }
    }

    fun load(configFolder: File)
    {
        try
        {
            file = configFolder.resolve("inventorio.json")
            if (file.exists())
                FileReader(file).use { writer ->
                    val configRoot = Gson().fromJson(writer, JsonObject::class.java)
                    segmentedHotbar = SegmentedHotbar.valueOf(configRoot.get("SegmentedHotbar").asString)
                    scrollWheelUtilityBelt = configRoot.get("ScrollWheelUtilityBelt").asBoolean
                    canThrowUnloyalTrident = configRoot.get("CanThrowUnloyalTrident").asBoolean
                    useItemAppliesToOffhand = configRoot.get("UseItemAppliesToOffhand").asBoolean
                    swappedHands = configRoot.get("SwappedHands").asBoolean
                }
        }
        catch (ignored: Exception)
        {
        }
    }
}