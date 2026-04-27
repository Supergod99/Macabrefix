package com.doug.macabrefix.mixin;

import com.curseforge.macabre.procedures.NumberAbilityProcedure;
import com.doug.macabrefix.config.MacabrefixConfig;
import com.doug.macabrefix.fixes.NetworkSyncThrottleFix;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NumberAbilityProcedure.class, remap = false)
public abstract class MacabreBossRandomSyncProcedureMixin {
    @Inject(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0)
    private static void macabrefix$updateBossRandomValuesWithoutSync(Event event, LevelAccessor world, CallbackInfo callbackInfo) {
        if (!MacabrefixConfig.networkSyncThrottleFixEnabled()) {
            return;
        }

        NetworkSyncThrottleFix.replaceBossAbilityRandomSync(world);
        callbackInfo.cancel();
    }
}
