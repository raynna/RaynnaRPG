package net.raynna.raynnarpg.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.player.ClientSkills;
import net.raynna.raynnarpg.data.*;
import net.raynna.raynnarpg.server.player.skills.SkillType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = RaynnaRPG.MOD_ID, value = Dist.CLIENT)
public class ClientItemEvents {

    @SubscribeEvent
    public static void onItemHover(ItemTooltipEvent event) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.player != null;
        boolean isPlayerCreative = mc.player.isCreative();
        ItemStack stack = event.getItemStack();
        String itemId = stack.getItem().getDescriptionId();
        Map<String, String> data = new HashMap<>();
        List<Component> toolTips = event.getToolTip();
        AtomicInteger insertIndex = new AtomicInteger(1);
        ClientSkills skills = new ClientSkills(mc.player);
        BlockData blockData = DataRegistry.getDataFromItem(stack, BlockData.class);
        if (blockData != null) {
            int miningLevel = skills.getSkillLevel(SkillType.MINING);
            StringBuilder miningText = new StringBuilder();

            if (miningLevel < blockData.getLevelRequirement()) {
                miningText.append("§cLevel: ")
                        .append(blockData.getLevelRequirement())
                        .append(" (Your level: ")
                        .append(miningLevel).append("), XP: ").append(blockData.getExperience());
            } else {
                miningText.append("§aLevel: ")
                        .append(blockData.getLevelRequirement())
                        .append(", XP: ")
                        .append(blockData.getExperience());
            }

            data.put("Mining", miningText.toString());
        }

        CraftingData craftingData = DataRegistry.getDataFromItem(stack, CraftingData.class);
        if (craftingData != null) {
            int craftingLevel = skills.getSkillLevel(SkillType.CRAFTING);
            StringBuilder craftingText = new StringBuilder();
            if (craftingLevel < craftingData.getLevelRequirement()) {
                craftingText.append("§cLevel: ")
                        .append(craftingData.getLevelRequirement())
                        .append(" (Your level: ")
                        .append(craftingLevel).append("), XP: ").append(craftingData.getExperience());
            } else {
                craftingText.append("§aLevel: ")
                        .append(craftingData.getLevelRequirement()).append(", XP: ").append(craftingData.getExperience());
            }
            data.put("Crafting",craftingText.toString());
        }

        SmeltingData smeltingData = DataRegistry.getDataFromItem(stack, SmeltingData.class);
        if (smeltingData != null) {
            int smeltingLevel = skills.getSkillLevel(SkillType.SMELTING);
            StringBuilder smeltingText = new StringBuilder();
            if (smeltingLevel < smeltingData.getLevelRequirement()) {
                smeltingText.append("§cLevel: ")
                        .append(smeltingData.getLevelRequirement())
                        .append(" (Your level: ")
                        .append(smeltingLevel).append("), XP: ").append(smeltingData.getExperience());
            } else {
                smeltingText.append("§aLevel: ")
                        .append(smeltingData.getLevelRequirement()).append(", XP: ").append(smeltingData.getExperience());
            }
            data.put("Smelting", smeltingText.toString());
        }

        ToolData toolData = DataRegistry.getDataFromItem(stack, ToolData.class);
        if (toolData != null) {
            String toolType = DataRegistry.determineToolType(stack);
            StringBuilder toolText = new StringBuilder();
            SkillType skillType;
            switch (toolType) {
                case "pickaxe":
                case "shovel":
                    skillType = SkillType.MINING;
                    break;
                default:
                    skillType = null;
                    break;
            }

            int playerLevel = skills.getSkillLevel(skillType);
            if (playerLevel < toolData.getLevelRequirement()) {
                toolText.append("§cLevel: ")
                        .append(toolData.getLevelRequirement())
                        .append(" (Your level: ")
                        .append(playerLevel).append(")");
            } else {
                toolText.append("§aLevel: ")
                        .append(toolData.getLevelRequirement());
            }

            data.put(toolType, toolText.toString());
        }

        // Add tooltips dynamically
        if (!data.isEmpty()) {
            toolTips.add(insertIndex.getAndIncrement(), Component.literal("§6RaynnaRPG: "));
            data.forEach((key, value) ->
                    toolTips.add(insertIndex.getAndIncrement(), Component.literal("§7" + key + " " + value))
            );
        }

        if (isPlayerCreative) {
            event.getToolTip().add(Component.literal("§6Description: §7" + itemId));
            event.getToolTip().add(Component.literal("§6Tags: "));
            for (TagKey<Item> tag : stack.getTags().toList()) {
                event.getToolTip().add(Component.literal("§7- " + tag.location().toString()));
            }
        }
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(ClientItemEvents.class);
    }
}
