package com.doug.macabrefix.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class MacabrefixConfig {
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.BooleanValue ENTITY_RELATED_FIXES;
    private static final ForgeConfigSpec.BooleanValue ATTRIBUTE_COMPATIBILITY_FIX;
    private static final ForgeConfigSpec.BooleanValue NETWORK_SYNC_THROTTLE_FIX;
    private static final ForgeConfigSpec.BooleanValue BOSS_TICK_SORT_FIX;
    private static final ForgeConfigSpec.BooleanValue STRUCTURE_TRIGGER_FIX;
    private static final ForgeConfigSpec.BooleanValue BOSS_SPAWNER_DUPLICATE_FIX;
    private static final ForgeConfigSpec.BooleanValue PIT_EFFECT_QUEUE_SYNC_FIX;
    private static final ForgeConfigSpec.BooleanValue ENTITY_SOUND_FALLBACK_FIX;
    private static final ForgeConfigSpec.BooleanValue LEAF_DECAY_FIX;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("fixes");
        ENTITY_RELATED_FIXES = builder
                .comment("Enable merged PR entity handlers: density cap, AI/gravity disable, suffocation immunity, whirlpool duplicate cancel, and whisper mute.")
                .define("entityRelatedFixes", true);
        ATTRIBUTE_COMPATIBILITY_FIX = builder
                .comment("Enable the Macabre attribute base reset compatibility fix.")
                .define("attributeCompatibilityFix", true);
        NETWORK_SYNC_THROTTLE_FIX = builder
                .comment("Enable Macabre player/saved-data sync throttles.")
                .define("networkSyncThrottleFix", true);
        BOSS_TICK_SORT_FIX = builder
                .comment("Enable removal of unused nearby-entity sorting in Macabre boss tick procedures.")
                .define("bossTickSortFix", true);
        STRUCTURE_TRIGGER_FIX = builder
                .comment("Enable cleanup of hidden dungeon/village structure trigger blocks without running the expensive template procedures.")
                .define("structureTriggerFix", true);
        BOSS_SPAWNER_DUPLICATE_FIX = builder
                .comment("Enable Hollow Man auto-spawner duplicate prevention and lower-allocation trigger checks.")
                .define("bossSpawnerDuplicateFix", true);
        PIT_EFFECT_QUEUE_SYNC_FIX = builder
                .comment("Enable bounded Pit effect overlay queue and sync behavior.")
                .define("pitEffectQueueSyncFix", true);
        ENTITY_SOUND_FALLBACK_FIX = builder
                .comment("Enable fallback sounds when inspected Macabre eye entity sound lookups are missing.")
                .define("entitySoundFallbackFix", true);
        LEAF_DECAY_FIX = builder
                .comment("Enable Macabre tree leaf decay support.")
                .define("leafDecayFix", true);
        builder.pop();

        SPEC = builder.build();
    }

    private MacabrefixConfig() {
    }

    public static boolean entityRelatedFixesEnabled() {
        return ENTITY_RELATED_FIXES.get();
    }

    public static boolean attributeCompatibilityFixEnabled() {
        return ATTRIBUTE_COMPATIBILITY_FIX.get();
    }

    public static boolean networkSyncThrottleFixEnabled() {
        return NETWORK_SYNC_THROTTLE_FIX.get();
    }

    public static boolean bossTickSortFixEnabled() {
        return BOSS_TICK_SORT_FIX.get();
    }

    public static boolean structureTriggerFixEnabled() {
        return STRUCTURE_TRIGGER_FIX.get();
    }

    public static boolean bossSpawnerDuplicateFixEnabled() {
        return BOSS_SPAWNER_DUPLICATE_FIX.get();
    }

    public static boolean pitEffectQueueSyncFixEnabled() {
        return PIT_EFFECT_QUEUE_SYNC_FIX.get();
    }

    public static boolean entitySoundFallbackFixEnabled() {
        return ENTITY_SOUND_FALLBACK_FIX.get();
    }

    public static boolean leafDecayFixEnabled() {
        return LEAF_DECAY_FIX.get();
    }
}
