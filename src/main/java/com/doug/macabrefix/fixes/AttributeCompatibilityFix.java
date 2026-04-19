package com.doug.macabrefix.fixes;

import com.curseforge.macabre.network.MacabreModVariables;
import com.curseforge.macabre.network.MacabreModVariables.PlayerVariables;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

public final class AttributeCompatibilityFix {
    private static final double EPSILON = 0.0000001D;

    private static final TrackedAttribute[] TRACKED_ATTRIBUTES = {
            new TrackedAttribute(() -> ForgeMod.ENTITY_GRAVITY.get()),
            new TrackedAttribute(() -> Attributes.MOVEMENT_SPEED),
            new TrackedAttribute(() -> ForgeMod.BLOCK_REACH.get()),
            new TrackedAttribute(() -> ForgeMod.ENTITY_REACH.get()),
            new TrackedAttribute(() -> Attributes.ATTACK_KNOCKBACK),
            new TrackedAttribute(() -> Attributes.ATTACK_SPEED),
            new TrackedAttribute(() -> Attributes.KNOCKBACK_RESISTANCE),
            new TrackedAttribute(() -> Attributes.MAX_HEALTH),
            new TrackedAttribute(() -> ForgeMod.SWIM_SPEED.get()),
            new TrackedAttribute(() -> Attributes.ATTACK_DAMAGE)
    };

    private static final ArmorBonus[] ARMOR_BONUSES = {
            new ArmorBonus(
                    () -> ForgeMod.ENTITY_GRAVITY.get(),
                    UUID.fromString("5b5f7e7f-22be-4c46-83fc-a4af70f21b3d"),
                    "macabrefix.macabre_armor.gravity",
                    0.08D,
                    variables -> variables != null && variables.baalArmor ? -0.04D : 0.0D),
            new ArmorBonus(
                    () -> Attributes.MOVEMENT_SPEED,
                    UUID.fromString("701a8ba7-9834-438e-9956-73b01cc9f0db"),
                    "macabrefix.macabre_armor.movement_speed",
                    0.1D,
                    variables -> variables != null && variables.baalArmor ? 0.2D : 0.0D),
            new ArmorBonus(
                    () -> ForgeMod.BLOCK_REACH.get(),
                    UUID.fromString("1564a04f-6a2d-40e1-8d31-40879cf0be63"),
                    "macabrefix.macabre_armor.block_reach",
                    4.5D,
                    variables -> variables != null && variables.gomoriaArmor ? 3.5D : 0.0D),
            new ArmorBonus(
                    () -> ForgeMod.ENTITY_REACH.get(),
                    UUID.fromString("476c7b1b-1248-42fe-8c0e-c881c93a5774"),
                    "macabrefix.macabre_armor.entity_reach",
                    3.0D,
                    variables -> variables != null && variables.gomoriaArmor ? 3.0D : 0.0D),
            new ArmorBonus(
                    () -> Attributes.ATTACK_KNOCKBACK,
                    UUID.fromString("bcf197da-0a8d-4afb-82bf-bd380095b202"),
                    "macabrefix.macabre_armor.attack_knockback",
                    0.0D,
                    variables -> variables != null && variables.valamonArmor ? 2.0D : 0.0D),
            new ArmorBonus(
                    () -> Attributes.ATTACK_SPEED,
                    UUID.fromString("70bf3105-b60c-4451-a34c-e0891291755c"),
                    "macabrefix.macabre_armor.attack_speed",
                    4.0D,
                    variables -> variables != null && variables.valamonArmor ? 4.0D : 0.0D),
            new ArmorBonus(
                    () -> Attributes.KNOCKBACK_RESISTANCE,
                    UUID.fromString("2db32858-83b9-4e56-8847-e48d819bfec9"),
                    "macabrefix.macabre_armor.knockback_resistance",
                    0.0D,
                    variables -> variables != null && variables.gargamawArmor ? 2.0D : 0.0D),
            new ArmorBonus(
                    () -> Attributes.MAX_HEALTH,
                    UUID.fromString("f91c3102-4f40-49e3-93d3-d07961c0132e"),
                    "macabrefix.macabre_armor.max_health",
                    20.0D,
                    AttributeCompatibilityFix::maxHealthBonus),
            new ArmorBonus(
                    () -> ForgeMod.SWIM_SPEED.get(),
                    UUID.fromString("2236f656-1a5b-40de-bf7d-f45c7f5bf21c"),
                    "macabrefix.macabre_armor.swim_speed",
                    1.0D,
                    variables -> variables != null && variables.morphegorArmor ? 6.0D : 0.0D),
            new ArmorBonus(
                    () -> Attributes.ATTACK_DAMAGE,
                    UUID.fromString("bcbb08ac-21c6-4e54-a71e-515ec13e5df8"),
                    "macabrefix.macabre_armor.attack_damage",
                    1.0D,
                    variables -> variables != null && variables.morphegorArmor ? 11.0D : 0.0D)
    };

    private static final Map<PlayerKey, double[]> BASE_VALUES = new HashMap<>();
    private static final Set<PlayerKey> SOURCE_BLOCKED_PLAYERS = new HashSet<>();

