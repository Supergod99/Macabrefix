package com.doug.macabrefix.mixin;

import com.curseforge.macabre.procedures.MacabreEffectProcedure;
import com.doug.macabrefix.config.MacabrefixConfig;
import com.doug.macabrefix.fixes.PitEffectQueueSyncFix;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MacabreEffectProcedure.class, remap = false)
public abstract class MacabrePitEffectProcedureMixin {
    @Inject(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0)
    private static void macabrefix$replacePitEffectQueueSync(
            Event event,
            LevelAccessor world,
            double x,
            double y,
            double z,
            Entity entity,
            CallbackInfo callbackInfo) {
        if (!MacabrefixConfig.pitEffectQueueSyncFixEnabled()) {
            return;
        }

        PitEffectQueueSyncFix.replacePitEffectProcedure(world, x, y, z, entity);
        callbackInfo.cancel();
    }
}
