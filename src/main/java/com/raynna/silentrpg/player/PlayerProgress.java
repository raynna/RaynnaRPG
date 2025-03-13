package com.raynna.silentrpg.player;

import com.mojang.logging.LogUtils;
import com.raynna.silentrpg.player.skills.Skills;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.UUID;

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

        if (progress == null) {
            this.progress = new Progress();
        }
    }


    public Skills getSkills() {
        return skills;
    }

    public Progress getProgress() {
        return progress;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("playerUUID", playerUUID.toString());

        if (skills != null) {
            tag.put("skills", skills.toNBT());
            System.out.println("Saving Skills: " + skills.toNBT().toString());
        }
        if (progress != null) {
            tag.put("progress", progress.toNBT());
            System.out.println("Saving Skills: " + progress.toNBT().toString());
        }

        return tag;
    }

    public static PlayerProgress fromNBT(CompoundTag tag) {
        UUID playerUUID = UUID.fromString(tag.getString("playerUUID"));
        PlayerProgress playerProgress = new PlayerProgress(playerUUID);

        if (tag.contains("skills")) {
            playerProgress.skills = Skills.fromNBT(tag.getCompound("skills"));
            System.out.println("Loaded Skills: " + playerProgress.skills.toNBT().toString());
        }

        if (tag.contains("progress")) {
            playerProgress.progress = Progress.fromNBT(tag.getCompound("progress"));
            System.out.println("Loaded Progress: " + playerProgress.progress.toNBT().toString());
        }

        return playerProgress;
    }
}