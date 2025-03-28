package net.raynna.raynnarpg.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.player.ClientSkills;
import net.raynna.raynnarpg.config.*;
import net.raynna.raynnarpg.config.combat.CombatConfig;
import net.raynna.raynnarpg.config.crafting.CraftingConfig;
import net.raynna.raynnarpg.config.mining.MiningConfig;
import net.raynna.raynnarpg.config.smelting.SmeltingConfig;
import net.raynna.raynnarpg.config.tools.ToolConfig;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.utils.SilentGearHelper;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.setup.gear.GearProperties;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = RaynnaRPG.MOD_ID, value = Dist.CLIENT)
public class ClientItemEvents {
    private static final String COLOR_REQUIRED = "§c";
    private static final String COLOR_ACHIEVED = "§a";
    private static final String COLOR_HEADER = "§6";
    private static final String COLOR_TEXT = "§7";

    @SubscribeEvent
    public static void onItemHover(ItemTooltipEvent event) {
        if (!shouldProcessTooltip(event)) return;

        TooltipContext context = new TooltipContext(event);
        Map<String, String> requirements = new LinkedHashMap<>();

        checkMiningRequirements(context, requirements);
        checkCraftingRequirements(context, requirements);
        checkSmeltingRequirements(context, requirements);
        checkToolRequirements(context, requirements);
        checkArmourRequirement(context, requirements);
        checkWeaponRequirements(context, requirements);

        if (!requirements.isEmpty()) {
            addRequirementsToTooltip(context, requirements);
        }

        handleDebugTooltips(context);
    }

    private static boolean shouldProcessTooltip(ItemTooltipEvent event) {
        return event.getEntity() == null || event.getEntity().isAlive();
    }

    private static class TooltipContext {
        public final ItemTooltipEvent event;
        public final ItemStack stack;
        public final ClientSkills skills;
        public final boolean isCreative;
        public final boolean isShiftDown;

        public TooltipContext(ItemTooltipEvent event) {
            this.event = event;
            this.stack = event.getItemStack();
            this.skills = new ClientSkills(Minecraft.getInstance().player);
            this.isCreative = Minecraft.getInstance().player.isCreative();
            this.isShiftDown = GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(),
                    GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
        }
    }

    private static void checkMiningRequirements(TooltipContext context, Map<String, String> requirements) {
        ConfigData data = MiningConfig.getMiningData(context.stack);
        if (data != null) {
            int level = context.skills.getSkillLevel(SkillType.MINING);
            requirements.put("Mining", formatRequirement(level, data));
        }
    }

    private static void checkCraftingRequirements(TooltipContext context, Map<String, String> requirements) {
        ConfigData data = CraftingConfig.getCraftingData(context.stack);
        if (data != null) {
            int level = context.skills.getSkillLevel(SkillType.CRAFTING);
            requirements.put("Crafting", formatRequirement(level, data));
        }
    }

    private static void checkSmeltingRequirements(TooltipContext context, Map<String, String> requirements) {
        ConfigData data = SmeltingConfig.getSmeltingData(context.stack);
        if (data != null) {
            int level = context.skills.getSkillLevel(SkillType.SMELTING);
            requirements.put("Smelting", formatRequirement(level, data));
        }
    }

    private static void checkToolRequirements(TooltipContext context, Map<String, String> requirements) {
        if (SilentGearHelper.isSilentGearLoaded() && context.stack.getItem() instanceof GearItem && SilentGearHelper.isPickaxe(context.stack)) {
            String harvestTier = SilentGearHelper.getGearProperty(context.stack, GearProperties.HARVEST_TIER.get());
            ConfigData data = ToolConfig.getSilentGearData(harvestTier);
            if (data != null) {
                int level = context.skills.getSkillLevel(SkillType.MINING);
                requirements.put("Mining", formatToolRequirement(level, data));
            }
        }

        ConfigData toolData = ToolConfig.getToolData(context.stack);
        if (toolData != null) {
            int level = context.skills.getSkillLevel(SkillType.MINING);
            requirements.put("Mining", formatToolRequirement(level, toolData));
        }
    }

