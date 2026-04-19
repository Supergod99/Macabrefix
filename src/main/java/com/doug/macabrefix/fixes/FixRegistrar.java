package com.doug.macabrefix.fixes;

import net.minecraftforge.common.MinecraftForge;

public final class FixRegistrar {
    private static boolean registered;

    private FixRegistrar() {
    }

    public static void register() {
        if (registered) {
            return;
        }

        registered = true;
        AttributeCompatibilityFix.register(MinecraftForge.EVENT_BUS);
        LeafDecayFix.register(MinecraftForge.EVENT_BUS);
    }
}
