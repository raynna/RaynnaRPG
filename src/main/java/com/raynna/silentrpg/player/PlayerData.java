package com.raynna.silentrpg.player;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData extends SavedData {
    private final Map<UUID, PlayerStats> playerStats = new HashMap<>();

    // Create new instance
    public static PlayerData create() {
        return new PlayerData();
    }

    // Load existing data
    public static PlayerData load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        PlayerData data = create();
        CompoundTag playersTag = tag.getCompound("players");

        for (String uuidStr : playersTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidStr);
            PlayerStats stats = PlayerStats.fromNBT(playersTag.getCompound(uuidStr));
            data.playerStats.put(uuid, stats);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        CompoundTag playersTag = new CompoundTag();
        for (Map.Entry<UUID, PlayerStats> entry : playerStats.entrySet()) {
            playersTag.put(entry.getKey().toString(), entry.getValue().toNBT());
        }
        tag.put("players", playersTag);
        return tag;
    }

    public PlayerStats getStats(UUID playerUUID) {
        return playerStats.computeIfAbsent(playerUUID, uuid -> new PlayerStats(playerUUID));
    }

    public void markDirty() {
        setDirty();
    }
}