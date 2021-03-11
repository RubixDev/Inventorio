package me.danetnaverno.inventorio.client.quickbar

import me.danetnaverno.inventorio.Inventorio
import me.danetnaverno.inventorio.isNotEmpty
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.options.HotbarStorageEntry
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import java.io.File

@Environment(EnvType.CLIENT)
class QuickBarStorage(private val file: File)
{
    private val entries = mutableListOf<HotbarStorageEntry>()
    private var loaded = false

    fun load()
    {
        try
        {
            val compoundTag = NbtIo.read(file) ?: return
            for (i in 0 until compoundTag.size)
            {
                val entry = HotbarStorageEntry()
                entry.fromListTag(compoundTag.getList(i.toString(), NbtType.COMPOUND))
                entries.add(entry)
            }
        }
        catch (ex: Exception)
        {
            Inventorio.LOGGER.error("Failed to load quickbar entries", ex)
        }
    }

    fun save()
    {
        try
        {
            val compoundTag = CompoundTag()
            for ((i, entry) in entries.withIndex())
                compoundTag.put(i.toString(), entry.toListTag())
            NbtIo.write(compoundTag, file)
        }
        catch (ex: Exception)
        {
            Inventorio.LOGGER.error("Failed to save quickbar entries", ex)
        }
    }

    fun setSavedQuickBar(index: Int, quickBar: List<ItemStack>)
    {
        val result = HotbarStorageEntry()
        for ((i, entry) in quickBar.withIndex())
            if (entry.isNotEmpty)
            {
                result[i] = entry.copy()
                result[i].count = 1
            }
        entries[index] = result
        save()
    }

    fun getSavedQuickBar(index: Int): HotbarStorageEntry
    {
        if (!loaded)
        {
            load()
            loaded = true
        }
        return if (index in entries.indices) entries[index] else HotbarStorageEntry()
    }

    companion object
    {
        val default = QuickBarStorage(MinecraftClient.getInstance().runDirectory.resolve("quickbar.nbt"))
    }
}
