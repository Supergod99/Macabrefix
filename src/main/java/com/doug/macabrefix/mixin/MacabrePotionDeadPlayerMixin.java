package com.doug.macabrefix.mixin;

import com.curseforge.macabre.procedures.MacabrePotionProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MacabrePotionProcedure.class, remap = false)
public abstract class MacabrePotionDeadPlayerMixin {
    @Inject(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0)
    private static void macabrefix$skipDeadPlayers(Event event, Entity entity, CallbackInfo callbackInfo) {
        if (entity instanceof LivingEntity livingEntity && (!livingEntity.isAlive() || livingEntity.isRemoved())) {
            callbackInfo.cancel();
        }
    }
}
