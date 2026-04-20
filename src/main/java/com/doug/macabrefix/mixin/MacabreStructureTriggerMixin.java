package com.doug.macabrefix.mixin;

import com.curseforge.macabre.procedures.DunGenUpdateTickProcedure;
import com.curseforge.macabre.procedures.VilGenUpdateTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {DunGenUpdateTickProcedure.class, VilGenUpdateTickProcedure.class}, remap = false)
public abstract class MacabreStructureTriggerMixin {
    @Inject(
            method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDD)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0)
    private static void macabrefix$clearStructureTriggerWithoutTemplateRun(
            LevelAccessor level, double x, double y, double z, CallbackInfo callbackInfo) {
        level.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        callbackInfo.cancel();
    }
}
