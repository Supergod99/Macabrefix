package com.doug.macabrefix.mixin;

import com.curseforge.macabre.procedures.BossSpawner2OnEntityTickUpdateProcedure;
import com.curseforge.macabre.procedures.BossSpawnerOnEntityTickUpdateProcedure;
import com.doug.macabrefix.fixes.BossSpawnerDuplicateFix;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {BossSpawnerOnEntityTickUpdateProcedure.class, BossSpawner2OnEntityTickUpdateProcedure.class}, remap = false)
public abstract class MacabreBossSpawnerProcedureMixin {
    @Inject(
            method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0)
    private static void macabrefix$replaceHollowSpawnerTick(
            LevelAccessor level, double x, double y, double z, Entity entity, CallbackInfo callbackInfo) {
        BossSpawnerDuplicateFix.replaceHollowSpawnerTick(level, x, y, z, entity);
        callbackInfo.cancel();
    }
}
