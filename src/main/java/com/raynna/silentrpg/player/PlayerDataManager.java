package com.raynna.silentrpg.player;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class PlayerDataManager {

    public static PlayerData getPlayerData(ServerLevel world) {
        DimensionDataStorage storage = world.getDataStorage();
        return storage.computeIfAbsent(
                new SavedData.Factory<>(PlayerData::create, PlayerData::load),
                "player_data"
        );
    }

    public static PlayerStats getPlayerStats(ServerPlayer player) {
        return getPlayerData(player.server.overworld()).getStats(player.getUUID());
    }

}
