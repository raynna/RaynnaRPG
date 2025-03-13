package com.raynna.silentrpg.player.events;

import com.raynna.silentrpg.player.PlayerDataProvider;
import com.raynna.silentrpg.player.PlayerDataStorage;
import com.raynna.silentrpg.player.PlayerProgress;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.UUID;

public class PlayerLogoutEvent {


    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            UUID playerUUID = serverPlayer.getUUID();
            ServerLevel level = serverPlayer.serverLevel();
            PlayerDataStorage dataStorage = PlayerDataProvider.getData(level);

            dataStorage.markDirty();
        }

    }

    public static void register() {
        NeoForge.EVENT_BUS.register(PlayerLogoutEvent.class);
    }
}
