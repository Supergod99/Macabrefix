package com.doug.macabrefix.fixes;

import com.doug.macabrefix.config.MacabrefixArmorConfig;
import com.doug.macabrefix.config.MacabrefixArmorConfig.ArmorPieceConfig;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public final class ArmorStatConfigFix {
    private static final String MODIFIER_PREFIX = "macabrefix_armor_config_";

    private ArmorStatConfigFix() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ArmorStatConfigFix::onItemAttributeModifiers);
    }

    public static int getConfiguredMaxDamage(ItemStack stack) {
        ArmorPieceConfig piece = getConfiguredPiece(stack);
        return piece == null ? -1 : piece.durability().get();
    }

    private static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ArmorPieceConfig piece = getConfiguredPiece(event.getItemStack());
        if (piece == null || event.getSlotType() != piece.slot()) {
            return;
        }

        replaceAttribute(event, piece, Attributes.ARMOR, piece.armor().get());
        replaceAttribute(event, piece, Attributes.ARMOR_TOUGHNESS, piece.toughness().get());
        replaceAttribute(event, piece, Attributes.KNOCKBACK_RESISTANCE, piece.knockbackResistance().get());
    }

    private static ArmorPieceConfig getConfiguredPiece(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return MacabrefixArmorConfig.getPiece(itemId);
    }

    private static void replaceAttribute(ItemAttributeModifierEvent event, ArmorPieceConfig piece,
            Attribute attribute, double value) {
        event.removeAttribute(attribute);
        if (value == 0.0D) {
            return;
        }

        EquipmentSlot slot = piece.slot();
        String modifierName = MODIFIER_PREFIX + slot.getName() + "_" + BuiltInRegistries.ATTRIBUTE.getKey(attribute).getPath();
        event.addModifier(attribute, new AttributeModifier(
                modifierId(slot, attribute),
                () -> modifierName,
                value,
                AttributeModifier.Operation.ADDITION));
    }

    private static UUID modifierId(EquipmentSlot slot, Attribute attribute) {
        ResourceLocation attributeId = BuiltInRegistries.ATTRIBUTE.getKey(attribute);
        String id = "macabrefix:armor_config/" + slot.getName() + "/" + attributeId;
        return UUID.nameUUIDFromBytes(id.getBytes(StandardCharsets.UTF_8));
    }
}
