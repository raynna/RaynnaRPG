package net.raynna.raynnarpg.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.raynna.raynnarpg.Config;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.player.ClientSkills;
import net.raynna.raynnarpg.data.*;
import net.raynna.raynnarpg.network.packets.message.MessagePacketSender;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.util.GearData;
import org.apache.logging.log4j.core.jmx.Server;
import org.lwjgl.glfw.GLFW;

import javax.xml.crypto.Data;
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
        boolean isSinglePlayer = mc.isSingleplayer();
        ItemStack stack = event.getItemStack();
        String descriptionId = stack.getItem().getDescriptionId();
        Map<String, String> tooltip = new HashMap<>();
        List<Component> toolTips = event.getToolTip();
        AtomicInteger insertIndex = new AtomicInteger(1);
        ClientSkills skills = new ClientSkills(mc.player);
        Config.ConfigData data = Config.getMiningData(stack);
        int levelReq;
        double xp;
        if (data != null) {
            int miningLevel = skills.getSkillLevel(SkillType.MINING);
            levelReq = data.getLevel();
            xp = data.getXp();
            StringBuilder miningText = new StringBuilder();

            if (miningLevel < levelReq) {
                miningText.append("§cLevel: ")
                        .append(levelReq)
                        .append(" (Current level: ")
                        .append(miningLevel).append("), Xp: ").append(xp);
            } else {
                miningText.append("§aLevel: ")
                        .append(levelReq)
                        .append(", Xp: ")
                        .append(xp);
            }
            tooltip.put("Mining", miningText.toString());
        }
       data = Config.getCraftingData(stack);
        if (data != null) {
            int craftingLevel = skills.getSkillLevel(SkillType.CRAFTING);
            levelReq = data.getLevel();
            xp = data.getXp();
            StringBuilder craftingText = new StringBuilder();
            if (craftingLevel < levelReq) {
                craftingText.append("§cLevel: ")
                        .append(levelReq)
                        .append(" (Current level: ")
                        .append(craftingLevel).append("), Xp: ").append(xp);
            } else {
                craftingText.append("§aLevel: ")
                        .append(levelReq).append(", Xp: ").append(xp);
            }
            tooltip.put("Crafting", craftingText.toString());
        }

        data = Config.getSmeltingData(stack);
        if (data != null) {
            int smeltingLevel = skills.getSkillLevel(SkillType.SMELTING);
            levelReq = data.getLevel();
            xp = data.getXp();
            StringBuilder smeltingText = new StringBuilder();
            if (smeltingLevel < levelReq) {
                smeltingText.append("§cLevel: ")
                        .append(levelReq)
                        .append(" (Current level: ")
                        .append(smeltingLevel).append("), Xp: ").append(xp);
            } else {
                smeltingText.append("§aLevel: ")
                        .append(levelReq).append(", Xp: ").append(xp);
            }
            tooltip.put("Smelting", smeltingText.toString());
        }
        if (ModList.get().isLoaded("silentgear")) {
            if (stack.getItem() instanceof GearItem silent) {
                Map<String, String> properties = new HashMap<>();
                GearPropertiesData propertiesData = GearData.getProperties(stack);
                propertiesData.properties().forEach((key, value) -> {
                    properties.put(key.getDisplayName().getString(), value.toString());
                });
                String harvestTierByName = properties.get("Harvest Tier");
                data = Config.getSilentGearData(harvestTierByName);
                if (data != null) {
                    levelReq = data.getLevel();
                    StringBuilder toolText = new StringBuilder();

                    int playerLevel = skills.getSkillLevel(SkillType.MINING);
                    if (playerLevel < levelReq) {
                        toolText.append("§cLevel: ")
                                .append(levelReq)
                                .append(" (Current level: ")
                                .append(playerLevel).append(")");
                    } else {
                        toolText.append("§aLevel: ")
                                .append(levelReq);
                    }

                    tooltip.put("Mining", toolText.toString());
                }
            }
        }
        data = Config.getToolData(stack);
        if (data != null) {
            StringBuilder toolText = new StringBuilder();
            levelReq = data.getLevel();
            int playerLevel = skills.getSkillLevel(SkillType.MINING);
            if (playerLevel < levelReq) {
                toolText.append("§cLevel: ")
                        .append(levelReq)
                        .append(" (Current level: ")
                        .append(playerLevel).append(")");
            } else {
                toolText.append("§aLevel: ")
                        .append(levelReq);
            }

            tooltip.put("Mining", toolText.toString());
        }

        if (!tooltip.isEmpty()) {
            toolTips.add(insertIndex.getAndIncrement(), Component.literal("§6RaynnaRPG: "));
            tooltip.forEach((key, value) ->
                    toolTips.add(insertIndex.getAndIncrement(), Component.literal("§7" + key + " " + value))
            );
        }

        if (isPlayerCreative) {
            event.getToolTip().add(Component.literal("§6Description: §7" + descriptionId));
            event.getToolTip().add(Component.literal("§6Tags: "));
            for (TagKey<Item> tag : stack.getTags().toList()) {
                event.getToolTip().add(Component.literal("§7- " + tag.location().toString()));
            }
            return;
        }
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.hasPermissions(2)) {
                boolean shift = GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
                if (!shift) {
                    event.getToolTip().add(Component.literal("Hold SHIFT to reveal item tags."));
                    return;
                }
                event.getToolTip().add(Component.literal("§6Description: §7" + descriptionId));
                event.getToolTip().add(Component.literal("§6Tags: "));
                for (TagKey<Item> tag : stack.getTags().toList()) {
                    event.getToolTip().add(Component.literal("§7- " + tag.location().toString()));
                }
            }
        } else {
            boolean shift = GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
            if (!shift) {
                event.getToolTip().add(Component.literal("Hold SHIFT to reveal item tags."));
                return;
            }
            event.getToolTip().add(Component.literal("§6Description: §7" + descriptionId));
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
