package de.rubixdev.inventorio.util

import de.rubixdev.inventorio.config.GlobalSettings
import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester

class BowTester : ConditionTester {
    override fun isSatisfied(mixinClassName: String) = GlobalSettings.infinityBowNeedsNoArrow.boolValue
}

class EnderChestTester : ConditionTester {
    override fun isSatisfied(mixinClassName: String) = GlobalSettings.expandedEnderChest.boolValue
}

class TotemTester : ConditionTester {
    override fun isSatisfied(mixinClassName: String) = GlobalSettings.totemFromUtilityBelt.boolValue
}
