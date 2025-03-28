package net.raynna.raynnarpg.config.tools;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ToolConfig {

    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> TOOLS = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> SILENT_GEAR_TOOLS = new HashMap<>();

    /**
     * usage: ToolConfig.registerConfig(builder, "minecraft:netherite_pickaxe", 40, false);
     * usage: ToolConfig.registerConfig(builder, "netherite", 40, true);
     */

    public static void registerMultipleConfigs(ModConfigSpec.Builder builder, String subCategoryKey, String translation, List<ToolEntry> entries) {
        builder.translation(translation).push(subCategoryKey);
        for (ToolEntry entry : entries) {
            registerConfig(builder, entry.key(), entry.level());
        }
        builder.pop();
    }

    public static void registerConfig(ModConfigSpec.Builder builder, String key, int level) {
        String modId = key.contains(":") ? key.split(":")[0] : key;
        String itemId = key.contains(":") ? key.split(":")[1] : key;
        String readableType = itemId.replace("_", " ");
        String keyTranslation = Utils.capitalize(readableType) + " Level";
        if (key.contains("tier"))
            key = key.split(":")[1];
        ModConfigSpec.ConfigValue<Integer> configValue = builder
                .translation(keyTranslation)
                .comment("Configurations for level requirements for " + readableType)
                .comment("Default: " + level)
                .define(modId + "_" + itemId + "_level", level);
        TOOLS.put(key, configValue);
    }

    public static ConfigData getSilentGearData(String harvestTier) {
        return getSilentGearDataByKey(harvestTier);
    }

    private static ConfigData getSilentGearDataByKey(String key) {
        ModConfigSpec.ConfigValue<Integer> levelValue = SILENT_GEAR_TOOLS.get(key);
        int level = levelValue != null ? levelValue.get() : 0;
        if (level != 0) {
            return new ConfigData(level, 0);
        }
        return null;
    }

    public static ConfigData getToolData(ItemStack stack) {
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return getToolDataByKey(itemId);
    }

    private static ConfigData getToolDataByKey(String key) {
        ModConfigSpec.ConfigValue<Integer> levelValue = TOOLS.get(key);
        int level = levelValue != null ? levelValue.get() : 0;
        if (level != 0) {
            return new ConfigData(level, 0);
        }
        return null;
    }
}