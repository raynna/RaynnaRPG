package net.raynna.raynnarpg.server.events;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.CommonHooks;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.config.mining.MiningConfig;
import net.raynna.raynnarpg.network.packets.message.MessagePacketSender;
import net.raynna.raynnarpg.network.packets.xpdrop.FloatingTextSender;
import net.raynna.raynnarpg.server.player.playerdata.PlayerDataProvider;
import net.raynna.raynnarpg.server.player.PlayerProgress;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.raynna.raynnarpg.utils.Colour;
import net.raynna.raynnarpg.utils.Utils;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TraitHelper;


public class ServerBlockEvents {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        PlayerProgress progress = PlayerDataProvider.getPlayerProgress(player);
        if (progress == null) return;

        BlockState blockState = event.getState();
        ConfigData miningData = MiningConfig.getMiningData(blockState);
        if (miningData == null) return;

        handleMiningEvent(player, progress, event, blockState, miningData);
    }

    private static void handleMiningEvent(ServerPlayer player, PlayerProgress progress,
                                          BlockEvent.BreakEvent event, BlockState blockState,
                                          ConfigData miningData) {
        if (!checkMiningLevel(player, progress, event, blockState, miningData)) {
            return;
        }
        if (Utils.hasSilkTouch(player.getMainHandItem())) {
            MessagePacketSender.send(player, "No XP gained because you used Silk Touch.");
            return;
        }
        grantMiningExperience(player, progress, miningData, event.getPos());
    }

    private static boolean checkMiningLevel(ServerPlayer player, PlayerProgress progress,
                                            BlockEvent.BreakEvent event, BlockState blockState,
                                            ConfigData miningData) {
        int miningLevel = progress.getSkills().getSkill(SkillType.MINING).getLevel();
        int requiredLevel = miningData.getLevel();

        if (miningLevel < requiredLevel) {
            event.setCanceled(true);
            String blockName = blockState.getBlock().getName().toFlatList().getFirst().getString();
            MessagePacketSender.send(player,
                    "You need a mining level of " + requiredLevel +
                            " in order to mine " + blockName + ".");
            return false;
        }
        return true;
    }

    private static boolean hasSilkTouch(ItemStack tool) {
        ItemEnchantments enchantments = tool.get(DataComponents.ENCHANTMENTS);
        if (enchantments != null) {
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                if (entry.getKey().is(Enchantments.SILK_TOUCH)) {
                    return true;
                }
            }
        }
        return TraitHelper.hasTrait(tool, Const.Traits.SILKY);
    }

    private static void grantMiningExperience(ServerPlayer player, PlayerProgress progress,
                                              ConfigData miningData, BlockPos pos) {
        double xp = miningData.getXp();
        int miningLevel = progress.getSkills().getSkill(SkillType.MINING).getLevel();
        int levelReq = miningData.getLevel();
        if (Utils.isXpCapped(miningLevel, levelReq)) {
            FloatingTextSender.sendOnBlock(player, Colour.RED + "Xp Capped", pos, SkillType.MINING);
            return;
        }
        progress.getSkills().addXp(SkillType.MINING, xp);
        FloatingTextSender.sendOnBlock(player, "+" + xp + "xp", pos, SkillType.MINING);
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(ServerBlockEvents.class);
    }
}

