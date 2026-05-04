package com.doug.macabrefix.mixin;

import com.doug.macabrefix.fixes.ArmorStatConfigFix;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MacabreArmorDurabilityMixin {
    @Inject(method = "getMaxDamage", at = @At("HEAD"), cancellable = true)
    private void macabrefix$getMaxDamage(CallbackInfoReturnable<Integer> cir) {
        int configuredDurability = ArmorStatConfigFix.getConfiguredMaxDamage((ItemStack) (Object) this);
        if (configuredDurability > 0) {
            cir.setReturnValue(configuredDurability);
        }
    }
}
