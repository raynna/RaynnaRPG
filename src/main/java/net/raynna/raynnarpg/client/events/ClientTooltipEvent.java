package net.raynna.raynnarpg.client.events;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.player.ClientSkills;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.config.combat.CombatConfig;
import net.raynna.raynnarpg.config.crafting.CraftingConfig;
import net.raynna.raynnarpg.config.mining.MiningConfig;
import net.raynna.raynnarpg.config.smelting.SmeltingConfig;
import net.raynna.raynnarpg.config.tools.ToolConfig;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.utils.Colour;
import net.raynna.raynnarpg.utils.SilentGearHelper;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.util.GearData;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = RaynnaRPG.MOD_ID, value = Dist.CLIENT)
public class ClientTooltipEvent {


    private static AtomicInteger myToolTipIndex;

    private static boolean shouldProcessTooltip(ItemTooltipEvent event) {
        return event.getEntity() == null || event.getEntity().isAlive();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onItemHover(ItemTooltipEvent event) {
        if (!shouldProcessTooltip(event)) return;
        myToolTipIndex = new AtomicInteger(1);
        TooltipContext context = new TooltipContext(event);
        ItemStack stack = context.event.getItemStack();
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
        handleEnchantTooltips(context);
        handleTraitTooltips(context);
        handleFoodEffectsTooltips(context);
        handleDebugTooltips(context);
    }

    private static class TooltipContext {
        public final ItemTooltipEvent event;
        public final ItemStack stack;
        public final ClientSkills skills;
        public final boolean isCreative;
        public final boolean isShiftDown;
        public final boolean isAltDown;
        public final boolean isControlDown;

        public TooltipContext(ItemTooltipEvent event) {
            this.event = event;
            this.stack = event.getItemStack();
            this.skills = new ClientSkills(Minecraft.getInstance().player);
            this.isCreative = Minecraft.getInstance().player.isCreative();
            this.isShiftDown = GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(),
                    GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
            this.isAltDown = GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(),
                    GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_PRESS;
            this.isControlDown = GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(),
                    GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS;
        }
    }

    private static void removeTooltipLines(TooltipContext context, String... matches) {
        Iterator<Component> it = context.event.getToolTip().iterator();
        while (it.hasNext()) {
            Component component = it.next();
            String line = component.getString().toLowerCase();
            for (String match : matches) {
                if (line.contains(match.toLowerCase())) {
                    it.remove();
                    break;
                }
            }
        }
    }

    private static void handleFoodEffectsTooltips(TooltipContext context) {
        ItemStack stack = context.stack;
        List<Component> tooltip = context.event.getToolTip();
        if (stack.isEmpty()) {
            return;
        }

        FoodProperties foodProps = stack.getItem().getFoodProperties(stack, null);
        if (foodProps == null) {
            return;
        }
        int index = Math.min(myToolTipIndex.getAndIncrement(), tooltip.size());


        String saturdation = String.format("%.1f", foodProps.saturation());
        String nutrition = String.format(String.valueOf(foodProps.nutrition()));
        tooltip.add(index, Component.literal(Colour.HEART_ICON + nutrition + " " + Colour.SATURATION_ICON + saturdation));

        List<FoodProperties.PossibleEffect> effects = foodProps.effects();
        if (!effects.isEmpty()) {
            index = Math.min(myToolTipIndex.getAndIncrement(), tooltip.size());
            tooltip.add(index, Component.literal(Colour.LIGHT_PURPLE + " Effects:"));
            for (FoodProperties.PossibleEffect effectPair : effects) {
                MobEffectInstance effect = effectPair.effect();
                float probability = effectPair.probability();
                String effectName = effect.getEffect().value().getDisplayName().getString();
                String duration = MobEffectUtil.formatDuration(effect, 1.0f, 20.0f).getString();
                String amplifier = getAmplifierString(effect.getAmplifier());

                index = Math.min(myToolTipIndex.getAndIncrement(), tooltip.size());
                tooltip.add(index, Component.literal(Colour.WHITE + " " + effectName + " " + amplifier));

                index = Math.min(myToolTipIndex.getAndIncrement(), tooltip.size());
                tooltip.add(index,
                        Component.literal(Colour.GRAY + "    Duration: " + duration +
                                (probability < 1.0F ? " (" + (int)(probability * 100) + "% chance)" : "")));

            }
        }
    }

    private static String getAmplifierString(int amplifier) {
        if (amplifier <= 0) return "";
        return switch (amplifier) {
            case 1 -> "II";
            case 2 -> "III";
            case 3 -> "IV";
            case 4 -> "V";
            default -> "[" + (amplifier + 1) + "]";
        };
    }

    private static void handleTraitTooltips(TooltipContext context) {
        ItemStack stack = context.stack;
        List<Component> tooltip = context.event.getToolTip();

        if (!SilentGearHelper.isSilentGearLoaded()) {
            return;
        }
        List<TraitInstance> allTraits = new ArrayList<>();

        if (stack.getItem() instanceof GearItem) {
            List<TraitInstance> gearTraits = GearData.getProperties(stack).getTraits();
            allTraits.addAll(gearTraits);
        }

        try {
            MaterialInstance material = MaterialInstance.from(stack);
            if (material != null) {
                for (PartType partType : material.getPartTypes()) {
                    List<TraitInstance> matTraits = (List<TraitInstance>) material.getTraits(PartGearKey.ofAll(partType));
                    allTraits.addAll(matTraits);
                }
            }
        } catch (Exception e) {
            RaynnaRPG.LOGGER.error("Error getting material traits", e);
        }

        if (allTraits.isEmpty())
            return;
        AtomicInteger index = new AtomicInteger(Math.min(myToolTipIndex.getAndIncrement(), context.event.getToolTip().size()));
        if (context.isAltDown) {
            tooltip.add(index.get(), Component.literal(Colour.GOLD + "Traits:"));

            for (TraitInstance trait : allTraits) {
                String name = trait.getTrait().getDisplayName(trait.getLevel()).getString();
                String desc = trait.getTrait().getDescription(trait.getLevel()).getString();
                index.set(Math.min(myToolTipIndex.getAndIncrement(), context.event.getToolTip().size()));
                tooltip.add(index.get(), Component.literal(Colour.WHITE + " " + name));

                index.set(Math.min(myToolTipIndex.getAndIncrement(), context.event.getToolTip().size()));
                tooltip.add(index.get(), Component.literal(Colour.GRAY + "    " + desc));
            }
        } else {
            // Compact view - all on one line
            StringBuilder compactLine = new StringBuilder(Colour.GOLD + "Traits: ");

            boolean first = true;
            for (TraitInstance trait : allTraits) {
                if (!first) {
                    compactLine.append(Colour.GRAY + ", ");
                }
                first = false;
                String name = trait.getTrait().getDisplayName(trait.getLevel()).getString();
                compactLine.append(Colour.WHITE).append(name);
            }

            tooltip.add(index.get(), Component.literal(compactLine.toString()));

            index.set(Math.min(myToolTipIndex.getAndIncrement(), context.event.getToolTip().size()));
            tooltip.add(index.get(), Component.literal(Colour.YELLOW + "  Trait Details [Left Alt]"));
        }
    }

    private static void handleEnchantTooltips(TooltipContext context) {
        ItemStack stack = context.stack;
        List<Component> tooltip = context.event.getToolTip();

        if (!stack.isEnchanted()) return;

        ItemEnchantments enchantments = stack.getTagEnchantments();
        if (enchantments == null || enchantments.isEmpty()) return;

        Set<String> enchantNames = new HashSet<>();
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            Enchantment enchant = entry.getKey().value();
            enchantNames.add(enchant.description().getString());
        }

        Iterator<Component> it = tooltip.iterator();
        while (it.hasNext()) {
            String line = it.next().getString();
            for (String enchantName : enchantNames) {
                if (line.contains(enchantName)) {
                    it.remove();
                    break;
                }
            }
        }
        AtomicInteger index = new AtomicInteger(Math.min(myToolTipIndex.getAndIncrement(), context.event.getToolTip().size()));
        if (context.isAltDown) {
            tooltip.add(index.get(), Component.literal(Colour.LIGHT_PURPLE + "Enchantments:"));

            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                Enchantment enchant = entry.getKey().value();
                int level = entry.getIntValue();
                String name = enchant.description().getString();
                index.set(Math.min(myToolTipIndex.getAndIncrement(), context.event.getToolTip().size()));
                tooltip.add(index.get(), Component.literal(Colour.WHITE + " " + name + " " + level));
                System.out.println("name before fix: " + name);
                String fixed = name.replaceAll("^[^ ]+", "").trim();
                System.out.println("name after fix: " + fixed);
                index.set(Math.min(myToolTipIndex.getAndIncrement(), context.event.getToolTip().size()));
                tooltip.add(index.get(), Component.literal(Colour.GRAY + "    " + getEnchantmentDescription(fixed, level)));
            }
        } else {
            StringBuilder compactLine = new StringBuilder(Colour.LIGHT_PURPLE + "Enchantments: ");

            boolean first = true;
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                Enchantment enchant = entry.getKey().value();
                int level = entry.getIntValue();
                String name = enchant.description().getString();

                if (!first) {
                    compactLine.append(", ");
                }
                compactLine.append(Colour.WHITE).append(name).append(" ").append(level);
                first = false;
            }

