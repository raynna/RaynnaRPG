package com.raynna.silentrpg.player.skills;

public class Skill {

    private final SkillType type;

    private int level = 1;
    private int xp = 0;


    public Skill(SkillType type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public SkillType getType() {
        return type;
    }

    public int getXp() {
        return xp;
    }

    public void addXp(int xp) {
         this.xp += xp;
    }

}
