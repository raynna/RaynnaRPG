package com.raynna.silentrpg.player;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataStorage extends SavedData {
    private final Map<UUID, PlayerProgress> playerProgress = new HashMap<>();

    public static PlayerDataStorage create() {
        return new PlayerDataStorage();
    }

    public static PlayerDataStorage load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        PlayerDataStorage data = create();
        CompoundTag playersTag = tag.getCompound("players");

        for (String uuidStr : playersTag.getAllKeys()) {
            UUID uuid = UUID.fromString(uuidStr);
            PlayerProgress progress = PlayerProgress.fromNBT(playersTag.getCompound(uuidStr));
            data.playerProgress.put(uuid, progress);
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        CompoundTag playersTag = new CompoundTag();
        for (Map.Entry<UUID, PlayerProgress> entry : playerProgress.entrySet()) {
            playersTag.put(entry.getKey().toString(), entry.getValue().toNBT());
        }
        tag.put("players", playersTag);
        return tag;
    }

    public PlayerProgress getStats(UUID playerUUID) {
        return playerProgress.computeIfAbsent(playerUUID, uuid -> new PlayerProgress(playerUUID));
    }

    public void markDirty() {
        setDirty();
    }
}