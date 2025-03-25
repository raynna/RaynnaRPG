package net.raynna.raynnarpg.config.smelting;

public record SmeltingEntry(String key, int level, double xp, String rawVariant) {
    public SmeltingEntry(String key, int level, double xp) {
        this(key, level, xp, "");
    }
}
