package net.raynna.raynnarpg.config.tools;

public record ToolEntry(String key, int level, boolean silentgear) {
    public ToolEntry(String key, int level) {
        this(key, level, false);
    }
}
