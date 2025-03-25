package net.raynna.raynnarpg.config.mining;

public record MiningEntry(String key, int level, double xp, String... tags) {
    public MiningEntry(String key, int level, double xp) {
        this(key, level, xp, "");
    }
    public MiningEntry(String key, int level, String... tags) {
        this(key, level, 0, tags);
    }
}
