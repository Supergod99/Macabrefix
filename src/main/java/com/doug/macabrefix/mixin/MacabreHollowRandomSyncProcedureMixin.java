package com.doug.macabrefix.mixin;

import com.curseforge.macabre.procedures.BosstickresetProcedure;
import com.doug.macabrefix.config.MacabrefixConfig;
import com.doug.macabrefix.fixes.NetworkSyncThrottleFix;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BosstickresetProcedure.class, remap = false)
public abstract class MacabreHollowRandomSyncProcedureMixin {
    @Inject(
            method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0)
    private static void macabrefix$updateHollowRandomValueWithoutSync(Event event, LevelAccessor world, CallbackInfo callbackInfo) {
        if (!MacabrefixConfig.networkSyncThrottleFixEnabled()) {
            return;
        }

        NetworkSyncThrottleFix.replaceHollowRandomSync(world);
        callbackInfo.cancel();
    }
}
