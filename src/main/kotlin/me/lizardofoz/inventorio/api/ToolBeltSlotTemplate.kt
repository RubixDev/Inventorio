package me.lizardofoz.inventorio.api

import de.rubixdev.inventorio.api.ToolBeltSlotTemplate
import net.minecraft.util.Identifier

@Deprecated("Package has been moved", ReplaceWith("de.rubixdev.inventorio.api.ToolBeltSlotTemplate"), DeprecationLevel.ERROR)
class ToolBeltSlotTemplate(name: String, emptyIcon: Identifier) : ToolBeltSlotTemplate(name, emptyIcon) {
    companion object {
        @JvmStatic
        @Suppress("DEPRECATION_ERROR", "DeprecatedCallableAddReplaceWith")
        @Deprecated("For internal use only", level = DeprecationLevel.ERROR)
        fun of(delegate: ToolBeltSlotTemplate?): me.lizardofoz.inventorio.api.ToolBeltSlotTemplate? {
            return delegate?.let { me.lizardofoz.inventorio.api.ToolBeltSlotTemplate(it.name, it.emptyIcon) }
        }
    }
}
