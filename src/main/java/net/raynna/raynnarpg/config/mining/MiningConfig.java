package net.raynna.raynnarpg.config.mining;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.utils.RegistryUtils;
import net.raynna.raynnarpg.utils.Utils;

import java.util.*;

public class MiningConfig {

    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> MINING_LEVEL = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<Double>> MINING_XP = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<List<String>>> MINING_TAGS = new HashMap<>();

    public static void registerMultipleConfigs(ModConfigSpec.Builder builder, String subCategoryKey, String translation, List<MiningEntry> entries) {
        builder.translation(translation).push(subCategoryKey);
        for (MiningEntry entry : entries) {
            registerConfig(builder, entry.key(), entry.level(), entry.xp(), entry.tags());
        }
        builder.pop();
    }

    /**
     * usage: MiningConfig.registerConfig(builder, "minecraft:iron_ore", 10, 5.0, "c:ores/iron");
     * usage: MiningConfig.registerConfig(builder, "diamond_ore", 20, 10.0, "c:ores/diamond");
     */
    public static void registerConfig(ModConfigSpec.Builder builder, String key, int level, double xp, String... tags) {
        String item = key.contains(":") ? key.split(":")[1] : key;
        String name = Utils.capitalize(item).replace("_", " ");
        ModConfigSpec.ConfigValue<Integer> levelValue = builder.translation(name + " Level: ")
                .comment("Config on mining level requirement for " + name + ".")
                .comment("Default: " + level)
                .define(item + "_level", level);
        ModConfigSpec.ConfigValue<Double> xpValue = builder.translation(name + " Xp: ")
                .comment("Config on mining experience yield for " + name + ".")
                .comment("Default: " + xp)
                .define(item + "_xp", xp);

        List<String> tagList = new ArrayList<>(List.of(tags));
        ModConfigSpec.ConfigValue<List<String>> tagsValue = builder.translation(name + " Tags: ")
                .comment("Default: " + tagList)
                .define(item + "_tags", tagList);
        MINING_LEVEL.put(key, levelValue);
        MINING_XP.put(key, xpValue);
        MINING_TAGS.put(key, tagsValue);
    }

    public static ConfigData getMiningData(ItemStack stack) {
        String blockId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

        ConfigData data = getMiningDataByKey(blockId);
        if (data != null) {
            return data;
        }

        Optional<Map.Entry<String, ModConfigSpec.ConfigValue<List<String>>>> matchingTag =
                stack.getTags().toList().stream().map(tag ->
                        tag.location().toString()).flatMap(tagName ->
                        MINING_TAGS.entrySet().stream().filter(entry ->
                                entry.getValue().get().contains(tagName))).findAny();

        if (matchingTag.isPresent()) {
            Map.Entry<String, ModConfigSpec.ConfigValue<List<String>>> entry = matchingTag.get();
            return getMiningDataByKey(entry.getKey());
        }

        return null;
    }


    public static ConfigData getMiningData(BlockState state) {
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();

        ConfigData data = getMiningDataByKey(blockId);
        if (data != null) {
            return data;
        }

        Optional<Map.Entry<String, ModConfigSpec.ConfigValue<List<String>>>> matchingTag =
                state.getTags().toList().stream().map(tag ->
                        tag.location().toString()).flatMap(tagName ->
                        MINING_TAGS.entrySet().stream().filter(entry ->
                                entry.getValue().get().contains(tagName))).findAny();

        if (matchingTag.isPresent()) {
            Map.Entry<String, ModConfigSpec.ConfigValue<List<String>>> entry = matchingTag.get();
            return getMiningDataByKey(entry.getKey());
        }

        return null;
    }


    private static ConfigData getMiningDataByKey(String key) {
        ModConfigSpec.ConfigValue<Integer> levelValue = MINING_LEVEL.get(key);
        ModConfigSpec.ConfigValue<Double> xpValue = MINING_XP.get(key);
        ModConfigSpec.ConfigValue<List<String>> tagsValue = MINING_TAGS.get(key);

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