package net.raynna.raynnarpg.config.crafting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.config.ConfigRegister;
import net.raynna.raynnarpg.utils.Utils;

import java.util.*;

public class CraftingConfig {

    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> CRAFTING_LEVEL = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<Double>> CRAFTING_XP = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<List<String>>> CRAFTING_TAGS = new HashMap<>();

    /**
     * usage: CraftingConfig.registerConfig(builder, "minecraft:netherite_pickaxe", 40, 10.0, "c:tools/netherite", "c:tools/pickaxe");
     * usage: CraftingConfig.registerConfig(builder, "netherite", 40, 10.0, "c:tools/netherite", "c:tools/axe");
     */

    public static void registerMultipleConfigs(ModConfigSpec.Builder builder, String subCategoryKey, String translation, List<CraftingEntry> entries) {
        builder.translation(translation).push(subCategoryKey);
        for (CraftingEntry entry : entries) {
            registerConfig(builder, entry.key(), entry.level(), entry.xp(), entry.tags());
        }
        builder.pop();
    }

    public static void registerConfig(ModConfigSpec.Builder builder, String key, int level, double xp, String... tags) {
        String item = key.contains(":") ? key.split(":")[1] : key;
        String name = Utils.capitalize(item).replace("_", " ");
        ModConfigSpec.ConfigValue<Integer> levelValue = builder.translation(name + " Level: ")
                .comment("Config on crafting level requirement for " + name + " materials")
                .comment("Default: " + level)
                .define(item + "_level", level);
        ModConfigSpec.ConfigValue<Double> xpValue = builder.translation(name + " Xp: ")
                .comment("Config on crafting experience yield for " + name + " materials")
                .comment("Default: " + xp)
                .define(item + "_xp", xp);

        List<String> tagList = new ArrayList<>(List.of(tags));
        ModConfigSpec.ConfigValue<List<String>> tagsValue = builder.translation(name + " Tags: ")
                .comment("Default: " + tagList)
                .define(item + "_tags", tagList);

        CRAFTING_LEVEL.put(key, levelValue);
        CRAFTING_XP.put(key, xpValue);
        CRAFTING_TAGS.put(key, tagsValue);
    }

    public static ConfigData getCraftingData(ItemStack stack) {
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

        ConfigData data = getCraftingDataByKey(itemId);
        if (data != null) {
            return data;
        }

        Optional<Map.Entry<String, ModConfigSpec.ConfigValue<List<String>>>> matchingTag =
                stack.getTags().toList().stream().map(tag ->
                        tag.location().toString()).flatMap(tagName ->
                        CRAFTING_TAGS.entrySet().stream().filter(entry ->
                                entry.getValue().get().contains(tagName))).findAny();

        if (matchingTag.isPresent()) {
            Map.Entry<String, ModConfigSpec.ConfigValue<List<String>>> entry = matchingTag.get();
            return getCraftingDataByKey(entry.getKey());
        }

        return null;
    }


    private static ConfigData getCraftingDataByKey(String key) {
        ModConfigSpec.ConfigValue<Integer> levelValue = CRAFTING_LEVEL.get(key);
        ModConfigSpec.ConfigValue<Double> xpValue = CRAFTING_XP.get(key);
        ModConfigSpec.ConfigValue<List<String>> tagsValue = CRAFTING_TAGS.get(key);

        int level = levelValue != null ? levelValue.get() : 0;
        double xp = xpValue != null ? xpValue.get() : 0;
        List<String> tagsList = tagsValue != null ? tagsValue.get() : List.of();
        String tags = String.join(", ", tagsList);

        if (level != 0 || xp != 0 || !tagsList.isEmpty()) {
            return new ConfigData(level, xp, tags);
        }
        return null;
    }
}
