package net.raynna.raynnarpg.server.player.skills;

public class Skill {

    private final SkillType type;

    private int level = 1, previousLevel = 1;
    private double xp, previousXp = 0;


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

    public void updateSkill(int level, double xp) {
        this.previousLevel = this.level;
        this.previousXp = this.xp;
        this.level = level;
        this.xp = xp;
    }

    public double getPreviousXp() {
        return previousXp;
    }

    public void setPreviousXp(double previousXp) {
        this.previousXp = previousXp;
    }

    public int getPreviousLevel() {
        return previousLevel;
    }

    public void setPreviousLevel(int previousLevel) {
        this.previousLevel = previousLevel;
    }
}
