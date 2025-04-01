package net.raynna.raynnarpg.client.events;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
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
import net.raynna.raynnarpg.utils.Colour;
import net.raynna.raynnarpg.utils.RegistryUtils;
import net.raynna.raynnarpg.utils.SilentGearHelper;
import net.raynna.raynnarpg.utils.Utils;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.property.*;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.client.util.GearTooltipFlag;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.CoreGearPart;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.util.Color;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientItemEvents {

    /*public static void onItemHover(ItemTooltipEvent event) {
        if (!shouldProcessTooltip(event)) return;
        TooltipContext context = new TooltipContext(event);
        ItemStack stack = context.event.getItemStack();
        event.getToolTip().clear();
        Map<String, String> requirements = new LinkedHashMap<>();
        checkMiningRequirements(context, requirements);
        checkCraftingRequirements(context, requirements);
        checkSmeltingRequirements(context, requirements);
        checkToolRequirements(context, requirements);
        checkArmourRequirement(context, requirements);
        checkWeaponRequirements(context, requirements);
        /*if (!requirements.isEmpty()) {
            addRequirementsToTooltip(context, requirements);
        }*/

        /*Map<String, String> foodDescriptions = new LinkedHashMap<>();
        Map<String, String> traits = new LinkedHashMap<>();
        Map<String, String> enchants = new LinkedHashMap<>();
        checkFoodEffects(context, foodDescriptions);
        checkEnchants(context, enchants);
        checkTraits(context, traits);
        Component hoverName = context.stack.getHoverName();
        String hoverText = hoverName.getString();
        hoverText = hoverText.replace(" & Knuckles", "").trim();

        Component modifiedHoverName = Component.literal(hoverText).withStyle(hoverName.getStyle());

        context.event.getToolTip().addFirst(modifiedHoverName);

        if (!requirements.isEmpty()) {
            addRequirementsToTooltip(context, requirements);
        }
        if (!foodDescriptions.isEmpty()) {
            addFoodTooltip(context, foodDescriptions);
        }
        addStatsTooltip(context);
        addDurabilityTooltip(context, false);
        if (!enchants.isEmpty()) {
            addEnchantmentTooltip(context, enchants);
        }

        //silentgear extra tooltips
        boolean disabled = false;
        //System.out.println(stack.getHoverName().getString() + ", type: " + GearHelper.getType(stack));
        if (!disabled && SilentGearHelper.isSilentGearLoaded() && stack.getItem() instanceof GearItem) {
            if (!context.isControlDown) {
                context.event.getToolTip().add(context.event.getToolTip().size(), Component.literal(Colour.YELLOW + "Properties" + Colour.GRAY + " [Left Control]"));
            } else {
                addPropertiesTooltip(context, traits);
            }
            if (!context.isAltDown) {
                context.event.getToolTip().add(context.event.getToolTip().size(), Component.literal(Colour.YELLOW + "Construction" + Colour.GRAY + " [Left Alt]"));
            } else {
                context.event.getToolTip().add(context.event.getToolTip().size(), Component.literal(Colour.GOLD + "Construction"));
                addPartsTooltip(context);
            }
        }

        handleDebugTooltips(context);
        String mod = ResourceLocation.parse(stack.getDescriptionId()).toString().split(":")[0];
    }*/

    private static boolean shouldProcessTooltip(ItemTooltipEvent event) {
        return event.getEntity() == null || event.getEntity().isAlive();
    }

    private static double getAttackRange(LivingEntity entity) {
        ItemStack stack = entity.getMainHandItem();
        double base = GearHelper.getType(stack).matches(GearTypes.TOOL.get())
                ? GearData.getProperties(stack).getNumber(GearProperties.ATTACK_REACH)
                : GearProperties.ATTACK_REACH.get().getBaseValue();

        AttributeInstance attribute = entity.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (attribute != null) {
            double reachBonus = attribute.getValue() - attribute.getBaseValue();
            return base + reachBonus;
        }

        return base;
    }

    private static float getTraitModifiedMiningSpeed(ItemStack stack, BlockState state, float baseSpeed) {
        var totalModifier = 0f;
        for (var traitInstance : TraitHelper.getTraits(stack)) {
            totalModifier += traitInstance.getTrait().getMiningSpeedModifier(traitInstance.getLevel(), state, baseSpeed);
        }
        return baseSpeed * (1f + totalModifier);
    }

    private static void addStatsTooltip(TooltipContext context) {
        AtomicInteger index = new AtomicInteger(context.event.getToolTip().size());
        EquipmentSlot slot = getAppropriateSlot(context.stack);
        ItemStack equippedStack = getCurrentlyEquippedItem(context.event.getEntity(), slot);

        double attackDamage = getItemAttackDamage(context.stack);
        if (attackDamage > 0) {
            double equippedDamage = getItemAttackDamage(equippedStack);
            String comparison = getComparisonString(attackDamage, equippedDamage);
            context.event.getToolTip().add(index.getAndIncrement(),
                    Component.literal(Colour.GOLD + "Attack Damage: " + Colour.WHITE +
                            String.format("%.1f", attackDamage) + comparison));
        }

        if (SilentGearHelper.isSilentGearLoaded() && context.stack.getItem() instanceof GearItem && context.event.getEntity() != null) {
            if (SilentGearHelper.isWeapon(context.stack)) {
                double attackRange = getAttackRange(context.event.getEntity());
                if (attackRange > 0.0) {
                    context.event.getToolTip().add(index.getAndIncrement(),
                            Component.literal(Colour.GOLD + "Attack Range: " +
                                    String.format("%.1f", attackRange)));
                }
            }
        }
    }

    private static double getItemAttackDamage(ItemStack stack) {
        if (stack.isEmpty()) return 0;

        if (SilentGearHelper.isSilentGearLoaded() && stack.getItem() instanceof GearItem) {
            return GearHelper.getAttackDamageModifier(stack);
        } else {
            return getAttributeValue(stack, Attributes.ATTACK_DAMAGE.getKey());
        }
    }

    private static double getItemAttackReach(TooltipContext context) {
        if (context.stack.isEmpty()) return 0;

        if (SilentGearHelper.isSilentGearLoaded() && context.stack.getItem() instanceof GearItem) {
            return context.event.getEntity() != null ? getAttackRange(context.event.getEntity()) : 0.0;
        } else {
            return getAttributeValue(context.stack, Attributes.ENTITY_INTERACTION_RANGE.getKey());
        }
    }

    private static EquipmentSlot getAppropriateSlot(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armorItem) {
            return armorItem.getEquipmentSlot();
        }
        return EquipmentSlot.MAINHAND;
    }

    private static ItemStack getCurrentlyEquippedItem(Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player player) {
            if (slot == EquipmentSlot.MAINHAND) {
                return player.getInventory().getSelected();
            }
            return player.getItemBySlot(slot);
        }
        return ItemStack.EMPTY;
    }

    private static double getAttributeValue(ItemStack stack, ResourceKey<Attribute> attribute) {
        if (stack.isEmpty()) return 0;
        return stack.getAttributeModifiers().modifiers().stream()
                .filter(e -> e.attribute().is(attribute))
                .mapToDouble(e -> e.modifier().amount())
                .findFirst()
                .orElse(0);
    }

    private static String getComparisonString(double newValue, double equippedValue) {
        if (equippedValue <= 0) return "";
        double diff = newValue - equippedValue;
        if (diff > 0) return Colour.GREEN + " (+" + String.format("%.1f", diff) + ")";
        if (diff < 0) return Colour.RED + " (" + String.format("%.1f", diff) + ")";
        return Colour.GRAY + " (=)";
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

    private static void checkTraits(TooltipContext context, Map<String, String> descriptions) {
        ItemStack stack = context.stack;

        if (SilentGearHelper.isSilentGearLoaded() && stack.getItem() instanceof GearItem) {
            List<TraitInstance> traits = GearData.getProperties(stack).getTraits();
            if (!traits.isEmpty()) {
                StringBuilder traitsLine = new StringBuilder(Colour.GOLD + "  Traits: ");
                boolean first = true;

                for (TraitInstance trait : traits) {
                    if (!first) {
                        traitsLine.append(Colour.GRAY + ", ");
                    }
                    first = false;
                    String name = trait.getTrait().getDisplayName(trait.getLevel()).getString();
                    traitsLine.append(Colour.WHITE).append(name);

                    String desc = trait.getTrait().getDescription(trait.getLevel()).getString();
                    descriptions.put(name, Colour.GRAY + desc);
                }

                descriptions.put("TRAITS_LINE", traitsLine.toString());
            }
        }
    }

    private static void checkEnchants(TooltipContext context, Map<String, String> enchants) {
        ItemStack stack = context.stack;
        if (stack.isEnchanted()) {
            ItemEnchantments enchantments = stack.getTagEnchantments();
            if (!enchantments.keySet().isEmpty()) {
                StringBuilder enchantmentsLine = new StringBuilder(Colour.LIGHT_PURPLE + "Enchantments: ");
                boolean first = true;
                for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                    if (!first) {
                        enchantmentsLine.append(Colour.GRAY + ", ");
                    }
                    first = false;
                    Enchantment enchant = entry.getKey().value();
                    int level = entry.getIntValue();

                    String name = enchant.description().getString();
                    enchantmentsLine.append(Colour.WHITE).append(name).append(" ").append(level);
                    String description = getEnchantmentDescription(name, level);
                    enchants.put(Colour.WHITE + name + " " + level, Colour.GRAY + description);
                }
                enchants.put("ENCHANTMENT_LINES", enchantmentsLine.toString());
            }
        }
    }

    private static void checkFoodEffects(TooltipContext context, Map<String, String> foodDescription) {
        ItemStack stack = context.stack;

        if (!stack.isEmpty()) {
            FoodProperties foodProps = stack.getItem().getFoodProperties(stack, null);

            if (foodProps != null) {
                StringBuilder effectLine = new StringBuilder();
                List<FoodProperties.PossibleEffect> effects = foodProps.effects();
                String nutrition = String.valueOf(foodProps.nutrition());
                String saturdation = String.format("%.1f", foodProps.saturation());
                saturdation = saturdation.replace(".0", "");
                effectLine.append(Colour.HEART_ICON).append(": ").append(nutrition).append(", ").append(Colour.SATURATION_ICON).append(": ").append(saturdation);
                if (!effects.isEmpty()) {
                    boolean first = true;
                    effectLine.append(Colour.LIGHT_PURPLE).append("\nEffects: ");
                    for (FoodProperties.PossibleEffect effectPair : effects) {
                        if (!first) {
                            effectLine.append(Colour.GRAY + ", ");
                        }
                        first = false;
                        MobEffectInstance effect = effectPair.effect();
                        float probability = effectPair.probability();
                        String effectName = effect.getEffect().value().getDisplayName().getString();
                        String duration = MobEffectUtil.formatDuration(effect, 1.0f, 20.0f).getString();
                        String amplifier = getAmplifierString(effect.getAmplifier());
                        effectLine.append(Colour.WHITE).append(effectName);
                        if (!amplifier.isEmpty()) {
                            effectLine.append(" ").append(amplifier);
                        }
                        effectLine.append(Colour.GRAY).append(" (").append(duration).append(")");
                        if (probability < 1.0F) {
                            effectLine.append(Colour.GRAY).append(" ").append((int) (probability * 100)).append("% chance");
                        }
                    }
                }
                foodDescription.put("FOOD_LINE", effectLine.toString());
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

    private static String getEnchantmentDescription(String enchantName, int level) {
        switch (enchantName.toLowerCase()) {
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
                return String.format("Adds %d%%-75%% sweeping damage (25%% per level)", 25 + (level - 1) * 25);
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


    private static String formatRequirement(int playerLevel, ConfigData data) {
        String color = playerLevel < data.getLevel() ? Colour.RED : Colour.GREEN;
        return String.format("%sLevel: %d%s, Xp: %.1f",
                color,
                data.getLevel(),
                playerLevel < data.getLevel() ? " (Current: " + playerLevel + ")" : "",
                data.getXp());
    }

    private static String formatToolRequirement(int playerLevel, ConfigData data) {
        String color = playerLevel < data.getLevel() ? Colour.RED : Colour.GREEN;
        return String.format("%sLevel: %d%s",
                color,
                data.getLevel(),
                playerLevel < data.getLevel() ? " (Current: " + playerLevel + ")" : "");
    }

    private static String formatCombatRequirement(int playerLevel, ConfigData data) {
        String color = playerLevel < data.getLevel() ? Colour.RED : Colour.GREEN;
        return String.format("%sLevel: %d%s",
                color,
                data.getLevel(),
                playerLevel < data.getLevel() ? " (Current: " + playerLevel + ")" : "");
    }

    private static void addRequirementsToTooltip(TooltipContext context, Map<String, String> requirements) {
        AtomicInteger index = new AtomicInteger(context.event.getToolTip().size());

        context.event.getToolTip().add(index.getAndIncrement(), Component.literal(Colour.GOLD + "RaynnaRPG: "));
        requirements.forEach((skill, text) -> {
                    context.event.getToolTip().add(index.getAndIncrement(), Component.literal(Colour.GRAY + skill + " " + text));
                }
        );
    }

    private static void addFoodTooltip(TooltipContext context, Map<String, String> foodDescription) {
        AtomicInteger index = new AtomicInteger(context.event.getToolTip().size());
        context.event.getToolTip().add(index.getAndIncrement(), Component.literal(foodDescription.get("FOOD_LINE")));
    }

    private static void addEnchantmentTooltip(TooltipContext context, Map<String, String> enchants) {
        AtomicInteger index = new AtomicInteger(context.event.getToolTip().size());
        if (enchants.containsKey("ENCHANTMENT_LINES") && !context.isAltDown) {
            context.event.getToolTip().add(index.getAndIncrement(), Component.literal(enchants.get("ENCHANTMENT_LINES")));
            context.event.getToolTip().add(index.getAndIncrement(), Component.literal(" " + Colour.SUB_BULLET + Colour.YELLOW + " Show details.." + Colour.GRAY + " [Left Alt]"));
        }
        if (context.isAltDown) {
            context.event.getToolTip().add(index.getAndIncrement(), Component.literal(Colour.LIGHT_PURPLE + "Enchantments: "));
            enchants.forEach((name, desc) -> {
                if (!name.equals("ENCHANTMENT_LINES")) {
                    context.event.getToolTip().add(index.getAndIncrement(), Component.literal(" " + Colour.SUB_BULLET + " " + name + ": " + desc));
                }
            });
        }
    }

    private static void addTraitsTooltip(TooltipContext context, Map<String, String> descriptions) {
        AtomicInteger index = new AtomicInteger(context.event.getToolTip().size());
        if (descriptions.containsKey("TRAITS_LINE") && !context.isAltDown) {
            context.event.getToolTip().add(index.getAndIncrement(), Component.literal(descriptions.get("TRAITS_LINE")));
            context.event.getToolTip().add(index.getAndIncrement(), Component.literal("   " + Colour.SUB_BULLET + Colour.YELLOW + " Show details.. " + Colour.GRAY + "[Left Alt]"));

        }
        if (context.isAltDown) {
            context.event.getToolTip().add(index.getAndIncrement(), Component.literal(Colour.GOLD + "  Traits: "));
            descriptions.forEach((name, desc) -> {
                if (!name.equals("TRAITS_LINE")) {
                    context.event.getToolTip().add(index.getAndIncrement(), Component.literal("   " + Colour.SUB_BULLET + " " + name + ": " + desc));
                }
            });
        }
    }


    private static void addPropertiesTooltip(TooltipContext context, Map<String, String> traits) {
        if (!context.isControlDown) return;

        ItemStack stack = context.stack;
        List<Component> tooltip = context.event.getToolTip();

        if (SilentGearHelper.isSilentGearLoaded() && stack.getItem() instanceof GearItem gearItem) {
            boolean shift = context.isShiftDown;
            GearTooltipFlag flag = GearTooltipFlag.withModifierKeys(shift, true, false);

            tooltip.add(Component.literal(Colour.GOLD + "Properties:"));

            GearPropertiesData gearProperties = GearData.getProperties(stack);
            Iterable<GearProperty<?, ?>> displayProperties = getDisplayProperties(stack, flag);

            for (GearProperty<?, ?> property : displayProperties) {
                if (property == GearProperties.ENCHANTMENT_VALUE.get()) {
                    continue;
                }
                if (property == GearProperties.TRAITS.get()) {
                    addTraitsTooltip(context, traits);
                    continue;
                }
                if (property == GearProperties.DURABILITY.get()) {
                    addDurabilityTooltip(context, true);
                    continue;
                }

                GearPropertyValue<?> value = gearProperties.get(property);
                if (value == null) continue;

                for (Component line : property.getTooltipLinesUnchecked(value, flag)) {
                    tooltip.add(Component.literal(Colour.GRAY + "  ")
                            .append(line.copy().withStyle(ChatFormatting.WHITE)));
                }
            }

            if (!flag.isAdvanced()) {
                tooltip.add(Component.literal(" " + Colour.SUB_BULLET + Colour.YELLOW + " Advanced Stats " + Colour.GRAY + "[Left Shift]"));
            }
        }
    }

    private static Iterable<GearProperty<?, ?>> getDisplayProperties(ItemStack stack, GearTooltipFlag flag) {
        if (flag.isAdvanced() && SilentGear.isDevBuild()) {
            return SgRegistries.GEAR_PROPERTY;
        }
        return GearPropertyGroups.getSortedRelevantProperties(
                GearHelper.getType(stack).relevantPropertyGroups());
    }
    // tooltipListParts(stack, tooltip, sortedConstructionParts, flag);

    private static void addPartsTooltip(TooltipContext context) {
        TextListBuilder builder = new TextListBuilder();
        PartList constructionParts = GearData.getConstruction(context.stack).parts();
        List<PartInstance> sortedConstructionParts = constructionParts.toSortedList();
        for (PartInstance part : sortedConstructionParts) {
            if (part.isValid() && part.get().isVisible()) {
                int partNameColor = Color.blend(part.getColor(context.stack), Color.VALUE_WHITE, 0.25f) & 0xFFFFFF;
                MutableComponent partNameText = TextUtil.withColor(part.getDisplayName().copy(), partNameColor);
                builder.add(partNameText.append(TextUtil.misc("spaceBrackets", part.getType().getDisplayName()).withStyle(ChatFormatting.DARK_GRAY)));

                if (part.get() instanceof CoreGearPart) {
                    builder.indent();
                    for (MaterialInstance material : CompoundPartItem.getMaterials(part.getItem())) {
                        int nameColor = material.getNameColor(part.getType(), GearTypes.ALL.get());
                        builder.add(TextUtil.withColor(material.getDisplayNameWithModifiers(part.getType(), ItemStack.EMPTY), nameColor));
                    }
                    builder.unindent();
                }
            }
        }
        context.event.getToolTip().addAll(builder.build());
    }

    private static void addDurabilityTooltip(TooltipContext context, boolean silentgear) {
        ItemStack stack = context.stack;

        if (!stack.isDamageableItem()) return;

        int currentDamage = stack.getDamageValue();
        int maxDurability = stack.getMaxDamage();

        if (maxDurability <= 0) return;

        int remainingDurability = maxDurability - currentDamage;

        double remainingPercent = 100.0 * (1.0 - ((double) currentDamage / maxDurability));

        ChatFormatting color;
        if (remainingPercent > 70) {
            color = ChatFormatting.GREEN;
        } else if (remainingPercent > 30) {
            color = ChatFormatting.YELLOW;
        } else {
            color = ChatFormatting.RED;
        }

        Component durabilityLine = Component.literal("")
                .append(Component.literal((!silentgear ? "" : "  ") + "Durability: ").withStyle(Style.EMPTY.withColor(0x4682B4)))
                .append(Component.literal(remainingDurability + "/" + maxDurability).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" (").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(String.format("%.1f", remainingPercent)).withStyle(color))
                .append(Component.literal("%").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(")").withStyle(ChatFormatting.WHITE));
        AtomicInteger index = new AtomicInteger(context.event.getToolTip().size());
        context.event.getToolTip().add(index.getAndIncrement(), durabilityLine);
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
}