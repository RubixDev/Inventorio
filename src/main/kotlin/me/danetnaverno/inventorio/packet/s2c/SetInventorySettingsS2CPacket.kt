package me.danetnaverno.inventorio.packet.s2c

import me.danetnaverno.inventorio.RobertoGarbagio
import me.danetnaverno.inventorio.packet.InventorioNetworking
import me.danetnaverno.inventorio.player.PlayerAddon
import me.danetnaverno.inventorio.util.QuickBarMode
import me.danetnaverno.inventorio.util.UtilityBeltMode
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener

class SetInventorySettingsS2CPacket(
        var quickBarMode: QuickBarMode = QuickBarMode.DEFAULT,
        var utilityBeltMode: UtilityBeltMode = UtilityBeltMode.FILTERED,
        var selectedUtilitySlot: Int = 0
) : Packet<ClientPlayPacketListener>
{
    override fun read(buf: PacketByteBuf)
    {
        quickBarMode = QuickBarMode.values()[buf.readByte().toInt()]
        utilityBeltMode = UtilityBeltMode.values()[buf.readByte().toInt()]
        selectedUtilitySlot = buf.readByte().toInt()
    }

    override fun write(buf: PacketByteBuf)
    {
        buf.writeByte(quickBarMode.ordinal)
        buf.writeByte(utilityBeltMode.ordinal)
        buf.writeByte(selectedUtilitySlot)
    }

    override fun apply(listener: ClientPlayPacketListener)
    {
        if (quickBarMode == QuickBarMode.NOT_SELECTED)
            InventorioNetworking.C2SSendPlayerSettingsFromDefault()
        PlayerAddon.Client.local.trySetRestrictionModesS2C(quickBarMode, utilityBeltMode)
        PlayerAddon.Client.local.inventoryAddon.selectedUtility = selectedUtilitySlot
        RobertoGarbagio.LOGGER.info("Applying SetInventorySettingsS2CPacket: $quickBarMode $utilityBeltMode $selectedUtilitySlot")
    }
}