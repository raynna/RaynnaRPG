package net.raynna.raynnarpg.server.player.skills;

public class Skill {

    private final SkillType type;

    private int level = 1;
    private double xp = 0;


    public Skill(SkillType type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setXp(double xp) {
        this.xp = xp;
    }

    public SkillType getType() {
        return type;
    }

    public double getXp() {
        return xp;
    }

    public void addXp(double xp) {
         this.xp += xp;
    }

}
