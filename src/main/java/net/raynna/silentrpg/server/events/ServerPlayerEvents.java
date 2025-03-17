package net.raynna.silentrpg.server.events;

import net.raynna.silentrpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.silentrpg.server.player.PlayerProgress;
import net.raynna.silentrpg.server.player.playerdata.PlayerDataStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.UUID;

public class ServerPlayerEvents {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (player instanceof ServerPlayer serverPlayer) {
            PlayerProgress playerProgress = PlayerDataProvider.getPlayerProgress(serverPlayer);
            playerProgress.init(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            UUID playerUUID = serverPlayer.getUUID();
            ServerLevel level = serverPlayer.serverLevel();
            PlayerDataStorage dataStorage = PlayerDataProvider.getData(level);
            dataStorage.markDirty();
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos blockPos = event.getPos();
        BlockState state = level.getBlockState(blockPos);
        Block block = state.getBlock();
        if (event.getEntity() instanceof ServerPlayer player) {
            if (block.equals(Blocks.DIRT)) {//TODO grab restrictions from database
                event.setCanceled(true);
                player.sendSystemMessage(Component.literal("You can't destroy block " + block.getName().toFlatList().getFirst()));
            }
        }
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(ServerPlayerEvents.class);
    }
}