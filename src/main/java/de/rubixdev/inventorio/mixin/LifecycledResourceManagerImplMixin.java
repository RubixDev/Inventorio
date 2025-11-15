package de.rubixdev.inventorio.mixin;

import de.rubixdev.inventorio.pack.InventorioResources;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(LifecycledResourceManagerImpl.class)
public abstract class LifecycledResourceManagerImplMixin implements LifecycledResourceManager {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static List<ResourcePack> injectRuntimePacks(List<ResourcePack> packs, ResourceType type) {
        var copy = new ArrayList<>(packs);

        if (type == ResourceType.SERVER_DATA) {
            copy.add(InventorioResources.PACK);
        }

        return copy;
    }
}
