package de.rubixdev.inventorio.mixin.neoforge.curios;

import de.rubixdev.inventorio.util.Never;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;

// this mixin is only valid in 1.20.4+
@Restriction(require = @Condition(type = Condition.Type.TESTER, tester = Never.class))
@Mixin(SharedConstants.class)
public class CuriosServerPayloadHandlerMixin {}
