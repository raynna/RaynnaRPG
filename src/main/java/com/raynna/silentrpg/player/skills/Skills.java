package com.raynna.silentrpg.player.skills;

import net.minecraft.server.level.ServerPlayer;

import java.util.EnumMap;
import java.util.Map;

public class Skills {

    private final Map<SkillType, Skill> skills = new EnumMap<>(SkillType.class);
    private final ServerPlayer player;


    public Skills(ServerPlayer player) {
        this.player = player;
        for (SkillType type : SkillType.values()) {
            skills.put(type, new Skill(type, this));
        }
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public void addXp(SkillType type, int xp) {
        Skill skill = getSkill(type);
        if (skill != null) {
            skill.addXp(xp);
        }
    }

    public Skill getSkill(SkillType type) {
        return skills.get(type);
    }


}