            tooltip.add(index.get(), Component.literal(compactLine.toString()));

            index.set(Math.min(myToolTipIndex.getAndIncrement(), context.event.getToolTip().size()));
            tooltip.add(index.get(), Component.literal(Colour.YELLOW + "  Enchant Details [Left Alt]"));
        }
    }

    private static String getEnchantmentDescription(String enchantName, int level) {
        System.out.println("Enchant name: " + enchantName.toLowerCase());
        switch (enchantName.toLowerCase()) {
            case "capturing":
                return "Each level gives you a higher chance capture a monster egg.";
            case "protection":
                return String.format("Reduces all damage by %d%% (4%% per level)", level * 4);
            case "fire protection":
                return String.format("Reduces fire damage by %d%% (8%% per level)", level * 8);
            case "feather falling":
                return String.format("Reduces fall damage by %d%% (12%% per level)", level * 12);
            case "blast protection":
                return String.format("Reduces explosion damage by %d%% (8%% per level)", level * 8);
            case "projectile protection":
                return String.format("Reduces projectile damage by %d%% (8%% per level)", level * 8);
            case "thorns":
                return String.format("%d%% chance to damage attackers (15%% per level)", level * 15);
            case "depth strider":
                return String.format("Increases underwater speed by %d%% (33%% per level)", level * 33);
            case "soul speed":
                return String.format("Increases speed on soul sand by %d%% (30%% per level)", level * 30);
            case "swift sneak":
                return String.format("Increases sneak speed by %d%% (15%% per level)", level * 15);
            case "sharpness":
                return String.format("+%.1f damage (1.25 per level)", level * 1.25);
            case "smite":
                return String.format("+%.1f damage vs undead (2.5 per level)", level * 2.5);
            case "bane of arthropods":
                return String.format("+%.1f damage vs arthropods (2.5 per level)", level * 2.5);
            case "knockback":
                return String.format("Adds %d blocks knockback (3 per level)", level * 3);
            case "fire aspect":
                return String.format("Burns target for %d seconds (4 per level)", level * 4);
            case "looting":
                return String.format("+%d%% loot drops (33%% per level)", level * 33);
            case "sweeping edge":
                return String.format("Adds %d%% sweeping damage (25%% per level)", 25 + (level - 1) * 25);
            case "impaling":
                return String.format("+%.1f damage vs aquatic mobs (2.5 per level)", level * 2.5);
            case "efficiency":
                return String.format("+%d%% mining speed (30%% per level)", level * 30);
            case "fortune":
                int fortuneChance = level * 20;
                return String.format("%d%% chance for extra drops (+20%% per level)", Math.min(fortuneChance, 100));
            case "unbreaking":
                double reduction = 100 - (100 / (level + 1.0));
                return String.format("Reduces durability loss by %.1f%%", reduction);
            case "power":
                return String.format("+%d%% arrow damage (25%% per level)", level * 25);
            case "punch":
                return String.format("Adds %d blocks knockback (3 per level)", level * 3);
            case "quick charge":
                return String.format("%d%% faster reload (25%% per level)", level * 25);
            case "piercing":
                return String.format("Pierces through %d entities (1 per level)", level);
            case "mending":
                return "Repairs 2 durability per XP orb";
            case "infinity":
                return "100% chance to not consume arrows";
            case "channeling":
                return level == 1 ? "Summons lightning during storms" : "Max level is I";
            case "riptide":
                return String.format("Launches player %d blocks (scales with level)", level * 3);
            case "curse of binding":
            case "curse of vanishing":
                return "Effect is always active (100%)";
            default:
                return "Active (level " + level + ")";
        }
    }

    private static void addRequirementsToTooltip(TooltipContext context, Map<String, String> requirements) {
        AtomicInteger index = new AtomicInteger(Math.min(myToolTipIndex.getAndIncrement(), context.event.getToolTip().size()));
        context.event.getToolTip().add(index.get(), Component.literal(Colour.GOLD + "RaynnaRPG: "));
        requirements.forEach((skill, text) -> {
                    index.set(Math.min(myToolTipIndex.getAndIncrement(), context.event.getToolTip().size()));
                    context.event.getToolTip().add(index.get(), Component.literal(Colour.GRAY + skill + " " + text));
                }
        );
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
                requirements.put("Mining", formatRequirement(level, data));
            }
        }

        ConfigData toolData = ToolConfig.getToolData(context.stack);
        if (toolData != null) {
            int level = context.skills.getSkillLevel(SkillType.MINING);
            requirements.put("Mining", formatRequirement(level, toolData));
        }
    }

    private static void checkArmourRequirement(TooltipContext context, Map<String, String> requirements) {
        if (SilentGearHelper.isSilentGearLoaded() && context.stack.getItem() instanceof GearItem && SilentGearHelper.isArmor(context.stack)) {
            ConfigData data = CombatConfig.getData(context.stack, true);
            if (data != null) {
                int level = context.skills.getSkillLevel(SkillType.COMBAT);
                requirements.put("Combat", formatRequirement(level, data));
            }
        }

        ConfigData armourData = CombatConfig.getData(context.stack, true);
        if (armourData != null) {
            int level = context.skills.getSkillLevel(SkillType.COMBAT);
            requirements.put("Combat", formatRequirement(level, armourData));
        }
    }

    private static void checkWeaponRequirements(TooltipContext context, Map<String, String> requirements) {
        if (SilentGearHelper.isSilentGearLoaded() && context.stack.getItem() instanceof GearItem && SilentGearHelper.isWeapon(context.stack)) {
            ConfigData data = CombatConfig.getData(context.stack, false);
            if (data != null) {
                int level = context.skills.getSkillLevel(SkillType.COMBAT);
                requirements.put("Combat", formatRequirement(level, data));
            }
        }

        ConfigData armourData = CombatConfig.getData(context.stack, false);
        if (armourData != null) {
            int level = context.skills.getSkillLevel(SkillType.COMBAT);
            requirements.put("Combat", formatRequirement(level, armourData));
        }
    }

    private static String formatRequirement(int playerLevel, double xp, ConfigData data) {
        String color = playerLevel < data.getLevel() ? Colour.RED : Colour.GREEN;
        if (xp > 0) {
            return String.format("%sLevel: %d%s, Xp: %.1f",
                    color,
                    data.getLevel(),
                    playerLevel < data.getLevel() ? " (Current: " + playerLevel + ")" : "",
                    data.getXp());
        }
        return String.format("%sLevel: %d%s",
                color,
                data.getLevel(),
                playerLevel < data.getLevel() ? " (Current: " + playerLevel + ")" : "");
    }

    private static String formatRequirement(int playerLevel, ConfigData data) {
        return formatRequirement(playerLevel, 0, data);
    }

    private static void handleDebugTooltips(TooltipContext context) {
        if (!context.isCreative &&
                !(context.event.getEntity() instanceof ServerPlayer serverPlayer && serverPlayer.hasPermissions(2))) {
            return;
        }
        AtomicInteger index = new AtomicInteger(context.event.getToolTip().size());
        if (!context.isShiftDown) {
            context.event.getToolTip().add(context.event.getToolTip().size(), Component.literal(Colour.YELLOW + "Debug" + Colour.GRAY + " [Left Shift]"));
            return;
        } else {
            context.event.getToolTip().add(context.event.getToolTip().size(), Component.literal(Colour.YELLOW + "Debug"));
        }
        context.event.getToolTip().add(index.getAndIncrement(), Component.literal(Colour.WHITE + "Description: " + Colour.GRAY + context.stack.getItem().getDescriptionId()));
        context.event.getToolTip().add(index.getAndIncrement(), Component.literal(Colour.WHITE + "Tags: "));
        context.stack.getTags().forEach(tag ->
                context.event.getToolTip().add(index.getAndIncrement(), Component.literal(Colour.GRAY + "- " + tag.location()))
        );
    }

    public static void register() {
        NeoForge.EVENT_BUS.register(ClientTooltipEvent.class);
    }
}
