package me.lizardofoz.inventorio.player

import me.lizardofoz.inventorio.enchantment.DeepPocketsEnchantment
import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.screenhandler.PlayerScreenHandlerAddon
import me.lizardofoz.inventorio.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.FireworkRocketEntity
import net.minecraft.item.FireworkItem
import net.minecraft.util.Hand

class PlayerAddon private constructor(val player: PlayerEntity)
{
    val inventoryAddon get() = (player.inventory as InventoryDuck).addon
    val handlerAddon get() = (player.playerScreenHandler as ScreenHandlerDuck).addon as PlayerScreenHandlerAddon

    fun fireRocketFromInventory()
    {
        for (section in inventoryAddon.combinedInventory)
            for (itemStack in section)
                if (itemStack.item is FireworkItem)
                {
                    //todo exclude explosives
                    if (player.isFallFlying)
                    {
                        if (player.world.isClient)
                            InventorioNetworking.C2SFireRocket()
                        else
                            player.world.spawnEntity(FireworkRocketEntity(player.world, itemStack, player))
                        player.swingHand(Hand.MAIN_HAND)
                        itemStack.decrement(1)
                        inventoryAddon.mainHandDisplayTool = itemStack
                        return
                    }
                }
    }

    fun getExtensionRows(): Int
    {
        return EnchantmentHelper.getEquipmentLevel(DeepPocketsEnchantment, player)
    }

    fun getAvailableExtensionSlotsRange(): IntRange
    {
        return EXTENSION_RANGE.first until EXTENSION_RANGE.first + getExtensionRows() * VANILLA_ROW_LENGTH
    }

    fun getUnavailableExtensionSlotsRange(): IntRange
    {
        return getAvailableExtensionSlotsRange().last + 1 .. EXTENSION_RANGE.last
    }


    @Environment(EnvType.CLIENT)
    object Client
    {
        val local get() = get(MinecraftClient.getInstance().player!!)
        var selectedQuickBarSection = -1
        @JvmField var triesToUseUtility = false
    }

    companion object
    {
        fun create(player: PlayerEntity): PlayerAddon
        {
            return PlayerAddon(player)
        }

        @JvmStatic
        operator fun get(player: PlayerEntity): PlayerAddon
        {
            return (player as PlayerDuck).addon
        }
    }
}