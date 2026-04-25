package com.doug.macabrefix.fixes;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public class EntityRelatedFixes {

    // density config for decorative entities
    private static final int MAX_DECORATION_DENSITY = 3;
    private static final double DENSITY_CHECK_RADIUS = 16.0;

    // entities entered here will have their AI and gravity disabled, and their spawns limited
    private static final Set<ResourceLocation> NO_AI_ENTITIES = Set.of(
            new ResourceLocation("macabre", "veintree_mid"),
            new ResourceLocation("macabre", "blindballoon"),
            new ResourceLocation("macabre", "worm"),
            new ResourceLocation("macabre", "worm_night"),
            new ResourceLocation("macabre", "gargamaw_spawner"),
            new ResourceLocation("macabre", "baal_spawner"),
            new ResourceLocation("macabre", "valamon_spawner"),
            new ResourceLocation("macabre", "morphegor_spawner"),
            new ResourceLocation("macabre", "gomoria_spawner"),
            new ResourceLocation("macabre", "fernrot"),
            new ResourceLocation("macabre", "spewer"),
            new ResourceLocation("macabre", "ultra_tree_spawner"),
            new ResourceLocation("macabre", "monolith"),
            new ResourceLocation("macabre", "molar"),
            new ResourceLocation("macabre", "canine"),
            new ResourceLocation("macabre", "incisor"),
            new ResourceLocation("macabre", "stagnant")
    );

    private static final ResourceLocation WHIRLPOOL_ID = new ResourceLocation("macabre", "whirlpool");
    private static final ResourceLocation WHISPERS_ID = new ResourceLocation("macabre", "whispers");
    private static final ResourceLocation SKELETON_HURT_ID = new ResourceLocation("minecraft", "entity.skeleton.hurt");
    private static final ResourceLocation MEATY_HIT_ID = new ResourceLocation("goety", "scythe_hit_meaty");
    private static final ResourceLocation MARAUDER_ID = new ResourceLocation("macabre", "marauder");
    private static final ResourceLocation MARAUDER_NIGHT_ID = new ResourceLocation("macabre", "marauder_night");

    @SubscribeEvent
    public static void onCheckSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getLevel().isClientSide()) return;

        Mob mob = event.getEntity();
        ResourceLocation typeKey = ForgeRegistries.ENTITY_TYPES.getKey(mob.getType());
        if (typeKey == null) return;

        if (NO_AI_ENTITIES.contains(typeKey)) {
            AABB checkArea = mob.getBoundingBox().inflate(DENSITY_CHECK_RADIUS);
            int count = event.getLevel().getEntitiesOfClass(Mob.class, checkArea,
                    e -> typeKey.equals(ForgeRegistries.ENTITY_TYPES.getKey(e.getType()))
            ).size();

            if (count >= MAX_DECORATION_DENSITY) {
                event.setSpawnCancelled(true);
                event.setCanceled(true);
            }
        } else if (typeKey.equals(WHIRLPOOL_ID)) {
            // whirlpool bug fix
            AABB checkArea = mob.getBoundingBox().inflate(16.0);
            boolean hasNearby = !event.getLevel().getEntitiesOfClass(Mob.class, checkArea,
                    e -> WHIRLPOOL_ID.equals(ForgeRegistries.ENTITY_TYPES.getKey(e.getType()))
            ).isEmpty();

            if (hasNearby) {
                event.setSpawnCancelled(true);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        Entity entity = event.getEntity();
        ResourceLocation typeKey = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());

        if (typeKey != null && NO_AI_ENTITIES.contains(typeKey) && entity instanceof Mob mob) {
            mob.setNoAi(true);
            mob.setNoGravity(true);
            mob.setDeltaMovement(0, 0, 0);
        }
    }

    // disables the stupid ass annoying whispers from playing at all
    @SubscribeEvent
    public static void onPlayLevelSound(PlayLevelSoundEvent event) {
        Holder<SoundEvent> soundHolder = event.getSound();
        if (soundHolder == null) return;

        ResourceLocation soundLoc = soundHolder.value().getLocation();

        if (soundLoc.equals(WHISPERS_ID)) {
            if (event.isCancelable()) {
                event.setCanceled(true);
            }
            event.setNewVolume(0.0f);
            return;
        }

        // changes the marauders hit sound to an actual fleshy sound (it was skeleton hit sound before)
        if (soundLoc.equals(SKELETON_HURT_ID) && event instanceof PlayLevelSoundEvent.AtEntity atEntityEvent) {
            Entity entity = atEntityEvent.getEntity();
            if (entity != null) {
                ResourceLocation entityLoc = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
                if (MARAUDER_ID.equals(entityLoc) || MARAUDER_NIGHT_ID.equals(entityLoc)) {
                    if (event.isCancelable()) {
                        event.setCanceled(true);
                    }
                    event.setNewVolume(0.0f);

                    SoundEvent newSound = ForgeRegistries.SOUND_EVENTS.getValue(MEATY_HIT_ID);
                    if (newSound != null && !entity.level().isClientSide()) {
                        entity.playSound(newSound, event.getNewVolume(), event.getNewPitch());
                    }
                }
            }
        }
    }
}