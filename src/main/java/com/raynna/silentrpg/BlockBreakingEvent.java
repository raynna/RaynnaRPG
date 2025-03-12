package com.raynna.silentrpg;

import com.raynna.silentrpg.player.PlayerDataManager;
import com.raynna.silentrpg.player.PlayerStats;
import com.raynna.silentrpg.player.skills.SkillType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

public class BlockBreakingEvent {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState blockState = event.getState();
        Block block = blockState.getBlock();
        Player player = event.getPlayer();

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal("§6[Debug] Block Broken: " + blockState));
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
            }

            if (block == Blocks.IRON_ORE) {
                event.setCanceled(true);
                serverPlayer.sendSystemMessage(Component.literal("You need a pickaxe to mine this ore."));
            }

            if (block == Blocks.STONE) {
                PlayerStats stats = PlayerDataManager.getPlayerStats(serverPlayer);
                stats.getSkills().addXp(SkillType.MINING, 150);
                serverPlayer.sendSystemMessage(Component.literal("You received 150 mining experience."));
                serverPlayer.sendSystemMessage(Component.literal("You now have " + stats.getSkills().getSkill(SkillType.MINING).getXp()));
            }
        }
    }


    public static void register() {
        NeoForge.EVENT_BUS.register(BlockBreakingEvent.class);
    }
}
