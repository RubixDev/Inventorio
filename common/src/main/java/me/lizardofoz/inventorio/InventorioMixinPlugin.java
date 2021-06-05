package me.lizardofoz.inventorio;

import me.lizardofoz.inventorio.extra.InventorioSharedConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class InventorioMixinPlugin implements IMixinConfigPlugin
{
    private static final String ROOT_PACKAGE = "me.lizardofoz.inventorio.mixin.";
    private static final String OPTIONAL_PACKAGE_ENDERCHEST = "me.lizardofoz.inventorio.mixin.optional.enderchest.";
    private static final String OPTIONAL_PACKAGE_BOWFIX = "me.lizardofoz.inventorio.mixin.optional.bowfix.";
    private static final String OPTIONAL_PACKAGE_TOTEM = "me.lizardofoz.inventorio.mixin.optional.totem.";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (!mixinClassName.startsWith(ROOT_PACKAGE))
            return false;
        if (mixinClassName.startsWith(OPTIONAL_PACKAGE_ENDERCHEST))
            return InventorioSharedConfig.INSTANCE.getExpandedEnderChest();
        if (mixinClassName.startsWith(OPTIONAL_PACKAGE_BOWFIX))
            return InventorioSharedConfig.INSTANCE.getInfinityBowNeedsNoArrow();
        if (mixinClassName.startsWith(OPTIONAL_PACKAGE_TOTEM))
            return InventorioSharedConfig.INSTANCE.getTotemFromUtilityBelt();
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