package net.raynna.silentrpg.data;

import java.util.Set;

public class BlockData {
    private final String id;
    private final int levelRequirement;
    private final int experience;
    private final Set<String> tags; // Store tags as strings

    public BlockData(String id, int levelRequirement, int experience, Set<String> tags) {
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

    public int getExperience() {
        return experience;
    }

    public Set<String> getTags() { // Getter for tags
        return tags;
    }
}