package me.lizardofoz.inventorio;

import me.lizardofoz.inventorio.config.GlobalSettings;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class InventorioMixinPlugin implements IMixinConfigPlugin
{
    private static final String ROOT_PACKAGE = "me.lizardofoz.inventorio.mixin.";
    private static final String OPTIONAL_PACKAGE_ENDERCHEST = "me.lizardofoz.inventorio.mixin.optional.enderchest.";
    private static final String OPTIONAL_PACKAGE_BOWFIX = "me.lizardofoz.inventorio.mixin.optional.bowfix.";
    private static final String OPTIONAL_PACKAGE_TOTEM = "me.lizardofoz.inventorio.mixin.optional.totem.";
    private static final String INTEGRATION_PACKAGE_GRAVESTONES = "me.lizardofoz.inventorio.mixin.invegration.gravestones.";
    private static final String INTEGRATION_PACKAGE_JEI = "me.lizardofoz.inventorio.mixin.invegration.gravestones.";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (!mixinClassName.startsWith(ROOT_PACKAGE))
            return false;
        if (mixinClassName.startsWith(OPTIONAL_PACKAGE_ENDERCHEST))
            return GlobalSettings.expandedEnderChest.getBoolValue();
        if (mixinClassName.startsWith(OPTIONAL_PACKAGE_BOWFIX))
            return GlobalSettings.infinityBowNeedsNoArrow.getBoolValue();
        if (mixinClassName.startsWith(OPTIONAL_PACKAGE_TOTEM))
            return GlobalSettings.totemFromUtilityBelt.getBoolValue();
        if (mixinClassName.startsWith(INTEGRATION_PACKAGE_GRAVESTONES))
            return GlobalSettings.integrationGravestones.getBoolValue();
        if (mixinClassName.startsWith(INTEGRATION_PACKAGE_JEI))
            return GlobalSettings.integrationJEI.getBoolValue();
        return true;
    }

    @Override
    public void onLoad(String mixinPackage)
    {
    }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }


    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
    {
    }

    @Override
    public List<String> getMixins()
    {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
    {
    }
}