package me.lizardofoz.inventorio.client.config

import me.lizardofoz.inventorio.packet.InventorioNetworking
import me.lizardofoz.inventorio.player.PlayerAddon
import me.lizardofoz.inventorio.util.QuickBarMode
import me.lizardofoz.inventorio.util.QuickBarSimplified
import me.lizardofoz.inventorio.util.UtilityBeltMode
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
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

        category.addEntry(entryBuilder
                .startEnumSelector(
                        TranslatableText("inventorio.config.quick_bar_mode_default"),
                        QuickBarMode::class.java,
                        config.quickBarModeDefault
                )
                .setEnumNameProvider { TranslatableText("inventorio.config.quick_bar_mode."+it.name) }
                .setTooltip(TranslatableText("inventorio.config.quick_bar_mode_default.tooltip"))
                .setDefaultValue(QuickBarMode.DEFAULT)
                .setSaveConsumer {
                    config.quickBarModeDefault = it
                    configHolder.save()
                }
                .build())

        category.addEntry(entryBuilder
                .startEnumSelector(
                        TranslatableText("inventorio.config.utility_belt_mode_default"),
                        UtilityBeltMode::class.java,
                        config.utilityBeltModeDefault
                )
                .setEnumNameProvider { TranslatableText("inventorio.config.utility_belt_mode."+it.name) }
                .setTooltip(TranslatableText("inventorio.config.utility_belt_mode_default.tooltip"))
                .setDefaultValue(UtilityBeltMode.FILTERED)
                .setSaveConsumer {
                    config.utilityBeltModeDefault = it
                    configHolder.save()
                }
                .build())

        category.addEntry(entryBuilder.startTextDescription(TranslatableText("inventorio.config.category_world")).build())

        if (MinecraftClient.getInstance().player != null)
        {
            val playerAddon = PlayerAddon.Client.local

            category.addEntry(entryBuilder
                    .startEnumSelector(
                            TranslatableText("inventorio.config.quick_bar_mode_world"),
                            QuickBarMode::class.java,
                            playerAddon.quickBarMode
                    )
                    .setEnumNameProvider { TranslatableText("inventorio.config.quick_bar_mode."+it.name) }
                    .setTooltip(TranslatableText("inventorio.config.quick_bar_mode_world.tooltip"))
                    .setDefaultValue(QuickBarMode.DEFAULT)
                    .setSaveConsumer { playerAddon.trySetRestrictionModesC2S(it, playerAddon.utilityBeltMode) }
                    .build())

            category.addEntry(entryBuilder
                    .startEnumSelector(
                            TranslatableText("inventorio.config.utility_belt_mode_world"),
                            UtilityBeltMode::class.java,
                            playerAddon.utilityBeltMode
                    )
                    .setEnumNameProvider { TranslatableText("inventorio.config.utility_belt_mode."+it.name) }
                    .setTooltip(TranslatableText("inventorio.config.utility_belt_mode_world.tooltip"))
                    .setDefaultValue(UtilityBeltMode.FILTERED)
                    .setSaveConsumer { playerAddon.trySetRestrictionModesC2S(playerAddon.quickBarMode, it) }
                    .build())
        }
        else
        {
            category.addEntry(entryBuilder.startTextDescription(TranslatableText("inventorio.config.world_locked_tooltip")).build())
        }

        category.addEntry(entryBuilder.startTextDescription(TranslatableText("inventorio.config.mod_compatibility")).build())

        category.addEntry(entryBuilder
                .startStrList(TranslatableText("inventorio.config.ignored_screens"), InventorioConfigData.config().ignoredScreens)
                .setTooltip(TranslatableText("inventorio.config.ignored_screens.tooltip"))
                .setSaveConsumer {
                    config.ignoredScreens = it
                    configHolder.save()
                    val player = MinecraftClient.getInstance().player
                    if (player != null)
                    {
                        PlayerAddon[player].setAllIgnoredScreenHandlers(it)
                        InventorioNetworking.C2SSendIgnoredScreenHandlers() //todo check this
                    }
                }
                .build()
        )

        category.addEntry(entryBuilder
                .startStrList(TranslatableText("inventorio.config.inventory_text_offsets"), InventorioConfigData.config().inventoryTextOffsets)
                .setTooltip(TranslatableText("inventorio.config.inventory_text_offsets.tooltip"))
                .setSaveConsumer {
                    config.inventoryTextOffsets = it
                    configHolder.save()
                    //todo
                    //it.map {  }
                    //val ass = Map<Class<out HandledScreen<*>>, Point>
                    //TitleOffsets.setCustomTitleOffsets(it)
                }
                .build()
        )

        return builder.build()
    }
}