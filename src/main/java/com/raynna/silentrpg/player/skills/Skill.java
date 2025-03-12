package com.raynna.silentrpg.player.skills;

import net.minecraft.server.level.ServerPlayer;

public class Skill {

    private final SkillType type;
    private final Skills owner;

    private int level = 1;
    private int xp = 0;


    public Skill(SkillType type, Skills owner) {
        this.type = type;
        this.owner = owner;
    }

    private ServerPlayer getPlayer() {
        return owner.getPlayer();
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