    private static void checkArmourRequirement(TooltipContext context, Map<String, String> requirements) {
        if (SilentGearHelper.isSilentGearLoaded() && context.stack.getItem() instanceof GearItem && SilentGearHelper.isArmor(context.stack)) {
            ConfigData data = CombatConfig.getData(context.stack, true);
            if (data != null) {
                int level = context.skills.getSkillLevel(SkillType.COMBAT);
                requirements.put("Combat", formatCombatRequirement(level, data));
            }
        }

        ConfigData armourData = CombatConfig.getData(context.stack, true);
        if (armourData != null) {
            int level = context.skills.getSkillLevel(SkillType.COMBAT);
            requirements.put("Combat", formatCombatRequirement(level, armourData));
        }
    }

    private static void checkWeaponRequirements(TooltipContext context, Map<String, String> requirements) {
        if (SilentGearHelper.isSilentGearLoaded() && context.stack.getItem() instanceof GearItem && SilentGearHelper.isWeapon(context.stack)) {
            ConfigData data = CombatConfig.getData(context.stack, false);
            if (data != null) {
                int level = context.skills.getSkillLevel(SkillType.COMBAT);
                requirements.put("Combat", formatCombatRequirement(level, data));
            }
        }

        ConfigData armourData = CombatConfig.getData(context.stack, false);
        if (armourData != null) {
            int level = context.skills.getSkillLevel(SkillType.COMBAT);
            requirements.put("Combat", formatCombatRequirement(level, armourData));
        }
    }

    private static String formatRequirement(int playerLevel, ConfigData data) {
        String color = playerLevel < data.getLevel() ? COLOR_REQUIRED : COLOR_ACHIEVED;
        return String.format("%sLevel: %d%s, Xp: %.1f",
                color,
                data.getLevel(),
                playerLevel < data.getLevel() ? " (Current: " + playerLevel + ")" : "",
                data.getXp());
    }

    private static String formatToolRequirement(int playerLevel, ConfigData data) {
        String color = playerLevel < data.getLevel() ? COLOR_REQUIRED : COLOR_ACHIEVED;
        return String.format("%sLevel: %d%s",
                color,
                data.getLevel(),
                playerLevel < data.getLevel() ? " (Current: " + playerLevel + ")" : "");
    }

    private static String formatCombatRequirement(int playerLevel, ConfigData data) {
        String color = playerLevel < data.getLevel() ? COLOR_REQUIRED : COLOR_ACHIEVED;
        return String.format("%sLevel: %d%s",
                color,
                data.getLevel(),
                playerLevel < data.getLevel() ? " (Current: " + playerLevel + ")" : "");
    }

    private static void addRequirementsToTooltip(TooltipContext context, Map<String, String> requirements) {
        List<Component> tooltip = context.event.getToolTip();
        AtomicInteger insertIndex = new AtomicInteger(1);

        tooltip.add(insertIndex.getAndIncrement(), Component.literal(COLOR_HEADER + "RaynnaRPG: "));
        requirements.forEach((skill, text) ->
                tooltip.add(insertIndex.getAndIncrement(), Component.literal(COLOR_TEXT + skill + " " + text))
        );
    }

    private static void handleDebugTooltips(TooltipContext context) {
        if (!context.isCreative &&
                !(context.event.getEntity() instanceof ServerPlayer serverPlayer && serverPlayer.hasPermissions(2))) {
            return;
        }

        if (!context.isShiftDown) {
            context.event.getToolTip().add(Component.literal("Hold SHIFT to reveal item tags."));
            return;
        }

        context.event.getToolTip().add(Component.literal(COLOR_HEADER + "Description: " + COLOR_TEXT + context.stack.getItem().getDescriptionId()));
        context.event.getToolTip().add(Component.literal(COLOR_HEADER + "Tags: "));
        context.stack.getTags().forEach(tag ->
                context.event.getToolTip().add(Component.literal(COLOR_TEXT + "- " + tag.location()))
        );
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(ClientItemEvents.class);
    }
}