package de.rubixdev.inventorio.api

import de.rubixdev.inventorio.config.GlobalSettings
import de.rubixdev.inventorio.util.ToolBeltMode
import de.rubixdev.inventorio.util.id
import net.minecraft.item.*

fun onApiInit() {
    if (GlobalSettings.toolBeltMode.value != ToolBeltMode.ENABLED) return

    InventorioAPI
        .registerToolBeltSlotIfNotExists(
            InventorioAPI.SLOT_PICKAXE,
            "textures/gui/empty/pickaxe.png".id,
        )!!
        .addAllowingCondition { stack, _ -> stack.item is PickaxeItem }
        .addAllowingTag("pickaxes".id)
        .addDenyingTag("pickaxes_blacklist".id)

    InventorioAPI
        .registerToolBeltSlotIfNotExists(
            InventorioAPI.SLOT_SWORD,
            "textures/gui/empty/sword.png".id,
        )!!
        .addAllowingCondition { stack, _ -> stack.item is SwordItem || stack.item is TridentItem }
        .addAllowingTag("swords".id)
        .addDenyingTag("swords_blacklist".id)

    InventorioAPI
        .registerToolBeltSlotIfNotExists(
            InventorioAPI.SLOT_AXE,
            "textures/gui/empty/axe.png".id,
        )!!
        .addAllowingCondition { stack, _ -> stack.item is AxeItem }
        .addAllowingTag("axes".id)
        .addDenyingTag("axes_blacklist".id)

    InventorioAPI
        .registerToolBeltSlotIfNotExists(
            InventorioAPI.SLOT_SHOVEL,
            "textures/gui/empty/shovel.png".id,
        )!!
        .addAllowingCondition { stack, _ -> stack.item is ShovelItem }
        .addAllowingTag("shovels".id)
        .addDenyingTag("shovels_blacklist".id)

    InventorioAPI
        .registerToolBeltSlotIfNotExists(
            InventorioAPI.SLOT_HOE,
            "textures/gui/empty/hoe.png".id,
        )!!
        .addAllowingCondition { stack, _ -> stack.item is HoeItem || stack.item is ShearsItem }
        .addAllowingTag("hoes".id)
        .addDenyingTag("hoes_blacklist".id)
}
