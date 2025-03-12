package com.raynna.silentrpg.player;

import com.raynna.silentrpg.player.skills.Skills;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class PlayerStats {

    private final UUID playerUUID;
    private Skills skills;

    public PlayerStats(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.skills = null;
    }

    public void init(ServerPlayer player) {
        if (skills == null) {
            this.skills = new Skills(player);
        }
    }
    public Skills getSkills() {
        return skills;
    }


    public static PlayerStats fromNBT(CompoundTag tag) {
        UUID playerUUID = UUID.fromString(tag.getString("playerUUID"));
        return new PlayerStats(playerUUID);
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("playerUUID", playerUUID.toString());
        return tag;
    }
}