package com.doug.macabrefix.fixes;

import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;

@SuppressWarnings("deprecation")
public final class BossArmorTooltipFix {
    private static final Map<String, String> TOOLTIP_KEYS = Map.ofEntries(
            Map.entry("macabre:baal_armor_helmet", "tooltip.macabrefix.boss_armor.baal"),
            Map.entry("macabre:baal_armor_chestplate", "tooltip.macabrefix.boss_armor.baal"),
            Map.entry("macabre:baal_armor_leggings", "tooltip.macabrefix.boss_armor.baal"),
            Map.entry("macabre:baal_armor_boots", "tooltip.macabrefix.boss_armor.baal"),
            Map.entry("macabre:gomoria_armor_helmet", "tooltip.macabrefix.boss_armor.gomoria"),
            Map.entry("macabre:gomoria_armor_chestplate", "tooltip.macabrefix.boss_armor.gomoria"),
            Map.entry("macabre:gomoria_armor_leggings", "tooltip.macabrefix.boss_armor.gomoria"),
            Map.entry("macabre:gomoria_armor_boots", "tooltip.macabrefix.boss_armor.gomoria"),
            Map.entry("macabre:valamon_armor_helmet", "tooltip.macabrefix.boss_armor.valamon"),
            Map.entry("macabre:valamon_armor_chestplate", "tooltip.macabrefix.boss_armor.valamon"),
            Map.entry("macabre:valamon_armor_leggings", "tooltip.macabrefix.boss_armor.valamon"),
            Map.entry("macabre:valamon_armor_boots", "tooltip.macabrefix.boss_armor.valamon"),
            Map.entry("macabre:gargamaw_armor_helmet", "tooltip.macabrefix.boss_armor.gargamaw"),
            Map.entry("macabre:gargamaw_armor_chestplate", "tooltip.macabrefix.boss_armor.gargamaw"),
            Map.entry("macabre:gargamaw_armor_leggings", "tooltip.macabrefix.boss_armor.gargamaw"),
            Map.entry("macabre:gargamaw_armor_boots", "tooltip.macabrefix.boss_armor.gargamaw"),
            Map.entry("macabre:morphegor_armor_helmet", "tooltip.macabrefix.boss_armor.morphegor"),
            Map.entry("macabre:morphegor_armor_chestplate", "tooltip.macabrefix.boss_armor.morphegor"),
            Map.entry("macabre:morphegor_armor_leggings", "tooltip.macabrefix.boss_armor.morphegor"),
            Map.entry("macabre:morphegor_armor_boots", "tooltip.macabrefix.boss_armor.morphegor"));

    private BossArmorTooltipFix() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(BossArmorTooltipFix::onItemTooltip);
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(event.getItemStack().getItem());
        String tooltipKey = TOOLTIP_KEYS.get(itemId.toString());
        if (tooltipKey != null) {
            event.getToolTip().add(Component.translatable(tooltipKey).withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
}
