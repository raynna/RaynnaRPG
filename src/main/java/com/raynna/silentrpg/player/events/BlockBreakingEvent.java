package com.raynna.silentrpg.player.events;

import com.raynna.silentrpg.player.playerdata.PlayerDataProvider;
import com.raynna.silentrpg.player.PlayerProgress;
import com.raynna.silentrpg.player.progress.ProgressKey;
import com.raynna.silentrpg.player.skills.SkillType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public class BlockBreakingEvent {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState blockState = event.getState();
        Block block = blockState.getBlock();
        Player player = event.getPlayer();

        if (player instanceof ServerPlayer serverPlayer) {
            /*serverPlayer.sendSystemMessage(Component.literal("§6[Debug] Block Broken: " + blockState));
            serverPlayer.sendSystemMessage(Component.literal(" - Block Name: " + block.getDescriptionId()));
            serverPlayer.sendSystemMessage(Component.literal(" - Hardness: " + blockState.getDestroySpeed(event.getLevel(), event.getPos())));
            serverPlayer.sendSystemMessage(Component.literal(" - Resistance: " + block.getExplosionResistance()));

            List<TagKey<Block>> blockTags = blockState.getTags().toList();
            if (!blockTags.isEmpty()) {
                serverPlayer.sendSystemMessage(Component.literal(" - Tags: " + blockTags));
            } else {
                serverPlayer.sendSystemMessage(Component.literal(" - No tags found."));
            }

            if (blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                serverPlayer.sendSystemMessage(Component.literal(" - This block requires a pickaxe."));
            }
            if (blockState.is(BlockTags.MINEABLE_WITH_AXE)) {
                serverPlayer.sendSystemMessage(Component.literal(" - This block requires an axe."));
            }
            if (blockState.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                serverPlayer.sendSystemMessage(Component.literal(" - This block requires a shovel."));
            }*/

            if (block == Blocks.IRON_ORE) {
                event.setCanceled(true);
                serverPlayer.sendSystemMessage(Component.literal("You need a pickaxe to mine this ore."));
            }
            if (block == Blocks.SAND) {
                PlayerProgress progress = PlayerDataProvider.getPlayerProgress(serverPlayer);
                progress.getSkills().resetSkills();
                progress.getProgress().resetProgress();
                serverPlayer.sendSystemMessage(Component.literal("All players progress has been reset."));
            }

            if (block == Blocks.STONE) {
                PlayerProgress progress = PlayerDataProvider.getPlayerProgress(serverPlayer);
                int xpToAdd = progress.getSkills().getSkill(SkillType.MINING).getXp() < 283850 ? 283850 : 1;
                if (xpToAdd > 500) {
                    progress.getSkills().getSkill(SkillType.MINING).setXp(0);
                    progress.getSkills().addXp(SkillType.MINING, xpToAdd);
                } else {
                    progress.getSkills().addXp(SkillType.MINING, xpToAdd);
                }
                int stoneMined = progress.getProgress().get(ProgressKey.MINED_STONE);
                progress.getProgress().set(ProgressKey.MINED_STONE, stoneMined + 1);
                serverPlayer.sendSystemMessage(Component.literal("You received " + xpToAdd + " mining experience, you now have in total of " + progress.getSkills().getSkill(SkillType.MINING).getXp() + " experience."));
                serverPlayer.sendSystemMessage(Component.literal("You have mined in total of " + progress.getProgress().get(ProgressKey.MINED_STONE) + " stone blocks."));
            }
        }
    }


    public static void register() {
        NeoForge.EVENT_BUS.register(BlockBreakingEvent.class);
    }
}
