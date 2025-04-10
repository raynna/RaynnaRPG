package net.raynna.raynnarpg.config;

public class ConfigData {
    public final int level;
    public double xp;
    public final String tags;
    public final String raw;

    public ConfigData(int level, double xp, String tags, String raw) {
        this.level = level;
        this.xp = xp;
        this.tags = tags;
        this.raw = raw;
    }

    public ConfigData(int level, double xp, String tags) {
        this(level, xp, tags, "none");
    }

    public ConfigData(int level, double xp) {
        this(level, xp, "none", "none");
    }

    public int getLevel() {
        return level;
    }

    public double getXp() {
        return xp;
    }

    public void setXp(double xp) {
        this.xp = xp;
    }

    public String getTags() {
        return tags;
    }

    public String getRaw() {
        return raw;
    }


    @Override
    public String toString() {
        return "ConfigData{" +
                "level='" + level +
                "', xp='" + xp +
                "', tags='" + tags + '\'' +
                ", raw='" + raw + "'}";
    }
}