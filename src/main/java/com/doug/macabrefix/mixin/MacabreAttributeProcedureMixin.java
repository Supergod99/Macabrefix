package com.doug.macabrefix.mixin;

import com.curseforge.macabre.procedures.BlooodAttribuesProcedure;
import com.doug.macabrefix.config.MacabrefixConfig;
import com.doug.macabrefix.fixes.AttributeCompatibilityFix;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlooodAttribuesProcedure.class, remap = false)
public abstract class MacabreAttributeProcedureMixin {
    @Inject(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0)
    private static void macabrefix$skipHardBaseAttributeWrites(Event event, Entity entity, CallbackInfo callbackInfo) {
        if (!MacabrefixConfig.attributeCompatibilityFixEnabled()) {
            return;
        }

        AttributeCompatibilityFix.replaceMacabreAttributeProcedure(entity);
        callbackInfo.cancel();
    }
}
