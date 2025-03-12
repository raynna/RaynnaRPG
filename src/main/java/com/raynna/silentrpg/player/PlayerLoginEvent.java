package com.raynna.silentrpg.player;

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
            PlayerStats playerStats = PlayerDataManager.getPlayerStats(serverPlayer);
            playerStats.init(serverPlayer);
        }
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(PlayerLoginEvent.class);
    }
}
