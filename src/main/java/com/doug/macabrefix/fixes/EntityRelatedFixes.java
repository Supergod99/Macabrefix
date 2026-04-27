package com.doug.macabrefix.fixes;

import com.doug.macabrefix.config.MacabrefixConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

public class EntityRelatedFixes {
    private static final String MACABRE = "macabre";

    private static final int MAX_DECORATION_DENSITY = 3;
    private static final double DENSITY_CHECK_RADIUS = 16.0;
    private static final double WHIRLPOOL_CHECK_RADIUS = 16.0;

    private static final Set<ResourceLocation> NO_AI_ENTITY_IDS = Set.of(
            id("veintree_mid"),
            id("blindballoon"),
            id("worm"),
            id("worm_night"),
            id("gargamaw_spawner"),
            id("baal_spawner"),
            id("valamon_spawner"),
            id("morphegor_spawner"),
            id("gomoria_spawner"),
            id("fernrot"),
            id("spewer"),
            id("ultra_tree_spawner"),
            id("monolith"),
            id("molar"),
            id("canine"),
            id("incisor"),
            id("stagnant")
    );

    private static final ResourceLocation WHIRLPOOL_ID = id("whirlpool");
    private static final ResourceLocation WHISPERS_ID = id("whispers");

    private static Set<EntityType<?>> noAiEntityTypes = Set.of();
    private static EntityType<?> whirlpoolType;
    private static boolean entityTypesResolved;

    @SubscribeEvent
    public static void onCheckSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!MacabrefixConfig.entityRelatedFixesEnabled() || event.getLevel().isClientSide()) return;

        Mob mob = event.getEntity();
        EntityType<?> type = mob.getType();

        if (isNoAiEntity(type)) {
            if (hasAtLeastSameType(event.getLevel(), mob, type, DENSITY_CHECK_RADIUS, MAX_DECORATION_DENSITY)) {
                event.setSpawnCancelled(true);
                event.setCanceled(true);
            }
        } else if (type == getWhirlpoolType()) {
            if (hasAtLeastSameType(event.getLevel(), mob, type, WHIRLPOOL_CHECK_RADIUS, 1)) {
                event.setSpawnCancelled(true);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!MacabrefixConfig.entityRelatedFixesEnabled() || event.getLevel().isClientSide()) return;

        Entity entity = event.getEntity();
        if (isNoAiEntity(entity.getType()) && entity instanceof Mob mob) {
            mob.setNoAi(true);
            mob.setNoGravity(true);
            mob.setDeltaMovement(0, 0, 0);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!MacabrefixConfig.entityRelatedFixesEnabled() || event.getEntity().level().isClientSide()) return;

        if (event.getSource().is(DamageTypes.IN_WALL) && isNoAiEntity(event.getEntity().getType())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayLevelSound(PlayLevelSoundEvent event) {
        if (!MacabrefixConfig.entityRelatedFixesEnabled()) return;

        Holder<SoundEvent> soundHolder = event.getSound();
        if (soundHolder == null) return;

        ResourceLocation soundLoc = soundHolder.value().getLocation();

        if (soundLoc.equals(WHISPERS_ID)) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            event.setNewVolume(0.0f);
        }
    }

    private static boolean isNoAiEntity(EntityType<?> type) {
        return getNoAiEntityTypes().contains(type);
    }

    private static Set<EntityType<?>> getNoAiEntityTypes() {
        resolveEntityTypes();
        return noAiEntityTypes;
    }

    private static EntityType<?> getWhirlpoolType() {
        resolveEntityTypes();
        return whirlpoolType;
    }

    private static void resolveEntityTypes() {
        if (entityTypesResolved) {
            return;
        }

        Set<EntityType<?>> resolvedNoAiTypes = new HashSet<>();
        for (ResourceLocation id : NO_AI_ENTITY_IDS) {
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(id);
            if (type != null) {
                resolvedNoAiTypes.add(type);
            }
        }

        noAiEntityTypes = Set.copyOf(resolvedNoAiTypes);
        whirlpoolType = ForgeRegistries.ENTITY_TYPES.getValue(WHIRLPOOL_ID);
        entityTypesResolved = true;
    }

    private static boolean hasAtLeastSameType(LevelAccessor level, Mob origin, EntityType<?> type, double radius, int limit) {
        AABB checkArea = origin.getBoundingBox().inflate(radius);
        int count = 0;
        for (Mob nearby : level.getEntitiesOfClass(Mob.class, checkArea, entity -> entity.getType() == type)) {
            if (++count >= limit) {
                return true;
            }
        }
        return false;
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MACABRE, path);
    }
}