    private AttributeCompatibilityFix() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(EventPriority.HIGHEST, AttributeCompatibilityFix::onPlayerTickStart);
        eventBus.addListener(EventPriority.LOWEST, AttributeCompatibilityFix::onPlayerTickEnd);
        eventBus.addListener(AttributeCompatibilityFix::onPlayerLoggedOut);
    }

    public static void replaceMacabreAttributeProcedure(Entity entity) {
        if (entity instanceof Player player) {
            SOURCE_BLOCKED_PLAYERS.add(PlayerKey.of(player));
            PlayerVariables variables = player.getCapability(MacabreModVariables.PLAYER_VARIABLES_CAPABILITY)
                    .resolve()
                    .orElse(null);
            applyArmorBonuses(player, variables);
            clampHealth(player);
        }
    }

    private static void onPlayerTickStart(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        Player player = event.player;
        PlayerVariables variables = player.getCapability(MacabreModVariables.PLAYER_VARIABLES_CAPABILITY)
                .resolve()
                .orElse(null);
        double[] baseValues = BASE_VALUES.computeIfAbsent(PlayerKey.of(player), key -> new double[TRACKED_ATTRIBUTES.length]);
        for (int index = 0; index < TRACKED_ATTRIBUTES.length; index++) {
            Attribute attribute = TRACKED_ATTRIBUTES[index].attribute();
            AttributeInstance instance = player.getAttribute(attribute);
            baseValues[index] = instance == null
                    ? Double.NaN
                    : normalizedBaseValue(attribute, instance, variables);
        }
    }

    private static void onPlayerTickEnd(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
        PlayerKey playerKey = PlayerKey.of(player);
        double[] baseValues = BASE_VALUES.get(playerKey);
        if (baseValues == null) {
            return;
        }

        PlayerVariables variables = player.getCapability(MacabreModVariables.PLAYER_VARIABLES_CAPABILITY)
                .resolve()
                .orElse(null);

        if (!SOURCE_BLOCKED_PLAYERS.remove(playerKey)) {
            restoreBaseValues(player, baseValues, variables);
        }
        applyArmorBonuses(player, variables);
        clampHealth(player);
    }

    private static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerKey playerKey = PlayerKey.of(event.getEntity());
        BASE_VALUES.remove(playerKey);
        SOURCE_BLOCKED_PLAYERS.remove(playerKey);
    }

    private static void restoreBaseValues(Player player, double[] baseValues, PlayerVariables variables) {
        for (int index = 0; index < TRACKED_ATTRIBUTES.length; index++) {
            double baseValue = baseValues[index];
            if (Double.isNaN(baseValue)) {
                continue;
            }

            AttributeInstance instance = player.getAttribute(TRACKED_ATTRIBUTES[index].attribute());
            if (instance != null
                    && !sameValue(instance.getBaseValue(), baseValue)
                    && isMacabreBaseOverride(TRACKED_ATTRIBUTES[index].attribute(), instance.getBaseValue(), variables)) {
                instance.setBaseValue(baseValue);
            }
        }
    }

    private static void applyArmorBonuses(Player player, PlayerVariables variables) {
        for (ArmorBonus bonus : ARMOR_BONUSES) {
            AttributeInstance instance = player.getAttribute(bonus.attribute());
            if (instance == null) {
                continue;
            }

            double amount = bonus.amount(variables);
            AttributeModifier current = instance.getModifier(bonus.id());
            if (sameValue(amount, 0.0D)) {
                if (current != null) {
                    instance.removeModifier(bonus.id());
                }
                continue;
            }

            if (current != null && sameValue(current.getAmount(), amount)) {
                continue;
            }

            if (current != null) {
                instance.removeModifier(bonus.id());
            }
            instance.addTransientModifier(new AttributeModifier(
                    bonus.id(),
                    bonus.name(),
                    amount,
                    AttributeModifier.Operation.ADDITION));
        }
    }

    private static double normalizedBaseValue(Attribute attribute, AttributeInstance instance, PlayerVariables variables) {
        double baseValue = instance.getBaseValue();
        for (ArmorBonus bonus : ARMOR_BONUSES) {
            if (bonus.attribute() != attribute || instance.getModifier(bonus.id()) != null) {
                continue;
            }

            double amount = bonus.amount(variables);
            if (!sameValue(amount, 0.0D) && sameValue(baseValue, bonus.fallbackBase() + amount)) {
                return bonus.fallbackBase();
            }
        }
        return baseValue;
    }

    private static boolean isMacabreBaseOverride(Attribute attribute, double baseValue, PlayerVariables variables) {
        for (ArmorBonus bonus : ARMOR_BONUSES) {
            if (bonus.attribute() == attribute && sameValue(baseValue, bonus.fallbackBase() + bonus.amount(variables))) {
                return true;
            }
        }
        return false;
    }

    private static void clampHealth(Player player) {
        float maxHealth = player.getMaxHealth();
        if (player.getHealth() > maxHealth) {
            player.setHealth(maxHealth);
        }
    }

    private static double maxHealthBonus(PlayerVariables variables) {
        if (variables == null) {
            return 0.0D;
        }
        if (variables.gomoriaArmor || variables.baalArmor || variables.valamonArmor) {
            return 20.0D;
        }
        if (variables.gargamawArmor) {
            return 40.0D;
        }
        return 0.0D;
    }

    private static boolean sameValue(double first, double second) {
        return Math.abs(first - second) <= EPSILON;
    }

    private record PlayerKey(UUID playerId, boolean clientSide) {
        private static PlayerKey of(Player player) {
            return new PlayerKey(player.getUUID(), player.level().isClientSide);
        }
    }

    private record TrackedAttribute(Supplier<Attribute> attributeSupplier) {
        private Attribute attribute() {
            return attributeSupplier.get();
        }
    }

    private record ArmorBonus(
            Supplier<Attribute> attributeSupplier,
            UUID id,
            String name,
            double fallbackBase,
            BonusAmount amount) {
        private Attribute attribute() {
            return attributeSupplier.get();
        }

        private double amount(PlayerVariables variables) {
            return amount.amount(variables);
        }
    }

    @FunctionalInterface
    private interface BonusAmount {
        double amount(PlayerVariables variables);
    }
}
