package net.raynna.raynnarpg.server.player;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModList;
import net.raynna.raynnarpg.network.packets.skills.SkillsPacketSender;
import net.raynna.raynnarpg.server.player.progress.Progress;
import net.raynna.raynnarpg.server.player.progress.ProgressKey;
import net.raynna.raynnarpg.server.player.skills.Skill;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.server.player.skills.Skills;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.raynna.raynnarpg.utils.Colour;
import net.raynna.raynnarpg.utils.MessageSender;
import net.raynna.raynnarpg.utils.StarterItems;
import net.raynna.raynnarpg.utils.Utils;
import org.slf4j.Logger;

import java.util.UUID;

import static net.raynna.raynnarpg.RaynnaRPG.MOD_NAME;

public class PlayerProgress {

    private static final Logger LOGGER = LogUtils.getLogger();

    private UUID playerUUID;
    private Skills skills;
    private Progress progress;

    public PlayerProgress(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.skills = null;
        this.progress = null;
    }

    public void init(ServerPlayer player) {
        if (this.playerUUID == null) {
            this.playerUUID = player.getUUID();
        }
        if (skills == null) {
            this.skills = new Skills();
        }
        skills.setPlayer(player);
        for (SkillType skill : SkillType.values()) {
            if (skills.getSkill(skill).getLevel() < 1) {
                skills.getSkill(skill).setLevel(1);
                MessageSender.send(player,Colour.RED + "Your " + skill.getName() + " level has been set to lvl 1, due to being 0.");
            }

        }
        if (progress == null) {
            this.progress = new Progress();
        }
        SkillsPacketSender.send(player, this.skills);
        if (progress.isActive(ProgressKey.FIRST_TIME_LOGGED_IN)) {
            progress.toggle(ProgressKey.FIRST_TIME_LOGGED_IN);
            if (ModList.get().isLoaded("silentgear")) {
                StarterItems.giveItems(player);
            }
            MessageSender.send(player, "["+ MOD_NAME + "] Welcome to the server " + player.getName().toFlatList().getFirst().getString() + "!", Colour.Colours.GOLD);
        } else {
            MessageSender.send(player, "["+ MOD_NAME + "] Welcome back " + player.getName().toFlatList().getFirst().getString() + "!", Colour.Colours.GOLD);
        }
        MessageSender.send(player, "["+ MOD_NAME + "] Your current skills: ", Colour.Colours.RED);
        for (SkillType type : SkillType.values()) {
            MessageSender.send(player, type.getName() + " - Level: " + skills.getSkill(type).getLevel() + ", Xp: " + Utils.formatNumber(skills.getSkill(type).getXp()), Colour.Colours.YELLOW);
        }
        System.out.println("["+player.getName().getString()+"] Loaded Skills: " + skills.toNBT().toString());
        System.out.println("["+player.getName().getString()+"] Loaded Progress: " + progress.toNBT().toString());
    }


    public Skills getSkills() {
        return skills;
    }

    public Progress getProgress() {
        return progress;
    }

    public PlayerProgress get(ServerPlayer player) {
        return this;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("playerUUID", playerUUID.toString());

        if (skills != null) {
            tag.put("skills", skills.toNBT());
        }
        if (progress != null) {
            tag.put("progress", progress.toNBT());
        }

        return tag;
    }

    public static PlayerProgress fromNBT(CompoundTag tag) {
        UUID playerUUID = UUID.fromString(tag.getString("playerUUID"));
        PlayerProgress playerProgress = new PlayerProgress(playerUUID);

        if (tag.contains("skills")) {
            playerProgress.skills = Skills.fromNBT(tag.getCompound("skills"));
            for (SkillType type : SkillType.values()) {
                if (playerProgress.skills.getSkill(type) == null) {
                    playerProgress.skills.getSkills().put(type, new Skill(type));
                    System.out.println("[Server] Added missing SkillType: " + type.name() + " with default values.");
                }
            }
        }

        if (tag.contains("progress")) {
            playerProgress.progress = Progress.fromNBT(tag.getCompound("progress"));
        }

        return playerProgress;
    }
}