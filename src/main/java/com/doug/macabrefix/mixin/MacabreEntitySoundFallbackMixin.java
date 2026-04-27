package com.doug.macabrefix.mixin;

import com.curseforge.macabre.entity.*;
import com.doug.macabrefix.config.MacabrefixConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.registries.IForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
        value = {
                BaloonEyeEntity.class,
                BaloonEyeNightEntity.class,
                EyebaloonEntity.class,
                EyebushEntity.class,
                WitnessEntity.class,
                WitnessNightEntity.class
        },
        remap = false)
public abstract class MacabreEntitySoundFallbackMixin {
    @Redirect(
            method = {
                    "m_7515_()Lnet/minecraft/sounds/SoundEvent;",
                    "m_7975_(Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/sounds/SoundEvent;",
                    "m_5592_()Lnet/minecraft/sounds/SoundEvent;",
                    "m_7355_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/registries/IForgeRegistry;getValue(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;"),
            require = 0,
            remap = false)
    private Object macabrefix$fallbackMissingSound(IForgeRegistry<SoundEvent> registry, ResourceLocation soundId) {
        SoundEvent sound = registry.getValue(soundId);
        return sound != null || !MacabrefixConfig.entitySoundFallbackFixEnabled() ? sound : SoundEvents.GRASS_BREAK;
    }
}
