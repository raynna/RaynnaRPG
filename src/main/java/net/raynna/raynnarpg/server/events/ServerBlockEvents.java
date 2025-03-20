package net.raynna.raynnarpg.server.events;

import net.raynna.raynnarpg.data.BlockData;
import net.raynna.raynnarpg.data.DataRegistry;
import net.raynna.raynnarpg.network.packets.message.MessagePacketSender;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.raynnarpg.server.player.PlayerProgress;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
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
            BlockData blockData = DataRegistry.getDataFromBlock(blockState);
            if (blockData != null) {
                int miningLevel = progress.getSkills().getSkill(SkillType.MINING).getLevel();
                int levelReq = blockData.getLevelRequirement();
                if (miningLevel < levelReq) {
                    event.setCanceled(true);
                    MessagePacketSender.send(serverPlayer, "You need a mining level of " + levelReq + " in order to mine " + block.getName().toFlatList().getFirst().getString() + ".");
                    return;
                }
                progress.getSkills().addXp(SkillType.MINING, blockData.getExperience());
            }
        }
    }


    public static void register() {
        NeoForge.EVENT_BUS.register(ServerBlockEvents.class);
    }
}
