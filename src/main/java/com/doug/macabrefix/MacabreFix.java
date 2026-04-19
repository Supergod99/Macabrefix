package com.doug.macabrefix;

import com.doug.macabrefix.fixes.FixRegistrar;
import com.doug.macabrefix.util.Constants;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public final class MacabreFix {
    public MacabreFix() {
        FixRegistrar.register();
    }
}
