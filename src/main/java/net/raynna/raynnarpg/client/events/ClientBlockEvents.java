package net.raynna.raynnarpg.client.events;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.player.ClientSkills;
import net.raynna.raynnarpg.data.BlockData;
import net.raynna.raynnarpg.data.DataRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.raynna.raynnarpg.data.ToolData;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.util.GearData;

import java.util.*;

@EventBusSubscriber(modid = RaynnaRPG.MOD_ID, value = Dist.CLIENT)
public class ClientBlockEvents {

    @SubscribeEvent
    public static void onBlockBreak(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.level != null;
        assert mc.player != null;

        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            if (!event.isAttack())
                return;
            BlockPos blockPos = ((BlockHitResult) mc.hitResult).getBlockPos();
            BlockState blockState = mc.level.getBlockState(blockPos);
            ClientSkills skills = new ClientSkills(mc.player);
            ItemStack mainHand = mc.player.getMainHandItem();
            int miningLevel = skills.getSkillLevel(SkillType.MINING);
            if (ModList.get().isLoaded("silentgear")) {
                if (mainHand.getItem() instanceof GearItem silent) {
                    String toolName = silent.asItem().getName(mainHand).getString();
                    Map<String, String> properties = new HashMap<>();
                    GearPropertiesData propertiesData = GearData.getProperties(mainHand);

                    propertiesData.properties().forEach((key, value) -> {
                        properties.put(key.getDisplayName().getString(), value.toString());
                    });

                    String harvestTierByName = properties.get("Harvest Tier");

                    ToolData toolData = DataRegistry.getTool(harvestTierByName);
                    if (toolData != null) {
                        if (miningLevel < toolData.getLevelRequirement()) {
                            event.setCanceled(true);
                            mc.player.swinging = false;
                            mc.player.resetAttackStrengthTicker();
                            mc.player.displayClientMessage(Component.literal("You need a mining level of " + toolData.getLevelRequirement() + " in order to use " + toolName + " as a tool."), true);
                            return;
                        }
                    }
                }
            }

            ToolData toolData = DataRegistry.getToolByTag(mainHand.getDescriptionId());
            if (toolData != null) {
                if (miningLevel < toolData.getLevelRequirement()) {
                    event.setCanceled(true);
                    mc.player.swinging = false;
                    mc.player.resetAttackStrengthTicker();
                    mc.player.displayClientMessage(Component.literal("You need a mining level of " + toolData.getLevelRequirement() + " in order to use " + mc.player.getMainHandItem().getHoverName().getString() + " as a tool."), true);
                    return;
                }
            }

            BlockData blockData = DataRegistry.getDataFromBlock(blockState);
            if (blockData != null) {
                int levelReq = blockData.getLevelRequirement();
                if (miningLevel < levelReq) {
                    event.setCanceled(true);
                    mc.player.swinging = false;
                    mc.player.resetAttackStrengthTicker();
                    String blockName = blockState.getBlock().getName().toFlatList().getFirst().getString();
                    mc.player.displayClientMessage(Component.literal("You need a mining level of " + blockData.getLevelRequirement() + " in order to mine " + blockName + "."), true);
                }
            }
        }
    }


    public static void register() {
        NeoForge.EVENT_BUS.register(ClientBlockEvents.class);
    }
}

