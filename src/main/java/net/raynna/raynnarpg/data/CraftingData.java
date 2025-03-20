package net.raynna.raynnarpg.data;

import java.util.Set;

public class CraftingData {
    private final String id;
    private final int levelRequirement;
    private final double experience;
    private final Set<String> tags;

    public CraftingData(String id, int levelRequirement, double experience, Set<String> tags) {
        this.id = id;
        this.levelRequirement = levelRequirement;
        this.experience = experience;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public double getExperience() {
        return experience;
    }

    public Set<String> getTags() {
        return tags;
    }
}
