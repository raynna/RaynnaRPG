package net.raynna.raynnarpg.config.smelting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.utils.RegistryUtils;
import net.raynna.raynnarpg.utils.Utils;

import java.util.*;

public class SmeltingConfig {

    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> SMELTING_LEVEL = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<Double>> SMELTING_XP = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<String>> SMELTING_RAW_MATERIAL = new HashMap<>();

    public static void registerMultipleConfigs(ModConfigSpec.Builder builder, String subCategoryKey, String translation, List<SmeltingEntry> entries) {
        builder.translation(translation).push(subCategoryKey);
        for (SmeltingEntry entry : entries) {
            registerConfig(builder, entry.key(), entry.level(), entry.xp(), entry.rawVariant());
        }
        builder.pop();
    }

    /**
     * usage: SmeltingConfig.registerConfig(builder, "minecraft:cooked_beef", 1, 6.0, "minecraft:beef");
     */

    public static void registerConfig(ModConfigSpec.Builder builder, String key, int level, double xp, String rawVariant) {
        String item = key.contains(":") ? key.split(":")[1] : key;
        String name = Utils.capitalize(item).replace("_", " ");

        ModConfigSpec.ConfigValue<Integer> levelValue = builder.translation(name + " Level: ")
                .comment("Config on smelting level requirement for " + name + ".")
                .comment("Default: " + level)
                .define(item + "_level" , level);
        ModConfigSpec.ConfigValue<Double> xpValue = builder.translation(name + " Xp: ")
                .comment("Config on smelting experience yield for " + name + ".")
                .comment("Default: " + xp)
                .define(item + "_xp" , xp);
        ModConfigSpec.ConfigValue<String> rawValue = builder.translation(name + " Raw Variant: ")
                .comment("Default: " + rawVariant)
                .define(item + "_raw" , rawVariant);

        SMELTING_LEVEL.put(key, levelValue);
        SMELTING_XP.put(key, xpValue);
        SMELTING_RAW_MATERIAL.put(key, rawValue);
    }

    public static ConfigData getSmeltingData(ItemStack stack) {
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return getSmeltingDataByKey(itemId);
    }


    private static ConfigData getSmeltingDataByKey(String key) {
        ModConfigSpec.ConfigValue<Integer> levelValue = SMELTING_LEVEL.get(key);
        ModConfigSpec.ConfigValue<Double> xpValue = SMELTING_XP.get(key);
        ModConfigSpec.ConfigValue<String> rawValue = SMELTING_RAW_MATERIAL.get(key);

        int level = levelValue != null ? levelValue.get() : 0;
        double xp = xpValue != null ? xpValue.get() : 0;
        String raw = rawValue != null ? rawValue.get() : "";

        if (level != 0 || xp != 0 || !raw.isEmpty()) {
            return new ConfigData(level, xp, "none", raw);
        }
        return null;
    }

}