package net.raynna.raynnarpg.client.player;

import net.minecraft.world.entity.player.Player;
import net.raynna.raynnarpg.server.player.skills.Skill;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.server.player.skills.Skills;

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
            Skill existingSkill = skills.getSkill(type);
            if (existingSkill != null && newSkill != null) {
                skills.getSkill(type).updateSkill(newSkill.getLevel(), newSkill.getXp());
                //System.out.println("[Client] updated: Skill: " + type.getName() + " Level: " + skills.getSkill(type).getLevel());
                //System.out.println("[Client] Skill: " + skills.getSkill(type).getType().getName() + " XP: " + skills.getSkill(type).getXp() + " Next Level XP: " + Skills.getXpForLevel(skills.getSkill(type).getLevel() + 1));
            }
        }
    }

    public int getSkillLevel(SkillType skillType) {
        Skill skill = skills.getSkill(skillType);
        if (skill == null) {
            return 1;
        }
        return skill.getLevel();
    }

    public double getSkillExperience(SkillType skillType) {
        Skill skill = skills.getSkill(skillType);
        if (skill == null)
            return 0;
        return skill.getXp();
    }

    public int getLevelForXp(double xp) {
        for (int level = 1; level <= Skills.MAX_LEVEL; level++) {
            if (getXpForLevel(level) > xp) {
                return level - 1;
            }
        }
        return Skills.MAX_LEVEL;
    }

    public static double getXpForLevel(int level) {
        double totalXP = 0;
        for (int n = 1; n < level; n++) {
            totalXP += Math.floor(n + 230 * Math.pow(2, n / 6.0));
        }
        return (long) Math.floor(totalXP / 2);
    }

    public static Skill getSkill(SkillType skillType) {
        return skills.getSkill(skillType);
    }
}
