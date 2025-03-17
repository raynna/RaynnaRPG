package net.raynna.silentrpg.server.events;

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
            PlayerProgress progress = PlayerDataProvider.getPlayerProgress(serverPlayer);
            if (block == Blocks.IRON_ORE) {//TODO grab restrictions from database
                boolean unlockedCrimsonIron = progress.getProgress().isActive(ProgressKey.UNLOCKED_CRIMSON_IRON);
                if (!unlockedCrimsonIron) {
                    event.setCanceled(true);
                    serverPlayer.sendSystemMessage(Component.literal("You need a pickaxe to mine this ore."));
                }
            }
            if (block == Blocks.SAND) {
                progress.getSkills().resetSkills();
                //progress.getProgress().resetProgress();
                progress.getProgress().toggle(ProgressKey.UNLOCKED_CRIMSON_IRON);
                boolean unlockedCrimsonIron = progress.getProgress().isActive(ProgressKey.UNLOCKED_CRIMSON_IRON);
                serverPlayer.sendSystemMessage(Component.literal("Crimson iron is now " + (unlockedCrimsonIron ? "unlocked" : "locked")));
                serverPlayer.sendSystemMessage(Component.literal("All players progress has been reset."));
            }

            if (block == Blocks.STONE) {
                int xpToAdd = progress.getSkills().getSkill(SkillType.MINING).getXp() < 283850 ? 283850 : 1;
                if (xpToAdd > 500) {
                    progress.getSkills().getSkill(SkillType.MINING).setXp(0);
                    progress.getSkills().addXp(SkillType.MINING, xpToAdd);
                } else {
                    progress.getSkills().addXp(SkillType.MINING, xpToAdd);
                }
                progress.getProgress().increase(ProgressKey.MINED_STONE, 1);
                int stoneMined = progress.getProgress().get(ProgressKey.MINED_STONE);
                serverPlayer.sendSystemMessage(Component.literal("You received " + xpToAdd + " mining experience, you now have in total of " + progress.getSkills().getSkill(SkillType.MINING).getXp() + " experience."));
                serverPlayer.sendSystemMessage(Component.literal("You have mined in total of " + stoneMined + " stone blocks."));
            }
        }
    }


    public static void register() {
        NeoForge.EVENT_BUS.register(ServerBlockEvents.class);
    }
}
