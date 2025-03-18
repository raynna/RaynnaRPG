package net.raynna.silentrpg.client.events;

import net.minecraft.tags.TagKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.common.EventBusSubscriber;
import net.raynna.silentrpg.SilentRPG;
import net.raynna.silentrpg.client.player.ClientSkills;
import net.raynna.silentrpg.data.BlockData;
import net.raynna.silentrpg.data.DataRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.raynna.silentrpg.server.player.skills.SkillType;

@EventBusSubscriber(modid = SilentRPG.MOD_ID, value = Dist.CLIENT)
public class ClientBlockEvents {

    @SubscribeEvent
    public static void onBlockBreak(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.level != null;
        assert mc.player != null;

        if (mc.hitResult != null && mc.hitResult.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            BlockPos blockPos = ((net.minecraft.world.phys.BlockHitResult) mc.hitResult).getBlockPos();
            BlockState blockState = mc.level.getBlockState(blockPos);
            Block block = blockState.getBlock();
            BlockData blockData = DataRegistry.getBlock(blockState.getBlock().getDescriptionId());
            StringBuilder sb = new StringBuilder();
            sb.append("BlockId: ").append(block.getDescriptionId());
            sb.append("\nBlockName: ").append(block.getName().toFlatList().getFirst().toString());
            sb.append("\nTags: ");
            for (TagKey<Block> tagKey : blockState.getTags().toList()) {
                String tagName = tagKey.location().toString();
                sb.append(tagName).append(", ");
            }
            System.out.println(sb.toString());
            sb.setLength(0);
            if (blockData == null) {
                sb.append("Block Data is null, trying to grab by tags");
                for (TagKey<Block> tagKey : blockState.getTags().toList()) {
                    String tagName = tagKey.location().toString();
                    blockData = DataRegistry.getBlock(tagName);

                    if (blockData != null) {
                        sb.append("\nFound block with tag: ").append(tagName);
                        break;
                    }
                }
            }
            System.out.println(sb.toString());
            System.out.println("isAttack?" + event.isAttack());
            if (!event.isAttack() || blockData == null) {
                System.out.println("Isnt attack or block data is null");
                return;
            }
            ClientSkills skills = new ClientSkills(mc.player);
            int miningLevel = skills.getSkillLevel(SkillType.MINING);
            int levelReq = blockData.getLevelRequirement();
            System.out.println("Mining level: " + miningLevel + ", Required level: " + levelReq);
            if (miningLevel >= levelReq) {
                System.out.println("Mining is higher or same as required level");
                return;
            }
            System.out.println("Mining level is lower than required level");
            System.out.println(sb.toString());
            event.setCanceled(true);
            mc.player.swinging = false;
            mc.player.resetAttackStrengthTicker();
            String blockName = blockState.getBlock().getName().toFlatList().getFirst().getString();
            mc.player.displayClientMessage(Component.literal("You need level " + blockData.getLevelRequirement() + " in mining to mine " + blockName), true);
        }
    }


    public static void register() {
        NeoForge.EVENT_BUS.register(ClientBlockEvents.class);
    }
}
