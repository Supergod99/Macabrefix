package com.doug.macabrefix.mixin;

import com.curseforge.macabre.procedures.MacabreEffectTickProcedure;
import com.doug.macabrefix.fixes.NetworkSyncThrottleFix;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MacabreEffectTickProcedure.class, remap = false)
public abstract class MacabreEffectSyncProcedureMixin {
    @Inject(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0)
    private static void macabrefix$syncEnterPitOnlyWhenChanged(Event event, Entity entity, CallbackInfo callbackInfo) {
        NetworkSyncThrottleFix.replaceEnterPitSync(entity);
        callbackInfo.cancel();
    }
}
