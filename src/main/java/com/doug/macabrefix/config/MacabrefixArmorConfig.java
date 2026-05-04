package com.doug.macabrefix.config;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.common.ForgeConfigSpec;

public final class MacabrefixArmorConfig {
    public static final ForgeConfigSpec SPEC;

    private static final Map<ResourceLocation, ArmorPieceConfig> PIECES = new HashMap<>();

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("armor");
        defineSet(builder, "symbiotic",
                new String[] {"symbiotic_armor_helmet", "symbiotic_armor_chestplate", "symbiotic_armor_leggings", "symbiotic_armor_boots"},
                new int[] {660, 960, 900, 780}, new int[] {6, 13, 11, 5}, 5.0D, 0.0D);
        defineSet(builder, "bloodClot",
                new String[] {"blood_clot_armor_helmet", "blood_clot_armor_chestplate", "blood_clot_armor_leggings", "blood_clot_armor_boots"},
                new int[] {935, 1360, 1275, 1105}, new int[] {11, 16, 15, 11}, 6.0D, 1.0D);
        defineSet(builder, "plasma",
                new String[] {"plasma_armor_helmet", "plasma_armor_chestplate", "plasma_armor_leggings", "plasma_armor_boots"},
                new int[] {220, 320, 300, 260}, new int[] {10, 15, 14, 10}, 6.0D, 1.0D);
        defineSet(builder, "ferrum",
                new String[] {"ferrum_armor_helmet", "ferrum_armor_chestplate", "ferrum_armor_leggings", "ferrum_armor_boots"},
                new int[] {880, 1280, 1200, 1040}, new int[] {4, 8, 7, 4}, 4.0D, 0.3D);
        defineSet(builder, "abhorrent",
                new String[] {"abhorrent_armor_helmet", "abhorrent_armor_chestplate", "abhorrent_armor_leggings", "abhorrent_armor_boots"},
                new int[] {1100, 1600, 1500, 1300}, new int[] {15, 21, 20, 15}, 6.5D, 1.0D);
        defineSet(builder, "skinsuit",
                new String[] {"skinsuit_helmet", "skinsuit_chestplate", "skinsuit_leggings", "skinsuit_boots"},
                new int[] {297, 432, 405, 351}, new int[] {4, 6, 5, 3}, 0.0D, 0.0D);
        defineSet(builder, "baalCursed",
                new String[] {"baal_armor_helmet", "baal_armor_chestplate", "baal_armor_leggings", "baal_armor_boots"},
                new int[] {1210, 1760, 1650, 1430}, new int[] {18, 32, 25, 16}, 7.0D, 3.0D);
        defineSet(builder, "gomoriaDecaying",
                new String[] {"gomoria_armor_helmet", "gomoria_armor_chestplate", "gomoria_armor_leggings", "gomoria_armor_boots"},
                new int[] {1320, 1920, 1800, 1560}, new int[] {15, 26, 31, 19}, 7.0D, 2.0D);
        defineSet(builder, "valamonCracked",
                new String[] {"valamon_armor_helmet", "valamon_armor_chestplate", "valamon_armor_leggings", "valamon_armor_boots"},
                new int[] {1375, 2000, 1875, 1625}, new int[] {23, 25, 21, 22}, 6.5D, 1.5D);
        defineSet(builder, "gargamawPutrid",
                new String[] {"gargamaw_armor_helmet", "gargamaw_armor_chestplate", "gargamaw_armor_leggings", "gargamaw_armor_boots"},
                new int[] {1155, 1680, 1575, 1365}, new int[] {31, 27, 24, 26}, 6.5D, 5.0D);
        defineSet(builder, "morphegorSplit",
                new String[] {"morphegor_armor_helmet", "morphegor_armor_chestplate", "morphegor_armor_leggings", "morphegor_armor_boots"},
                new int[] {1155, 1680, 1575, 1365}, new int[] {16, 29, 27, 17}, 7.0D, 2.0D);
        builder.pop();

        SPEC = builder.build();
    }

    private MacabrefixArmorConfig() {
    }

    public static ArmorPieceConfig getPiece(ResourceLocation itemId) {
        return PIECES.get(itemId);
    }

    private static void defineSet(ForgeConfigSpec.Builder builder, String setName, String[] itemIds,
            int[] durability, int[] armor, double toughness, double knockbackResistance) {
        builder.push(setName);
        definePiece(builder, "helmet", itemIds[0], EquipmentSlot.HEAD, durability[0], armor[0], toughness, knockbackResistance);
        definePiece(builder, "chestplate", itemIds[1], EquipmentSlot.CHEST, durability[1], armor[1], toughness, knockbackResistance);
        definePiece(builder, "leggings", itemIds[2], EquipmentSlot.LEGS, durability[2], armor[2], toughness, knockbackResistance);
        definePiece(builder, "boots", itemIds[3], EquipmentSlot.FEET, durability[3], armor[3], toughness, knockbackResistance);
        builder.pop();
    }

    private static void definePiece(ForgeConfigSpec.Builder builder, String pieceName, String itemPath,
            EquipmentSlot slot, int defaultDurability, int defaultArmor, double defaultToughness, double defaultKnockbackResistance) {
        builder.push(pieceName);
        ForgeConfigSpec.IntValue durability = builder
                .comment("Maximum durability for macabre:" + itemPath + ".")
                .defineInRange("durability", defaultDurability, 1, Integer.MAX_VALUE);
        ForgeConfigSpec.IntValue armor = builder
                .comment("Armor protection for macabre:" + itemPath + ".")
                .defineInRange("armor", defaultArmor, 0, Integer.MAX_VALUE);
        ForgeConfigSpec.DoubleValue toughness = builder
                .comment("Armor toughness for macabre:" + itemPath + ".")
                .defineInRange("toughness", defaultToughness, 0.0D, Double.MAX_VALUE);
        ForgeConfigSpec.DoubleValue knockbackResistance = builder
                .comment("Knockback resistance for macabre:" + itemPath + ".")
                .defineInRange("knockbackResistance", defaultKnockbackResistance, 0.0D, Double.MAX_VALUE);
        PIECES.put(ResourceLocation.fromNamespaceAndPath("macabre", itemPath),
                new ArmorPieceConfig(slot, durability, armor, toughness, knockbackResistance));
        builder.pop();
    }

    public record ArmorPieceConfig(
            EquipmentSlot slot,
            ForgeConfigSpec.IntValue durability,
            ForgeConfigSpec.IntValue armor,
            ForgeConfigSpec.DoubleValue toughness,
            ForgeConfigSpec.DoubleValue knockbackResistance) {
    }
}
