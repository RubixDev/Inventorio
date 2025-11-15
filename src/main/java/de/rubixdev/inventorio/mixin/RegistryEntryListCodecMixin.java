package de.rubixdev.inventorio.mixin;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import de.rubixdev.inventorio.pack.DummyRegistryEntryList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryListCodec;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RegistryEntryListCodec.class)
public abstract class RegistryEntryListCodecMixin<E> implements Codec<RegistryEntryList<E>> {
    @Shadow
    @Final
    private Codec<Either<TagKey<E>, List<RegistryEntry<E>>>> entryListStorageCodec;

    @Inject(
        method = "encode(Lnet/minecraft/registry/entry/RegistryEntryList;Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;",
        at = @At("HEAD"),
        cancellable = true
    )
    public <T> void encodeDummyList(
        RegistryEntryList<E> registryEntryList,
        DynamicOps<T> dynamicOps,
        T object,
        CallbackInfoReturnable<DataResult<T>> cir
    ) {
        if (registryEntryList instanceof DummyRegistryEntryList<E>) {
            cir.setReturnValue(
                entryListStorageCodec.encode(registryEntryList.getStorage().mapRight(List::copyOf), dynamicOps, object)
            );
        }
    }
}
