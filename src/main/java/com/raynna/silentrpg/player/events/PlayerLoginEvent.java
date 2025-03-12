package com.raynna.silentrpg.player.events;

import com.raynna.silentrpg.player.PlayerDataProvider;
import com.raynna.silentrpg.player.PlayerProgress;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerLoginEvent {


    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (player instanceof ServerPlayer serverPlayer) {
            PlayerProgress playerProgress = PlayerDataProvider.getPlayerProgress(serverPlayer);
            playerProgress.init(serverPlayer);
        }
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(PlayerLoginEvent.class);
    }
}
