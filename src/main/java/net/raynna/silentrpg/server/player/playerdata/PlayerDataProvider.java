package net.raynna.silentrpg.server.player.playerdata;

import net.raynna.silentrpg.server.player.PlayerProgress;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class PlayerDataProvider {

    public static PlayerDataStorage getData(ServerLevel world) {
        DimensionDataStorage storage = world.getDataStorage();
        return storage.computeIfAbsent(
                new SavedData.Factory<>(PlayerDataStorage::create, PlayerDataStorage::load),
                "player_data"
        );
    }

    public static PlayerProgress getPlayerProgress(ServerPlayer player) {
        return getData(player.server.overworld()).getStats(player.getUUID(), player.serverLevel());
    }

}
