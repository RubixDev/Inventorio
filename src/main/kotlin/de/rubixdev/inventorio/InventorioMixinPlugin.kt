package de.rubixdev.inventorio

import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin

class InventorioMixinPlugin : RestrictiveMixinConfigPlugin() {
    override fun getRefMapperConfig(): String? = null
    override fun acceptTargets(myTargets: MutableSet<String>?, otherTargets: MutableSet<String>?) {}
    override fun getMixins(): MutableList<String>? = null
}
