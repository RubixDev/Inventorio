package me.lizardofoz.inventorio.client.control

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

/**
 * The usecase: when you bind "Use Item" and "Use Utility" to the same key, the intended behavior is them merging together.
 * It works on Forge, but FABRIC allows only one bindings per key.
 * This is the fix: the co-bound binding will unbind itself if binded to the same key.
 */
@Environment(EnvType.CLIENT)
open class KeyCoBinding(protected val coBindTranslationKey: String, protected val vanillaBinging: () -> KeyBinding?, translationKey: String, type: InputUtil.Type, code: Int, category: String): KeyBinding(translationKey, type, code, category)
{
    open val isThisOrVanillaPressed: Boolean
        get() = (isUnbound && vanillaBinging()?.isPressed == true) || super.isPressed()

    override fun setBoundKey(boundKey: InputUtil.Key)
    {
        super.setBoundKey(boundKey)
        if (!this.isUnbound && this.equals(vanillaBinging()))
            super.setBoundKey(InputUtil.UNKNOWN_KEY)
    }

    override fun getBoundKeyLocalizedText(): Text
    {
        return if (isUnbound)
            TranslatableText(coBindTranslationKey)
        else
            super.getBoundKeyLocalizedText()
    }
}