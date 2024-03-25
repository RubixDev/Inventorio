package de.rubixdev.inventorio.util

import de.rubixdev.inventorio.config.GlobalSettings
import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester

@Suppress("unused")
class Never : ConditionTester {
    override fun isSatisfied(mixinClassName: String) = false
}

class BowTester : ConditionTester {
    override fun isSatisfied(mixinClassName: String) = GlobalSettings.infinityBowNeedsNoArrow.boolValue
}

class EnderChestTester : ConditionTester {
    override fun isSatisfied(mixinClassName: String) = GlobalSettings.expandedEnderChest.boolValue
}

class TotemTester : ConditionTester {
    override fun isSatisfied(mixinClassName: String) = GlobalSettings.totemFromUtilityBelt.boolValue
}

//#if FABRIC
//$$ class TrinketsTester : ConditionTester {
//$$     override fun isSatisfied(mixinClassName: String) = GlobalSettings.trinketsIntegration.boolValue
//$$ }
//#elseif FORGELIKE
//$$ class CuriosTester : ConditionTester {
//$$     override fun isSatisfied(mixinClassName: String) = GlobalSettings.curiosIntegration.boolValue
//$$ }
//#endif
