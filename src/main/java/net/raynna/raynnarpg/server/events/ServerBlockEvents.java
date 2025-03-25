package net.raynna.raynnarpg.server.events;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.CommonHooks;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.config.mining.MiningConfig;
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
            ConfigData data = MiningConfig.getMiningData(blockState);
            if (data != null) {
                int miningLevel = progress.getSkills().getSkill(SkillType.MINING).getLevel();
                int levelReq = data.getLevel();
                if (miningLevel < levelReq) {
                    event.setCanceled(true);
                    MessagePacketSender.send(player, "You need a mining level of " + levelReq + " in order to mine " + block.getName().toFlatList().getFirst().getString() + ".");
                    return;
                }

                ItemStack tool = player.getMainHandItem();

                ItemEnchantments itemEnchantments = (ItemEnchantments) tool.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                HolderLookup.RegistryLookup<Enchantment> lookup = CommonHooks.resolveLookup(Registries.ENCHANTMENT);

                if (lookup != null) {
                    itemEnchantments = tool.getAllEnchantments(lookup);
                }

                for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
                    if (entry.getKey().is(Enchantments.SILK_TOUCH)) {
                        MessagePacketSender.send(player, "No XP gained because you used Silk Touch.");
                        return;
                    }
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
