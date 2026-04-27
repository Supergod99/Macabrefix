package com.doug.macabrefix;

import com.doug.macabrefix.config.MacabrefixConfig;
import com.doug.macabrefix.fixes.EntityRelatedFixes;
import com.doug.macabrefix.fixes.FixRegistrar;
import com.doug.macabrefix.util.Constants;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public final class MacabreFix {
    public MacabreFix(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, MacabrefixConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(EntityRelatedFixes.class);
        FixRegistrar.register();
    }
}
