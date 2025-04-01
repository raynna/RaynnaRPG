package net.raynna.raynnarpg.server.player.skills;

import ca.weblite.objc.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.raynna.raynnarpg.network.packets.skills.SkillsPacketSender;
import net.raynna.raynnarpg.network.packets.toasts.CustomToastPacketSender;
import net.raynna.raynnarpg.server.player.PlayerProgress;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataStorage;
import net.raynna.raynnarpg.utils.Colour;
import net.raynna.raynnarpg.utils.MessageSender;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Skills {

    private final static int MAX_LEVEL = 50;
    private final static double MAX_XP = 283854;//to avoid people getting more xp than max lvl, might want to increase if added some type of highscore

    private final Map<SkillType, Skill> skills = new EnumMap<>(SkillType.class);
    private ServerPlayer player;

    public Skills() {
        for (SkillType type : SkillType.values()) {
            skills.put(type, new Skill(type));
        }
    }

    public Map<SkillType, Skill> getSkills() {
        return skills;
    }

    public void setPlayer(ServerPlayer player) {
        this.player = player;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public void addXp(SkillType type, double xp) {
        Skill skill = getSkill(type);
        if (skill != null) {
            if (skill.getXp() >= MAX_XP)
                return;
            int oldLevel = skill.getLevel();
            if (skill.getXp() + xp > MAX_XP) {
                skill.setXp(MAX_XP);
                return;
            }
            skill.setPreviousXp(skill.getXp());
            skill.addXp(xp);
            skill.setXp(Math.round(skill.getXp() * 100.0) / 100.0);
            int newLevel = getLevelForXp(skill.getXp());
            if (newLevel > oldLevel) {
                skill.setPreviousLevel(oldLevel);
                skill.setLevel(newLevel);
                int levels = newLevel - oldLevel;
                onLevelUp(skill, levels);
            }
            SkillsPacketSender.send(player, this);
            //System.out.println("[Server] Skill: " + skill.getType().getName() + " XP: " + skill.getXp() + " Next Level XP: " + Skills.getXpForLevel(skill.getLevel() + 1));

        }
    }

    public int getLevelForXp(double xp) {
        for (int level = 1; level <= 50; level++) {
            if (getXpForLevel(level) > xp) {
                return level - 1;
            }
        }
        return 50;
    }

    public static double getXpForLevel(int level) {
        double totalXP = 0;
        for (int n = 1; n < level; n++) {
            totalXP += Math.floor(n + 230 * Math.pow(2, n / 6.0));
        }

        return (long) Math.floor(totalXP / 2);
    }

    public static double getXpForMaterial(int level, SkillType type) {
        double baseXp = 1;
        if (type == SkillType.MINING)
            baseXp = 3;
        if (type == SkillType.SMELTING)
            baseXp = 6;
        if (type == SkillType.CRAFTING)
            baseXp = 3;
        double xpIncreaseFactor = 1.12;
        double xp = baseXp * Math.pow(xpIncreaseFactor, level - 1);

        return Math.round(xp * 100.0) / 100.0;
    }

    public Skill getSkill(SkillType type) {
        return skills.get(type);
    }

    public boolean isMaxLevel(SkillType type) {
        Skill skill = getSkill(type);
        return skill != null && skill.getLevel() == MAX_LEVEL;
    }

    private void onLevelUp(Skill skill, int level) {
        String skillName = skill.getType().getName();
        int currentLevel = skill.getLevel();
        String playerName = player.getName().getString();

        if (level > 1) {
            MessageSender.send(player, "Congratulations! You leveled up " + level + " " + skillName + " levels. You are now level " + currentLevel + "!", Colour.Colours.GREEN);
        } else {
            MessageSender.send(player, "Congratulations! You leveled up a " + skillName + " level. You are now level " + currentLevel + "!", Colour.Colours.GREEN);
        }
        player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
        CustomToastPacketSender.send(player, "Level up!", "You have reached level " + currentLevel + " in " + skillName + "!");
        boolean reachedMax = isMaxLevel(skill.getType());
        if (reachedMax) {
            MessageSender.send(player, "You've achieved the highest level possible in " + skillName + "!", Colour.Colours.GOLD);
        }

        MinecraftServer server = player.getServer();
        if (server == null) return;

        String levelUpMessage = (level > 1)
                ? playerName + " has leveled up " + level + " " + skillName + " levels. " + playerName + " is now level " + currentLevel + "!"
                : playerName + " has leveled up a " + skillName + " level. " + playerName + " is now level " + currentLevel + "!";

        String maxedMessage = playerName + " achieved the highest level possible in " + skillName + "!";

        MessageSender.sendAllButSelf(player, levelUpMessage, Colour.Colours.YELLOW);
        if (reachedMax) {
            MessageSender.sendAllButSelf(player, maxedMessage, Colour.Colours.GOLD);
        }
        if (player != null) {
            PlayerDataStorage.savePlayer(player);
        }
    }


    public void resetSkills() {
        for (Skill skill : skills.values()) {
            skill.setLevel(1);
            skill.setXp(0);
        }
        SkillsPacketSender.send(player, this);
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
            try {
                SkillType type = SkillType.valueOf(skillName);
                skills.skills.put(type, deserializeSkill(type, tag.getCompound(skillName)));
            } catch (IllegalArgumentException e) {
                System.out.println("Warning: SkillType " + skillName + " does not exist and will be removed.");
            }
        }
        for (SkillType type : SkillType.values()) {
            if (!skills.skills.containsKey(type)) {
                skills.skills.put(type, new Skill(type));
            }
        }

        return skills;
    }

    private CompoundTag serializeSkill(Skill skill) {
        CompoundTag skillTag = new CompoundTag();
        skillTag.putInt("level", skill.getLevel());
        skillTag.putDouble("xp", skill.getXp());
        return skillTag;
    }

    private static Skill deserializeSkill(SkillType type, CompoundTag skillTag) {
        Skill skill = new Skill(type);
        skill.setLevel(skillTag.getInt("level"));
        skill.setXp(skillTag.getDouble("xp"));
        return skill;
    }
}
