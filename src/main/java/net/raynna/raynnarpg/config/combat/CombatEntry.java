package net.raynna.raynnarpg.config.combat;

public record CombatEntry(String key, int level, boolean armour) {
    public CombatEntry(String key, int level) {
        this(key, level, true);
    }
}
