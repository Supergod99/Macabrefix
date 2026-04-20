package com.doug.macabrefix.fixes;

import com.curseforge.macabre.MacabreMod;
import com.curseforge.macabre.network.MacabreModVariables;
import com.curseforge.macabre.network.MacabreModVariables.PlayerVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;

public final class PitEffectQueueSyncFix {
    private static final double TRIGGER_CHANCE = 0.000002D;
    private static final int STEP_DELAY_TICKS = 2;
    private static final int EFFECT_DURATION_TICKS = 120;
    private static final int EFFECT_AMPLIFIER = 1;
    private static final double[] PRIMARY_SEQUENCE = sequence(1, 24);
    private static final double[] SECONDARY_SEQUENCE = sequence(25, 37);
    private static final PitEvent[] PIT_EVENTS = {
            new PitEvent("sus", 1.0F, PRIMARY_SEQUENCE),
            new PitEvent("bring", 0.5F, SECONDARY_SEQUENCE),
            new PitEvent("chil", 1.0F, PRIMARY_SEQUENCE),
            new PitEvent("bass", 1.0F, PRIMARY_SEQUENCE),
            new PitEvent("jumpscare", 0.3F, PRIMARY_SEQUENCE),
            new PitEvent("wooshambient", 0.5F, PRIMARY_SEQUENCE),
            new PitEvent("wearealldeadmusic", 0.5F, SECONDARY_SEQUENCE),
            new PitEvent("whispers", 1.0F, SECONDARY_SEQUENCE)
    };
    private static final MobEffect[] PIT_MOB_EFFECTS = {
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.BLINDNESS,
            MobEffects.CONFUSION,
            MobEffects.DIG_SLOWDOWN
    };

    private PitEffectQueueSyncFix() {
    }

    public static void replacePitEffectProcedure(LevelAccessor levelAccessor, double x, double y, double z, Entity entity) {
        if (entity == null || !(levelAccessor instanceof Level level) || level.isClientSide()) {
            return;
        }

        LazyOptional<PlayerVariables> optionalVariables =
                entity.getCapability(MacabreModVariables.PLAYER_VARIABLES_CAPABILITY);
        if (!optionalVariables.isPresent()) {
            return;
        }

        PlayerVariables variables = optionalVariables.orElseThrow(IllegalStateException::new);
        if (!variables.ENTERPIT || variables.PITEFFECT != 0.0D) {
            return;
        }

        PitEvent pitEvent = choosePitEvent();
        if (pitEvent == null) {
            return;
        }

        playPitSound(level, x, y, z, pitEvent);
        applyPitMobEffects(entity);
        setPitEffect(entity, variables, pitEvent.sequence[0]);
        if (pitEvent.sequence.length > 1) {
            MacabreMod.queueServerWork(
                    STEP_DELAY_TICKS,
                    new PitEffectSequenceStep(entity, pitEvent.sequence, 1));
        }
    }

    private static PitEvent choosePitEvent() {
        for (PitEvent pitEvent : PIT_EVENTS) {
            if (Math.random() < TRIGGER_CHANCE) {
                return pitEvent;
            }
        }
        return null;
    }

    private static void playPitSound(Level level, double x, double y, double z, PitEvent pitEvent) {
        ResourceLocation soundId = ResourceLocation.tryBuild("macabre", pitEvent.sound);
        SoundEvent soundEvent = soundId == null ? null : ForgeRegistries.SOUND_EVENTS.getValue(soundId);
        if (soundEvent != null) {
            level.playSound(
                    (Player) null,
                    BlockPos.containing(x, y, z),
                    soundEvent,
                    SoundSource.NEUTRAL,
                    pitEvent.volume,
                    1.0F);
        }
    }

    private static void applyPitMobEffects(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity) || livingEntity.level().isClientSide()) {
            return;
        }

        for (MobEffect effect : PIT_MOB_EFFECTS) {
            livingEntity.addEffect(new MobEffectInstance(
                    effect,
                    EFFECT_DURATION_TICKS,
                    EFFECT_AMPLIFIER,
                    false,
                    false));
        }
    }

    private static void setPitEffect(Entity entity, PlayerVariables variables, double value) {
        if (variables.PITEFFECT == value) {
            return;
        }

        variables.PITEFFECT = value;
        variables.syncPlayerVariables(entity);
    }

    private static double[] sequence(int first, int last) {
        double[] values = new double[last - first + 2];
        for (int value = first; value <= last; value++) {
            values[value - first] = value;
        }
        values[values.length - 1] = 0.0D;
        return values;
    }

    private record PitEvent(String sound, float volume, double[] sequence) {
    }

    private static final class PitEffectSequenceStep implements Runnable {
        private final Entity entity;
        private final double[] sequence;
        private final int index;

        private PitEffectSequenceStep(Entity entity, double[] sequence, int index) {
            this.entity = entity;
            this.sequence = sequence;
            this.index = index;
        }

        @Override
        public void run() {
            if (entity.isRemoved()) {
                return;
            }

            entity.getCapability(MacabreModVariables.PLAYER_VARIABLES_CAPABILITY).ifPresent(variables -> {
                double value = sequence[index];
                if (!variables.ENTERPIT && value != 0.0D) {
                    setPitEffect(entity, variables, 0.0D);
                    return;
                }

                setPitEffect(entity, variables, value);
                if (value != 0.0D && index + 1 < sequence.length) {
                    MacabreMod.queueServerWork(
                            STEP_DELAY_TICKS,
                            new PitEffectSequenceStep(entity, sequence, index + 1));
                }
            });
        }
    }
}
