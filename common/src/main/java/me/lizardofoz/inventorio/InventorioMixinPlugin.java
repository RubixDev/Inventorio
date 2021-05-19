package me.lizardofoz.inventorio;

import me.lizardofoz.inventorio.extra.InventorioServerConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class InventorioMixinPlugin implements IMixinConfigPlugin
{
    private static final String ROOT_PACKAGE = "me.lizardofoz.inventorio.mixin.";
    private static final String ENDERCEST_OPTION_PACKAGE = "me.lizardofoz.inventorio.mixin.enderchest.";
    private static final String BOWFIX_OPTION_PACKAGE = "me.lizardofoz.inventorio.mixin.bowfix.";

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
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (!mixinClassName.startsWith(ROOT_PACKAGE))
            return false;
        if (mixinClassName.startsWith(ENDERCEST_OPTION_PACKAGE))
            return InventorioServerConfig.INSTANCE.getExpandedEnderChest();
        if (mixinClassName.startsWith(BOWFIX_OPTION_PACKAGE))
            return InventorioServerConfig.INSTANCE.getInfinityBowNeedsNoArrow();
        return true;
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