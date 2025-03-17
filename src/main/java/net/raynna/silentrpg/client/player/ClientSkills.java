package net.raynna.silentrpg.client.player;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.raynna.silentrpg.server.player.skills.Skill;
import net.raynna.silentrpg.server.player.skills.SkillType;
import net.raynna.silentrpg.server.player.skills.Skills;

import java.util.HashMap;
import java.util.Map;

public class ClientSkills {

    private static final Skills skills = new Skills();
    private Player player;


    public ClientSkills(Player player) {
        this.player = player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void updateSkills(Skills newSkills) {
        for (SkillType type : SkillType.values()) {
            Skill newSkill = newSkills.getSkill(type);
            skills.getSkill(type).setLevel(newSkill.getLevel());
            skills.getSkill(type).setXp(newSkill.getXp());
            System.out.println("[Client] updated: Skill: " + type.getName() + " Level: " + skills.getSkill(type).getLevel());
        }
    }

    public int getSkillLevel(SkillType skillType) {
        return skills.getSkill(skillType).getLevel();
    }

    public static Skill getSkill(SkillType skillType) {
        return skills.getSkill(skillType);
    }
}
