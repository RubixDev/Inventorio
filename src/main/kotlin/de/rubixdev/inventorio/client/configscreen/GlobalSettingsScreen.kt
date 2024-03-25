package de.rubixdev.inventorio.client.configscreen

import de.rubixdev.inventorio.client.configscreen.PlayerSettingsScreen.addBoolEntry
import de.rubixdev.inventorio.client.configscreen.PlayerSettingsScreen.addEnumEntry
import de.rubixdev.inventorio.config.GlobalSettings
import de.rubixdev.inventorio.util.PlatformApi
import de.rubixdev.inventorio.util.ToolBeltMode
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
object GlobalSettingsScreen {
    fun get(parent: Screen?): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setSavingRunnable(GlobalSettings::save)
            .setTitle(Text.translatable("inventorio.settings.global.title"))
        val entryBuilder = builder.entryBuilder()
        val category = builder.getOrCreateCategory(Text.translatable("inventorio.settings.global.title"))

        category.addEntry(entryBuilder.startTextDescription(Text.translatable("inventorio.settings.global.description")).build())

        val isNotLocal = MinecraftClient.getInstance().networkHandler?.connection?.isLocal != true
        if (isNotLocal) {
            category.addEntry(entryBuilder.startTextDescription(Text.translatable("inventorio.settings.global.disabled_by_server")).build())
        }

        addBoolEntry(category, entryBuilder, GlobalSettings.expandedEnderChest, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.infinityBowNeedsNoArrow, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.totemFromUtilityBelt, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.allowSwappedHands, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.allow2x2CraftingGrid, true, isNotLocal)
        //#if FABRIC
        addBoolEntry(category, entryBuilder, GlobalSettings.trinketsIntegration, true, isNotLocal) { PlatformApi.isModLoaded("trinkets") }
        //#elseif FORGELIKE
        addBoolEntry(category, entryBuilder, GlobalSettings.curiosIntegration, true, isNotLocal) { PlatformApi.isModLoaded("curios") }
        //#endif

        addEnumEntry(category, entryBuilder, GlobalSettings.toolBeltMode, true, isNotLocal, ToolBeltMode::class.java, ToolBeltMode.ENABLED)
        addBoolEntry(category, entryBuilder, GlobalSettings.utilityBeltShortDefaultSize, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.deepPocketsBookCraft, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.deepPocketsInTrades, true, isNotLocal)
        addBoolEntry(category, entryBuilder, GlobalSettings.deepPocketsInRandomSelection, true, isNotLocal)

        return builder.build()
    }
}
