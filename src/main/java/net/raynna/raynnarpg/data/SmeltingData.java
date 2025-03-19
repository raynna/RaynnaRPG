package net.raynna.raynnarpg.data;

import java.util.Set;

public class SmeltingData {
    private final String id;
    private final int levelRequirement;
    private final int experience;
    private final String rawMaterial;
    private final Set<String> tags;

    public SmeltingData(String id, int levelRequirement, int experience, String rawMaterial, Set<String> tags) {
        this.id = id;
        this.levelRequirement = levelRequirement;
        this.experience = experience;
        this.rawMaterial = rawMaterial;
        this.tags = tags;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public int getExperience() {
        return experience;
    }

    public String getRawMaterial() {
        return rawMaterial;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getId() {
        return id;
    }
}
