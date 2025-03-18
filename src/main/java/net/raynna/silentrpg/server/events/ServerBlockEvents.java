package net.raynna.silentrpg.server.events;

import net.minecraft.tags.TagKey;
import net.raynna.silentrpg.data.BlockData;
import net.raynna.silentrpg.data.DataRegistry;
import net.raynna.silentrpg.network.packets.message.MessagePacketSender;
import net.raynna.silentrpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.silentrpg.server.player.PlayerProgress;
import net.raynna.silentrpg.server.player.progress.ProgressKey;
import net.raynna.silentrpg.server.player.skills.SkillType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public class ServerBlockEvents {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState blockState = event.getState();
        Block block = blockState.getBlock();
        Player player = event.getPlayer();

        if (player instanceof ServerPlayer serverPlayer) {
            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(serverPlayer);
            BlockData blockData = DataRegistry.getBlock(blockState.getBlock().getDescriptionId());
            if (blockData == null) {
                for (TagKey<Block> tagKey : blockState.getTags().toList()) {
                    String tagName = tagKey.location().toString();
                    blockData = DataRegistry.getBlock(tagName);
                    if (blockData != null) {
                        break;
                    }
                }
            }
            if (blockData == null) {
                MessagePacketSender.send(serverPlayer, "Block data is null");
                return;
            }
            progress.getSkills().addXp(SkillType.MINING, blockData.getExperience());
        }
    }


    public static void register() {
        NeoForge.EVENT_BUS.register(ServerBlockEvents.class);
    }
}
