package me.danetnaverno.inventorio.packet.s2c

import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.packet.InventorioNetworking
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.util.QuickBarMode
import me.danetnaverno.inventorio.util.UtilityBeltMode
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object SetInventorySettingsS2CPacket
{
    val identifier = Identifier("inventorio", "set_inventory_settings_s2c")

    fun consume(context: PacketContext, buf: PacketByteBuf)
    {
        val quickBarMode = QuickBarMode.values()[buf.readByte().toInt()]
        val utilityBeltMode = UtilityBeltMode.values()[buf.readByte().toInt()]
        val selectedUtilitySlot = buf.readByte().toInt()

        if (quickBarMode == QuickBarMode.NOT_SELECTED)
            InventorioNetworking.C2SSendPlayerSettingsFromDefault()
        PlayerAddon.Client.local.trySetRestrictionModesS2C(quickBarMode, utilityBeltMode)
        PlayerAddon.Client.local.inventoryAddon.selectedUtility = selectedUtilitySlot
        RobertoGarbagio.LOGGER.info("Applying SetInventorySettingsS2CPacket: $quickBarMode $utilityBeltMode $selectedUtilitySlot")
    }

    fun write(buf: PacketByteBuf, quickBarMode: QuickBarMode = QuickBarMode.DEFAULT, utilityBeltMode: UtilityBeltMode = UtilityBeltMode.FILTERED, selectedUtilitySlot: Int = 0)
    {
        buf.writeByte(quickBarMode.ordinal)
        buf.writeByte(utilityBeltMode.ordinal)
        buf.writeByte(selectedUtilitySlot)
    }
}