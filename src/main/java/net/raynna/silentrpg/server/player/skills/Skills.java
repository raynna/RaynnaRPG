package net.raynna.silentrpg.server.player.skills;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.raynna.silentrpg.network.packets.skills.SkillsPacketSender;

import java.util.EnumMap;
import java.util.Map;

public class Skills {

    private final static int MAX_LEVEL = 50;
    private final static int MAX_XP = 283854;//to avoid people getting more xp than max lvl, might want to increase if added some type of highscore

    private final Map<SkillType, Skill> skills = new EnumMap<>(SkillType.class);
    private ServerPlayer player;

    public Skills() {
        for (SkillType type : SkillType.values()) {
            skills.put(type, new Skill(type));
        }
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public void addXp(SkillType type, int xp) {
        Skill skill = getSkill(type);
        if (skill != null) {
            if (skill.getXp() >= MAX_XP)
                return;
            int oldLevel = getLevelForXp(skill.getXp());
            if (skill.getXp() + xp > MAX_XP) {
                skill.setXp(MAX_XP);
                return;
            }
            skill.addXp(xp);
            int newLevel = getLevelForXp(skill.getXp());
            if (newLevel > oldLevel) {
                skill.setLevel(newLevel);
                int levels = newLevel - oldLevel;
                onLevelUp(skill, levels);
            }
            SkillsPacketSender.send(player, this);
        }
    }

    public int getLevelForXp(int xp) {
        int points = 0;
        int output;

        double value = 450.0;
        double value2 = 2.5;
        double divisor = 8.0;

        for (int lvl = 1; lvl <= 50; lvl++) {
            points += (int) Math.floor(lvl + value * Math.pow(value2, lvl / divisor));
            output = (int) Math.floor(points / 4);
            if ((output - 1) >= (double) xp) {
                return lvl;
            }
        }
        return 50;
    }

    public Skill getSkill(SkillType type) {
        return skills.get(type);
    }

    public boolean isMaxLevel(SkillType type) {
        Skill skill = getSkill(type);
        return skill != null && skill.getLevel() == MAX_LEVEL;
    }

    private void onLevelUp(Skill skill, int level) {
        if (level > 1) {
            player.sendSystemMessage(Component.literal("Congratulations! You leveled up " + level + " " + skill.getType().getName() + " levels. You are now level " + skill.getLevel() + "!"));
        } else {
            player.sendSystemMessage(Component.literal("Congratulations! You leveled up a " + skill.getType().getName() + " level. You are now level " + skill.getLevel() + "!"));
        }
        if (isMaxLevel(skill.getType())) {
            player.sendSystemMessage(Component.literal("You've achieved the highest level possible in " + skill.getType().getName() + "!"));
        }
    }

    public void resetSkills() {
        for (Skill skill : skills.values()) {
            skill.setLevel(1);
            skill.setXp(0);
        }
        player.sendSystemMessage(Component.literal("You have reset all your skills."));
    }

    public CompoundTag toNBT() {
        CompoundTag skillsTag = new CompoundTag();

        for (Map.Entry<SkillType, Skill> entry : skills.entrySet()) {
            skillsTag.put(entry.getKey().name(), serializeSkill(entry.getValue()));
        }

        return skillsTag;
    }


    public static Skills fromNBT(CompoundTag tag) {
        Skills skills = new Skills();

        for (String skillName : tag.getAllKeys()) {
            SkillType type = SkillType.valueOf(skillName);
            skills.skills.put(type, deserializeSkill(type, tag.getCompound(skillName)));
        }

        return skills;
    }

    private CompoundTag serializeSkill(Skill skill) {
        CompoundTag skillTag = new CompoundTag();
        skillTag.putInt("level", skill.getLevel());
        skillTag.putInt("xp", skill.getXp());
        return skillTag;
    }

    private static Skill deserializeSkill(SkillType type, CompoundTag skillTag) {
        Skill skill = new Skill(type);
        skill.setLevel(skillTag.getInt("level"));
        skill.setXp(skillTag.getInt("xp"));
        return skill;
    }
}
