package com.doug.macabrefix.fixes;

import com.curseforge.macabre.init.MacabreModItems;
import com.curseforge.macabre.init.MacabreModMobEffects;
import com.curseforge.macabre.network.MacabreModVariables;
import com.curseforge.macabre.network.MacabreModVariables.MapVariables;
import com.curseforge.macabre.network.MacabreModVariables.PlayerVariables;
import java.util.function.Supplier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public final class NetworkSyncThrottleFix {
    private NetworkSyncThrottleFix() {
    }

    public static void replaceArmorFlagSync(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        entity.getCapability(MacabreModVariables.PLAYER_VARIABLES_CAPABILITY).ifPresent(variables -> {
            boolean changed = false;
            changed |= setBloodArmor(variables, hasFullSet(
                    livingEntity,
                    MacabreModItems.ABHORRENT_ARMOR_HELMET,
                    MacabreModItems.ABHORRENT_ARMOR_CHESTPLATE,
                    MacabreModItems.ABHORRENT_ARMOR_LEGGINGS,
                    MacabreModItems.ABHORRENT_ARMOR_BOOTS));
            changed |= setBaalArmor(variables, hasFullSet(
                    livingEntity,
                    MacabreModItems.BAAL_ARMOR_HELMET,
                    MacabreModItems.BAAL_ARMOR_CHESTPLATE,
                    MacabreModItems.BAAL_ARMOR_LEGGINGS,
                    MacabreModItems.BAAL_ARMOR_BOOTS));
            changed |= setGomoriaArmor(variables, hasFullSet(
                    livingEntity,
                    MacabreModItems.GOMORIA_ARMOR_HELMET,
                    MacabreModItems.GOMORIA_ARMOR_CHESTPLATE,
                    MacabreModItems.GOMORIA_ARMOR_LEGGINGS,
                    MacabreModItems.GOMORIA_ARMOR_BOOTS));
            changed |= setValamonArmor(variables, hasFullSet(
                    livingEntity,
                    MacabreModItems.VALAMON_ARMOR_HELMET,
                    MacabreModItems.VALAMON_ARMOR_CHESTPLATE,
                    MacabreModItems.VALAMON_ARMOR_LEGGINGS,
                    MacabreModItems.VALAMON_ARMOR_BOOTS));
            changed |= setGargamawArmor(variables, hasFullSet(
                    livingEntity,
                    MacabreModItems.GARGAMAW_ARMOR_HELMET,
                    MacabreModItems.GARGAMAW_ARMOR_CHESTPLATE,
                    MacabreModItems.GARGAMAW_ARMOR_LEGGINGS,
                    MacabreModItems.GARGAMAW_ARMOR_BOOTS));
            changed |= setMorphegorArmor(variables, hasFullSet(
                    livingEntity,
                    MacabreModItems.MORPHEGOR_ARMOR_HELMET,
                    MacabreModItems.MORPHEGOR_ARMOR_CHESTPLATE,
                    MacabreModItems.MORPHEGOR_ARMOR_LEGGINGS,
                    MacabreModItems.MORPHEGOR_ARMOR_BOOTS));

            if (changed) {
                variables.syncPlayerVariables(entity);
            }
        });
    }

    public static void replaceEnterPitSync(Entity entity) {
        boolean hasMacabreEffect = entity instanceof LivingEntity livingEntity
                && livingEntity.hasEffect(MacabreModMobEffects.MACABRE.get());

        entity.getCapability(MacabreModVariables.PLAYER_VARIABLES_CAPABILITY).ifPresent(variables -> {
            if (variables.ENTERPIT != hasMacabreEffect) {
                variables.ENTERPIT = hasMacabreEffect;
                variables.syncPlayerVariables(entity);
            }
        });
    }

    public static void replaceHollowRandomSync(LevelAccessor levelAccessor) {
        RandomSource random = levelAccessor instanceof Level level ? level.getRandom() : RandomSource.create();
        MapVariables.get(levelAccessor).hollow = Mth.nextInt(random, 1, 10);
    }

    public static void replaceBossAbilityRandomSync(LevelAccessor levelAccessor) {
        RandomSource random = levelAccessor instanceof Level level ? level.getRandom() : RandomSource.create();
        updateBossAbilityRandomValues(MapVariables.get(levelAccessor), random);
    }

    private static void updateBossAbilityRandomValues(MapVariables variables, RandomSource random) {
        variables.baalNumber = Mth.nextInt(random, 1, 200);
        variables.gomoriaNumber = Mth.nextInt(random, 1, 300);
        variables.valamonNumber = Mth.nextInt(random, 1, 300);
        variables.gargamawNumber = Mth.nextInt(random, 1, 200);
        variables.morphegorNumber = Mth.nextInt(random, 1, 300);
    }

    private static boolean hasFullSet(
            LivingEntity entity,
            Supplier<? extends Item> helmet,
            Supplier<? extends Item> chestplate,
            Supplier<? extends Item> leggings,
            Supplier<? extends Item> boots) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == helmet.get()
                && entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == chestplate.get()
                && entity.getItemBySlot(EquipmentSlot.LEGS).getItem() == leggings.get()
                && entity.getItemBySlot(EquipmentSlot.FEET).getItem() == boots.get();
    }

    private static boolean setBloodArmor(PlayerVariables variables, boolean value) {
        if (variables.bloodarmor == value) {
            return false;
        }
        variables.bloodarmor = value;
        return true;
    }

    private static boolean setBaalArmor(PlayerVariables variables, boolean value) {
        if (variables.baalArmor == value) {
            return false;
        }
        variables.baalArmor = value;
        return true;
    }

    private static boolean setGomoriaArmor(PlayerVariables variables, boolean value) {
        if (variables.gomoriaArmor == value) {
            return false;
        }
        variables.gomoriaArmor = value;
        return true;
    }

    private static boolean setValamonArmor(PlayerVariables variables, boolean value) {
        if (variables.valamonArmor == value) {
            return false;
        }
        variables.valamonArmor = value;
        return true;
    }

    private static boolean setGargamawArmor(PlayerVariables variables, boolean value) {
        if (variables.gargamawArmor == value) {
            return false;
        }
        variables.gargamawArmor = value;
        return true;
    }

    private static boolean setMorphegorArmor(PlayerVariables variables, boolean value) {
        if (variables.morphegorArmor == value) {
            return false;
        }
        variables.morphegorArmor = value;
        return true;
    }
}
