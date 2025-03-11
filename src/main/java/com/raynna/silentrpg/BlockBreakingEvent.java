package com.raynna.silentrpg;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

public class BlockBreakingEvent {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Block block = event.getState().getBlock();

        if (block == Blocks.IRON_ORE) {
            event.setCanceled(true);
            if (event.getPlayer() instanceof ServerPlayer player) {
                player.sendSystemMessage(Component.literal("You need a pickaxe to mine this ore."));
            }
        }
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(BlockBreakingEvent.class);
    }
}
