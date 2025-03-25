package net.raynna.raynnarpg.config.crafting;

public record CraftingEntry(String key, int level, double xp, String... tags) {
    public CraftingEntry(String key, int level, double xp) {
        this(key, level, xp, "");
    }
    public CraftingEntry(String key, int level, String... tags) {
        this(key, level, 0, tags);
    }
}
