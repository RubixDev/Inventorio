package me.lizardofoz.inventorio.player

import de.rubixdev.inventorio.mixin.accessor.SimpleInventoryAccessor
import de.rubixdev.inventorio.player.PlayerInventoryAddon
import net.minecraft.entity.player.PlayerEntity

@Deprecated("Package has been moved", ReplaceWith("de.rubixdev.inventorio.player.PlayerInventoryAddon"), DeprecationLevel.ERROR)
class PlayerInventoryAddon(player: PlayerEntity) : PlayerInventoryAddon(player) {
    companion object {
        @JvmStatic
        @Suppress("DEPRECATION_ERROR")
        @Deprecated("For internal use only", level = DeprecationLevel.ERROR)
        fun ofNonNull(delegate: PlayerInventoryAddon): me.lizardofoz.inventorio.player.PlayerInventoryAddon {
            return me.lizardofoz.inventorio.player.PlayerInventoryAddon(delegate.player).apply {
                copyFrom(delegate)
                @Suppress("CAST_NEVER_SUCCEEDS")
                (this as SimpleInventoryAccessor).heldStacks = delegate.stacks
            }
        }

        @JvmStatic
        @Suppress("DEPRECATION_ERROR", "DeprecatedCallableAddReplaceWith")
        @Deprecated("For internal use only", level = DeprecationLevel.ERROR)
        fun of(delegate: PlayerInventoryAddon?): me.lizardofoz.inventorio.player.PlayerInventoryAddon? {
            return delegate?.let { ofNonNull(delegate) }
        }
    }
}
