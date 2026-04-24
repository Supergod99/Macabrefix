package com.doug.macabrefix.fixes;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public class EntityRelatedFixes {

    // density config for decorative entities
    private static final int MAX_DECORATION_DENSITY = 3;
    private static final double DENSITY_CHECK_RADIUS = 16.0;

    // entities entered here will have their AI and gravity disabled, and their spawns limited
    private static final Set<String> NO_AI_ENTITIES = Set.of(
            "macabre:veintree_mid",
            "macabre:blindballoon",
            "macabre:worm",
            "macabre:worm_night",
            "macabre:gargamaw_spawner",
            "macabre:baal_spawner",
            "macabre:valamon_spawner",
            "macabre:morphegor_spawner",
            "macabre:gomoria_spawner",
            "macabre:fernrot",
            "macabre:spewer",
            "macabre:ultra_tree_spawner",
            "macabre:monolith",
            "macabre:molar",
            "macabre:canine",
            "macabre:incisor"
    );

    private static final String WHIRLPOOL_ID = "macabre:whirlpool";

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        Entity entity = event.getEntity();
        ResourceLocation typeKey = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());

        if (typeKey != null) {
            String entityId = typeKey.toString();

            if (NO_AI_ENTITIES.contains(entityId) && entity instanceof Mob mob) {
                // Density check: stop spawning if there are too many nearby
                AABB checkArea = entity.getBoundingBox().inflate(DENSITY_CHECK_RADIUS);
                int nearbyCount = event.getLevel().getEntities(entity, checkArea, e -> {
                    ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(e.getType());
                    return key != null && key.toString().equals(entityId);
                }).size();

                if (nearbyCount >= MAX_DECORATION_DENSITY) {
                    event.setCanceled(true);
                    return;
                }

                mob.setNoAi(true);
                mob.setNoGravity(true);
                mob.setDeltaMovement(0, 0, 0);
                return;
            }

            // whirlpool bug fix
            if (entityId.equals(WHIRLPOOL_ID)) {
                AABB checkArea = entity.getBoundingBox().inflate(16.0);
                boolean hasNearbyWhirlpool = !event.getLevel().getEntities(entity, checkArea, e -> {
                    ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(e.getType());
                    return key != null && key.toString().equals(WHIRLPOOL_ID);
                }).isEmpty();

                if (hasNearbyWhirlpool) {
                    event.setCanceled(true);
                }
            }
        }
    }

    // disables the stupid ass annoying whispers from playing at all
    @SubscribeEvent
    public static void onPlayLevelSound(PlayLevelSoundEvent event) {
        Holder<SoundEvent> soundHolder = event.getSound();
        if (soundHolder == null) return;

        SoundEvent soundEvent = soundHolder.value();
        String soundId = soundEvent.getLocation().toString();

        if (soundId.equals("macabre:whispers")) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            event.setNewVolume(0.0f);
            return;
        }

        // changes the marauders hit sound to an actual fleshy sound (it was skeleton hit sound before)
        if (soundId.equals("minecraft:entity.skeleton.hurt") && event instanceof PlayLevelSoundEvent.AtEntity atEntityEvent) {
            Entity entity = atEntityEvent.getEntity();
            if (entity != null) {
                ResourceLocation entityLoc = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
                if (entityLoc != null) {
                    String entityId = entityLoc.toString();
                    if (entityId.equals("macabre:marauder") || entityId.equals("macabre:marauder_night")) {
                        float originalVolume = event.getNewVolume();
                        float originalPitch = event.getNewPitch();
                        if (event.isCancelable()) {
                            event.setCanceled(true);
                        }
                        event.setNewVolume(0.0f);
                        SoundEvent newSound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("goety", "scythe_hit_meaty"));
                        if (newSound != null && !entity.level().isClientSide()) {
                            entity.playSound(newSound, originalVolume, originalPitch);
                        }
                    }
                }
            }
        }
    }
}