package me.lizardofoz.inventorio.client.config

import me.lizardofoz.inventorio.extra.InventorioSharedConfig
import me.lizardofoz.inventorio.util.SegmentedHotbar
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
        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(TranslatableText("inventorio.config.title"))
        val entryBuilder = builder.entryBuilder()
        val category = builder.getOrCreateCategory(TranslatableText("inventorio.keys.category"))

        category.addEntry(entryBuilder
            .startEnumSelector(
                TranslatableText("inventorio.config.segmented_hotbar_mode"),
                SegmentedHotbar::class.java,
                InventorioConfig.segmentedHotbar
            )
            .setEnumNameProvider { TranslatableText("inventorio.config.segmented_hotbar_mode." + it.name) }
            .setDefaultValue(SegmentedHotbar.OFF)
            .setSaveConsumer {
                InventorioConfig.segmentedHotbar = it
                InventorioConfig.save()
            }
            .build())

        category.addEntry(entryBuilder
            .startBooleanToggle(
                TranslatableText("inventorio.config.scroll_wheel_utility_belt_mode"),
                InventorioConfig.scrollWheelUtilityBelt
            )
            .setDefaultValue(false)
            .setSaveConsumer {
                InventorioConfig.scrollWheelUtilityBelt = it
                InventorioConfig.save()
            }
            .build())

        category.addEntry(entryBuilder
            .startBooleanToggle(
                TranslatableText("inventorio.config.can_throw_unloyal_trident"),
                InventorioConfig.canThrowUnloyalTrident
            )
            .setDefaultValue(false)
            .setSaveConsumer {
                InventorioConfig.canThrowUnloyalTrident = it
                InventorioConfig.save()
            }
            .build())

        category.addEntry(entryBuilder
            .startBooleanToggle(
                TranslatableText("inventorio.config.use_item_applies_to_offhand"),
                InventorioConfig.useItemAppliesToOffhand
            )
            .setDefaultValue(false)
            .setTooltip(TranslatableText("inventorio.config.use_item_applies_to_offhand.tooltip"))
            .setSaveConsumer {
                InventorioConfig.useItemAppliesToOffhand = it
                InventorioConfig.save()
            }
            .build())

        if (InventorioSharedConfig.allowSwappedHands)
            category.addEntry(entryBuilder
                .startBooleanToggle(
                    TranslatableText("inventorio.config.swapped_hands"),
                    InventorioConfig.swappedHands
                )
                .setDefaultValue(false)
                .setTooltip(TranslatableText("inventorio.config.swapped_hands.tooltip"))
                .setSaveConsumer {
                    InventorioConfig.swappedHands = it
                    InventorioConfig.save()
                }
                .build())
        else
            category.addEntry(entryBuilder
                .startTextDescription(
                    TranslatableText("inventorio.config.swapped_hands.disabled"),
                )
                .setTooltip(TranslatableText("inventorio.config.swapped_hands.disabled.tooltip"))
                .build())

        return builder.build()
    }
}