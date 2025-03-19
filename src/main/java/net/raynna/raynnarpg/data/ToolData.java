package net.raynna.raynnarpg.data;

import java.util.Set;

public class ToolData {

    private final String id;
    private final int levelRequirement;
    private final Set<String> tags;

    public ToolData(String id, int levelRequirement, Set<String> tags) {
        this.id = id;
        this.levelRequirement = levelRequirement;
        this.tags = tags;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getId() {
        return id;
    }
}
