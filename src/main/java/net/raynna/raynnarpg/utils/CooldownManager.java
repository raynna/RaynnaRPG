package net.raynna.raynnarpg.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private static final Map<String, Map<Player, Long>> cooldownMap = new HashMap<>();
    
    /**
     * Checks if an entity can perform an action based on cooldown
     * @param cooldownKey Unique identifier for the cooldown type
     * @param player The ServerPlayer to check
     * @param cooldownMs Duration of the cooldown in milliseconds
     * @return true if cooldown has expired or doesn't exist, false if still on cooldown
     */
    public static boolean checkCooldown(Player player, String cooldownKey, long cooldownMs) {

        long currentTime = System.currentTimeMillis();
        
        // Get or create the cooldown map for this key
        Map<Player, Long> entityCooldowns = cooldownMap.computeIfAbsent(cooldownKey, k -> new HashMap<>());
        
        Long lastTime = entityCooldowns.get(player);
        
        if (lastTime == null || currentTime - lastTime >= cooldownMs) {
            entityCooldowns.put(player, currentTime);
            return true;
        }
        return false;
    }
    
    /**
     * Gets remaining cooldown time for an entity
     * @param cooldownKey Unique identifier for the cooldown type
     * @param player The player to check
     * @return Remaining cooldown time in milliseconds, 0 if no cooldown
     */
    public static long getRemainingCooldown(Player player, String cooldownKey) {
        Map<Player, Long> entityCooldowns = cooldownMap.get(cooldownKey);
        if (entityCooldowns == null) return 0;
        
        Long lastTime = entityCooldowns.get(player);
        if (lastTime == null) return 0;
        
        long elapsed = System.currentTimeMillis() - lastTime;
        long cooldown = getCooldownDuration(cooldownKey);
        return Math.max(0, cooldown - elapsed);
    }
    
    /**
     * Forcefully resets a cooldown for an entity
     * @param cooldownKey Unique identifier for the cooldown type
     * @param player The player to reset
     */
    public static void resetCooldown(Player player, String cooldownKey) {
        Map<Player, Long> entityCooldowns = cooldownMap.get(cooldownKey);
        if (entityCooldowns != null) {
            entityCooldowns.remove(player);
        }
    }
    
    /**
     * Clears all cooldowns of a specific type
     * @param cooldownKey Unique identifier for the cooldown type
     */
    public static void clearCooldowns(String cooldownKey) {
        cooldownMap.remove(cooldownKey);
    }
    
    /**
     * Clears all cooldowns for all types
     */
    public static void clearAllCooldowns() {
        cooldownMap.clear();
    }
    
    // Optional: Store default cooldown durations if needed
    private static final Map<String, Long> defaultCooldowns = new HashMap<>();
    
    /**
     * Sets a default cooldown duration for a specific key
     * @param cooldownKey Unique identifier for the cooldown type
     * @param durationMs Default duration in milliseconds
     */
    public static void setDefaultCooldown(String cooldownKey, long durationMs) {
        defaultCooldowns.put(cooldownKey, durationMs);
    }
    
    /**
     * Gets the default cooldown duration for a key
     * @param cooldownKey Unique identifier for the cooldown type
     * @return Default duration in milliseconds, 0 if not set
     */
    public static long getCooldownDuration(String cooldownKey) {
        return defaultCooldowns.getOrDefault(cooldownKey, 0L);
    }
}