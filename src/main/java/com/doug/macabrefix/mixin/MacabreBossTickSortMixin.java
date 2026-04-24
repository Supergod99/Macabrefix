package com.doug.macabrefix.mixin;

import com.curseforge.macabre.procedures.BaalOnEntityTickUpdateProcedure;
import com.curseforge.macabre.procedures.GargamawOnEntityTickUpdateProcedure;
import com.curseforge.macabre.procedures.MorphegorOnEntityTickUpdateProcedure;
import com.curseforge.macabre.procedures.ValamonOnEntityTickUpdateProcedure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Comparator;
import java.util.stream.Stream;

@Mixin(
        value = {
            BaalOnEntityTickUpdateProcedure.class,
            GargamawOnEntityTickUpdateProcedure.class,
            ValamonOnEntityTickUpdateProcedure.class,
            MorphegorOnEntityTickUpdateProcedure.class
        },
        remap = false)
public abstract class MacabreBossTickSortMixin {
    @Redirect(
            method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;sorted(Ljava/util/Comparator;)Ljava/util/stream/Stream;"),
            remap = false,
            require = 0)
    @SuppressWarnings("rawtypes")
    private static Stream macabrefix$skipUnusedNearbyEntitySort(Stream stream, Comparator comparator) {
        return stream;
    }
}
