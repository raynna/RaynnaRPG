package net.raynna.raynnarpg.client.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.player.ClientSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.raynna.raynnarpg.compat.silentgear.SilentGearCompat;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.config.combat.CombatConfig;
import net.raynna.raynnarpg.config.mining.MiningConfig;
import net.raynna.raynnarpg.config.tools.ToolConfig;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.utils.ItemUtils;
import net.raynna.raynnarpg.utils.Utils;

@EventBusSubscriber(modid = RaynnaRPG.MOD_ID, value = Dist.CLIENT)
public class ClientBlockEvents {

    @SubscribeEvent
    public static void onClickEvent(InputEvent.InteractionKeyMappingTriggered event) {
        if (!shouldProcessBreakEvent(event)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        BlockHitResult hitResult = (BlockHitResult) mc.hitResult;
        if (mc.player == null || mc.level == null || hitResult == null) return;

        BlockPos blockPos = hitResult.getBlockPos();
        BlockState blockState = mc.level.getBlockState(blockPos);
        ClientSkills skills = new ClientSkills(mc.player);
        ItemStack mainHand = mc.player.getMainHandItem();
        int miningLevel = skills.getSkillLevel(SkillType.MINING);
        int combatLevel = skills.getSkillLevel(SkillType.COMBAT);

        if (checkToolRequirements(mc, event, mainHand, miningLevel, blockPos)) {
            return;
        }
        if (!canUseWeapon(mc.player, mainHand, combatLevel)) {
            event.setCanceled(true);
            mc.player.swinging = false;
            mc.player.resetAttackStrengthTicker();
            return;
        }
        checkBlockMiningRequirements(mc, event, blockState, miningLevel, blockPos);
    }

    private static boolean shouldProcessBreakEvent(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        return event.isAttack()
                && mc.hitResult != null
                && mc.hitResult.getType() == HitResult.Type.BLOCK
                && mc.level != null
                && mc.player != null;
    }

    private static boolean canUseWeapon(Player player, ItemStack weapon, int combatLevel) {
        if (weapon.isEmpty()) return true;

        if (SilentGearCompat.IS_LOADED && SilentGearCompat.isGearItem(weapon) && ItemUtils.isWeapon(weapon)) {
            if (!ItemUtils.checkCombatLevel(player, weapon, combatLevel, false)) {
                return false;
            }
        }
        ConfigData data = CombatConfig.getData(weapon, false);
        if (data != null && combatLevel < data.getLevel()) {
            player.displayClientMessage(
                    Component.literal("You need a combat level of " + data.getLevel() +
                            " to use " + weapon.getHoverName().getString()),
                    true
            );
            return false;
        }

        return true;
    }


    private static boolean checkToolRequirements(Minecraft mc, InputEvent.InteractionKeyMappingTriggered event,
                                                 ItemStack mainHand, int miningLevel, BlockPos blockPos) {
        if (mainHand.isEmpty()) {
            return false;
        }

        if (SilentGearCompat.IS_LOADED && SilentGearCompat.isGearItem(mainHand) && ItemUtils.isPickaxe(mainHand)) {
            if (!ItemUtils.checkMiningLevel(mc.player, mainHand, miningLevel)) {
                handleFailedRequirement(mc, event, blockPos, null);
                return true;
            }
        }

        ConfigData toolData = ToolConfig.getToolData(mainHand);
        if (toolData != null && miningLevel < toolData.getLevel()) {
            handleFailedRequirement(mc, event, blockPos,
                    "You need a mining level of " + toolData.getLevel() +
                            " in order to use " + mainHand.getHoverName().getString() + " as a tool.");
            return true;
        }

        return false;
    }

    private static void checkBlockMiningRequirements(Minecraft mc, InputEvent.InteractionKeyMappingTriggered event,
                                                     BlockState blockState, int miningLevel, BlockPos blockPos) {
        ConfigData miningData = MiningConfig.getMiningData(blockState);
        if (miningData != null && miningLevel < miningData.getLevel()) {
            String blockName = blockState.getBlock().getName().toFlatList().getFirst().getString();
            handleFailedRequirement(mc, event, blockPos,
                    "You need a mining level of " + miningData.getLevel() +
                            " in order to mine " + blockName + ".");
        }
    }

    private static void handleFailedRequirement(Minecraft mc, InputEvent.InteractionKeyMappingTriggered event,
                                                BlockPos blockPos, String message) {
        event.setCanceled(true);
        mc.player.swinging = false;
        mc.player.resetAttackStrengthTicker();
        Utils.checkMiningMiss(mc.player, blockPos, 0.5f);
        if (message != null) {
            mc.player.displayClientMessage(Component.literal(message), true);
        }
    }


    public static void register() {
        NeoForge.EVENT_BUS.register(ClientBlockEvents.class);
    }
}

