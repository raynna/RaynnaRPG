package net.raynna.raynnarpg.server.events;

import net.raynna.raynnarpg.Config;
import net.raynna.raynnarpg.network.packets.message.MessagePacketSender;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.raynnarpg.server.player.PlayerProgress;
import net.raynna.raynnarpg.server.player.skills.Skill;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.minecraft.server.level.ServerPlayer;
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
        if (event.getPlayer() instanceof ServerPlayer player) {
            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
            Skill mining = progress.getSkills().getSkill(SkillType.MINING);
            Config.ConfigData data = Config.getMiningData(blockState);
            if (data != null) {
                int miningLevel = progress.getSkills().getSkill(SkillType.MINING).getLevel();
                int levelReq = data.getLevel();
                if (miningLevel < levelReq) {
                    event.setCanceled(true);
                    MessagePacketSender.send(player, "You need a mining level of " + levelReq + " in order to mine " + block.getName().toFlatList().getFirst().getString() + ".");
                    return;
                }
                progress.getSkills().addXp(mining.getType(), data.getXp());
                MessagePacketSender.send(player, "You gained " + data.getXp() + " mining experience.");
                //MessageSender.send(player, "Mining Xp: " + mining.getXp() + ", Xp until level: " + progress.getSkills().getXpToLevelUp(mining.getType()));
            }
        }
    }


    public static void register() {
        NeoForge.EVENT_BUS.register(ServerBlockEvents.class);
    }
}
