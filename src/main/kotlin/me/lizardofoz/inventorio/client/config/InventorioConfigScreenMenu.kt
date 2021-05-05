package me.lizardofoz.inventorio.client.config

import me.lizardofoz.inventorio.util.QuickBarSimplified
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.TranslatableText

@Environment(EnvType.CLIENT)
object InventorioConfigScreenMenu
{
    fun get(parent: Screen?): Screen
    {
        val configHolder = InventorioConfigData.holder()
        val config = configHolder.config

        val builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(TranslatableText("inventorio.config.title"))
        val entryBuilder = builder.entryBuilder()
        val category = builder.getOrCreateCategory(TranslatableText("inventorio.keys.category"))

        category.addEntry(entryBuilder.startTextDescription(TranslatableText("inventorio.config.category_global")).build())

        category.addEntry(entryBuilder
                .startEnumSelector(
                        TranslatableText("inventorio.config.simplified_quick_bar"),
                        QuickBarSimplified::class.java,
                        config.quickBarSimplified
                )
                .setEnumNameProvider { TranslatableText("inventorio.config.simplified_quick_bar."+it.name) }
                .setTooltip(TranslatableText("inventorio.config.simplified_quick_bar.tooltip"))
                .setDefaultValue(QuickBarSimplified.OFF)
                .setSaveConsumer {
                    config.quickBarSimplified = it
                    configHolder.save()
                }
                .build())

        category.addEntry(entryBuilder.startTextDescription(TranslatableText("inventorio.config.category_world")).build())

        return builder.build()
    }
}