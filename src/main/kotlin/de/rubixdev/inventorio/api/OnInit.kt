package de.rubixdev.inventorio.api

import de.rubixdev.inventorio.config.GlobalSettings
import de.rubixdev.inventorio.util.ToolBeltMode
import net.minecraft.item.*
import net.minecraft.util.Identifier

fun onApiInit() {
    if (GlobalSettings.toolBeltMode.value != ToolBeltMode.ENABLED) return

    InventorioAPI
        .registerToolBeltSlotIfNotExists(
            InventorioAPI.SLOT_PICKAXE,
            Identifier("inventorio", "textures/gui/empty/pickaxe.png"),
        )!!
        .addAllowingCondition { stack, _ -> stack.item is PickaxeItem }
        .addAllowingTag(Identifier("inventorio", "pickaxes"))
        .addDenyingTag(Identifier("inventorio", "pickaxes_blacklist"))

    InventorioAPI
        .registerToolBeltSlotIfNotExists(
            InventorioAPI.SLOT_SWORD,
            Identifier("inventorio", "textures/gui/empty/sword.png"),
        )!!
        .addAllowingCondition { stack, _ -> stack.item is SwordItem || stack.item is TridentItem }
        .addAllowingTag(Identifier("inventorio", "swords"))
        .addDenyingTag(Identifier("inventorio", "swords_blacklist"))

    InventorioAPI
        .registerToolBeltSlotIfNotExists(
            InventorioAPI.SLOT_AXE,
            Identifier("inventorio", "textures/gui/empty/axe.png"),
        )!!
        .addAllowingCondition { stack, _ -> stack.item is AxeItem }
        .addAllowingTag(Identifier("inventorio", "axes"))
        .addDenyingTag(Identifier("inventorio", "axes_blacklist"))

    InventorioAPI
        .registerToolBeltSlotIfNotExists(
            InventorioAPI.SLOT_SHOVEL,
            Identifier("inventorio", "textures/gui/empty/shovel.png"),
        )!!
        .addAllowingCondition { stack, _ -> stack.item is ShovelItem }
        .addAllowingTag(Identifier("inventorio", "shovels"))
        .addDenyingTag(Identifier("inventorio", "shovels_blacklist"))

    InventorioAPI
        .registerToolBeltSlotIfNotExists(
            InventorioAPI.SLOT_HOE,
            Identifier("inventorio", "textures/gui/empty/hoe.png"),
        )!!
        .addAllowingCondition { stack, _ -> stack.item is HoeItem || stack.item is ShearsItem }
        .addAllowingTag(Identifier("inventorio", "hoes"))
        .addDenyingTag(Identifier("inventorio", "hoes_blacklist"))
}
