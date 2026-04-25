package com.doug.macabrefix.fixes;

import com.curseforge.macabre.entity.TheHollowManEntity;
import com.curseforge.macabre.init.MacabreModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

public final class BossSpawnerDuplicateFix {
    private static final double PLAYER_TRIGGER_HALF_SIZE = 5.0D;
    private static final double EXISTING_BOSS_RADIUS = 32.0D;

    private BossSpawnerDuplicateFix() {
    }

    public static void replaceHollowSpawnerTick(LevelAccessor levelAccessor, double x, double y, double z, Entity spawner) {
        if (spawner == null || !(levelAccessor instanceof ServerLevel level)) {
            return;
        }

        if (hasPlayerInOriginalTriggerBox(level, x, y, z)) {
            if (!hasNearbyHollowMan(level, x, y, z)) {
                Entity hollowMan = MacabreModEntities.THE_HOLLOW_MAN.get()
                        .spawn(level, BlockPos.containing(x, y + 1.0D, z), MobSpawnType.MOB_SUMMONED);
                if (hollowMan != null) {
                    hollowMan.setDeltaMovement(0.0D, 0.0D, 0.0D);
                }
            }

            if (!spawner.level().isClientSide()) {
                spawner.discard();
            }
        }

        level.sendParticles(ParticleTypes.POOF, x, y, z, 30, 3.0D, 3.0D, 3.0D, 0.1D);
    }

    private static boolean hasPlayerInOriginalTriggerBox(ServerLevel level, double x, double y, double z) {
        for (ServerPlayer player : level.players()) {
            if (player.isRemoved()) {
                continue;
            }

            if (Math.abs(player.getX() - x) <= PLAYER_TRIGGER_HALF_SIZE
                    && Math.abs(player.getY() - y) <= PLAYER_TRIGGER_HALF_SIZE
                    && Math.abs(player.getZ() - z) <= PLAYER_TRIGGER_HALF_SIZE) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasNearbyHollowMan(ServerLevel level, double x, double y, double z) {
        AABB searchBox = new AABB(x, y, z, x, y, z).inflate(EXISTING_BOSS_RADIUS);
        return !level.getEntitiesOfClass(TheHollowManEntity.class, searchBox, Entity::isAlive).isEmpty();
    }
}
