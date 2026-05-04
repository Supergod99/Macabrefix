package com.doug.macabrefix.fixes;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class FixRegistrar {
    private static boolean registered;

    private FixRegistrar() {
    }

    public static void register() {
        if (registered) {
            return;
        }

        registered = true;
        ArmorStatConfigFix.register(MinecraftForge.EVENT_BUS);
        AttributeCompatibilityFix.register(MinecraftForge.EVENT_BUS);
        if (FMLEnvironment.dist.isClient()) {
            BossArmorTooltipFix.register(MinecraftForge.EVENT_BUS);
        }
        LeafDecayFix.register(MinecraftForge.EVENT_BUS);
    }
}
