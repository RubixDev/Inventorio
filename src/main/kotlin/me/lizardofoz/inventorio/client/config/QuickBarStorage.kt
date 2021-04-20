package me.lizardofoz.inventorio.client.config

import me.lizardofoz.inventorio.Inventorio
import me.lizardofoz.inventorio.util.INVENTORIO_ROW_LENGTH
import me.lizardofoz.inventorio.util.isNotEmpty
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

    fun tryLoad()
    {
        if (!loaded)
        {
            try
            {
                val compoundTag = NbtIo.read(file) ?: CompoundTag()
                for (i in 0 until compoundTag.size)
                {
                    val entry = HotbarStorageEntry()
                    entry.fromListTag(compoundTag.getList(i.toString(), NbtType.COMPOUND))
                    entries.add(entry)
                }
                for (i in compoundTag.size until INVENTORIO_ROW_LENGTH)
                    entries.add(HotbarStorageEntry())
            }
            catch (ex: Exception)
            {
                Inventorio.LOGGER.error("Failed to load quickbar entries", ex)
            }
            loaded = true
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
        tryLoad()
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
        tryLoad()
        return if (index in entries.indices) entries[index] else HotbarStorageEntry()
    }

    companion object
    {
        private val directory = MinecraftClient.getInstance().runDirectory.resolve("quickbars")
        val global = QuickBarStorage(directory.resolve("global.nbt"))
        var local = QuickBarStorage(directory.resolve(MinecraftClient.getInstance().currentServerEntry?.name + ".nbt"))
            private set

        init
        {
            if (!directory.exists())
                directory.mkdir()
        }
    }
}
