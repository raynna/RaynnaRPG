package net.raynna.raynnarpg.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerDamageTracker {
    private static final Map<UUID, Map<UUID, DamageRecord>> damageMap = new ConcurrentHashMap<>();

    public static void recordDamage(ServerPlayer attacker, LivingEntity target, float amount) {
        float actualDamage = Math.min(amount, target.getHealth());
        damageMap.computeIfAbsent(attacker.getUUID(), k -> new ConcurrentHashMap<>()).compute(target.getUUID(), (k, v) -> {
            if (v == null) {
                return new DamageRecord(actualDamage);
            } else {
                return v.addDamage(actualDamage);
            }
        });
    }

    public static List<ServerPlayer> getAttackers(LivingEntity entity) {
        return damageMap.entrySet().stream()
                .filter(entry -> entry.getValue().containsKey(entity.getUUID()))
                .map(entry -> {
                    Player player = entity.level().getPlayerByUUID(entry.getKey());
                    return player instanceof ServerPlayer ? (ServerPlayer)player : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static float getDamageDealt(ServerPlayer player, LivingEntity entity) {
        Map<UUID, DamageRecord> playerDamage = damageMap.getOrDefault(player.getUUID(), Collections.emptyMap());
        DamageRecord record = playerDamage.get(entity.getUUID());
        return record == null ? 0 : record.totalDamage;
    }

    public static void clearRecords(LivingEntity entity) {
        damageMap.values().forEach(map -> map.remove(entity.getUUID()));
    }

    public static void clearPlayerRecords(ServerPlayer player) {
        int recordsRemoved = damageMap.containsKey(player.getUUID()) ?
                damageMap.get(player.getUUID()).size() : 0;

        damageMap.remove(player.getUUID());
        System.out.println("[DEBUG] Removed " + recordsRemoved + " damage records for player");
    }

    private static class DamageRecord {
        final float totalDamage;
        final long lastHitTime;

        DamageRecord(float damage) {
            this.totalDamage = damage;
            this.lastHitTime = System.currentTimeMillis();
        }

        DamageRecord addDamage(float additional) {
            return new DamageRecord(this.totalDamage + additional);
        }
    }
}