package me.danetnaverno.inventorio.client.config

import me.danetnaverno.inventorio.QuickBarMode
import me.danetnaverno.inventorio.QuickBarSimplified
import me.danetnaverno.inventorio.UtilityBeltMode
import me.danetnaverno.inventorio.player.PlayerAddon
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
        val global = builder.getOrCreateCategory(TranslatableText("inventorio.config.category_global"))

        global.addEntry(entryBuilder
                .startEnumSelector(
                        TranslatableText("inventorio.config.simplified_quick_bar"),
                        QuickBarSimplified::class.java,
                        config.quickBarSimplifiedGlobal
                )
                .setEnumNameProvider { TranslatableText("inventorio.config.simplified_quick_bar."+it.name) }
                .setTooltip(TranslatableText("inventorio.config.simplified_quick_bar.tooltip"))
                .setDefaultValue(QuickBarSimplified.OFF)
                .setSaveConsumer {
                    config.quickBarSimplifiedGlobal = it
                    configHolder.save()
                }
                .build())

        global.addEntry(entryBuilder
                .startStrList(TranslatableText("inventorio.config.ignored_screens_list"), InventorioConfigData.config().ignoredScreensGlobal)
                .setTooltip(TranslatableText("inventorio.config.ignored_screens_list.tooltip"))
                .setSaveConsumer {
                    config.ignoredScreensGlobal = it
                    configHolder.save()
                    val player = MinecraftClient.getInstance().player
                    if (player != null)
                        PlayerAddon[player].setAllIgnoredScreenHandlers(it)
                }
                .build()
        )

        global.addEntry(entryBuilder
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

        global.addEntry(entryBuilder
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

        val world = builder.getOrCreateCategory(TranslatableText("inventorio.config.category_world"))

        if (MinecraftClient.getInstance().player != null)
        {
            val playerAddon = PlayerAddon.Client.local

            world.addEntry(entryBuilder
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

            world.addEntry(entryBuilder
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
            world.addEntry(entryBuilder.startTextDescription(TranslatableText("inventorio.config.world_locked_tooltip")).build())
        }

        return builder.build()
    }
}