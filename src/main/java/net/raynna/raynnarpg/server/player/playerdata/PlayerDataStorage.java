package net.raynna.raynnarpg.server.player.playerdata;

import net.minecraft.server.level.ServerPlayer;
import net.raynna.raynnarpg.server.player.PlayerProgress;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
            try {
                UUID uuid = UUID.fromString(uuidStr);
                CompoundTag playerTag = playersTag.getCompound(uuidStr);

                if (!playerTag.contains("playerUUID") || uuid == null || playerTag.getString("playerUUID").isEmpty()) {
                    System.err.println("Invalid or missing playerUUID for UUID: " + uuidStr + ". Skipping...");
                    continue;
                }
                System.out.println("Loading playerUUID: " + uuidStr);
                PlayerProgress progress = PlayerProgress.fromNBT(playerTag);
                data.playerProgress.put(uuid, progress);

            } catch (Exception e) {
                System.err.println("Failed to load data for player UUID: " + uuidStr + ". Skipping entry.");
                e.printStackTrace();
            }
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


    public PlayerProgress getStats(UUID playerUUID, ServerLevel serverLevel) {
        return playerProgress.computeIfAbsent(playerUUID, uuid -> {
            PlayerDataStorage storage = PlayerDataProvider.getData(serverLevel);

            CompoundTag tag = storage.getPlayerDataTag(playerUUID);
            if (!tag.contains("playerUUID") || tag.getString("playerUUID").isEmpty()) {
                tag.putString("playerUUID", playerUUID.toString());
                System.out.println("Added missing playerUUID to CompoundTag for UUID: " + playerUUID);
            }

            PlayerProgress progress = PlayerProgress.fromNBT(tag);

            markDirty();
            return progress;
        });
    }

    public static void savePlayer(ServerPlayer player) {
        PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
        progress.toNBT();
        PlayerDataProvider.getData(player.serverLevel()).markDirty();
        if (progress.getSkills() != null) {
            System.out.println("["+player.getName().getString()+"] Saved Skills: " + progress.getSkills().toNBT().toString());
        }
        if (progress.getProgress() != null) {
            System.out.println("["+player.getName().getString()+"] Saved Progress: " + progress.getProgress().toNBT().toString());
        }
    }


    public CompoundTag getPlayerDataTag(UUID playerUUID) {
        PlayerProgress progress = playerProgress.get(playerUUID);

        if (progress != null) {
            return progress.toNBT();
        } else {
            return new CompoundTag();
        }
    }

    public void markDirty() {
        setDirty();
    }
}