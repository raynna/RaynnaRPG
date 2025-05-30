package net.raynna.raynnarpg.config.smelting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.server.player.skills.Skills;
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
        String modId = key.contains(":") ? key.split(":")[0] : key;
        String item = key.contains(":") ? key.split(":")[1] : key;
        String name = Utils.capitalize(item).replace("_", " ");
        if (key.contains("tier"))
            key = key.split(":")[1];
        ModConfigSpec.ConfigValue<Integer> levelValue = builder.translation(name + " Level: ")
                .comment("Config on smelting level requirement for " + name + ".")
                .comment("Default: " + level)
                .define(modId+"_"+item + "_level" , level);
        ModConfigSpec.ConfigValue<Double> xpValue = builder.translation(name + " Xp: ")
                .comment("Config on smelting experience yield for " + name + ".")
                .comment("Default: " + xp)
                .define(modId+"_"+item + "_xp" , xp);
        ModConfigSpec.ConfigValue<String> rawValue = builder.translation(name + " Raw Variant: ")
                .comment("Default: " + rawVariant)
                .define(modId+"_"+item + "_raw" , rawVariant);

        SMELTING_LEVEL.put(key, levelValue);
        SMELTING_XP.put(key, xpValue);
        SMELTING_RAW_MATERIAL.put(key, rawValue);
    }

    public static ConfigData getSmeltingData(ItemStack stack) {
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        ConfigData data = getSmeltingDataByKey(itemId);

        if (data == null) {
            data = findDataByRawVariant(itemId);
        }
        if (data != null && !data.getRaw().isEmpty()) {
            String rawItemId = data.getRaw();
            ConfigData rawData = getSmeltingDataByKey(rawItemId);
            if (rawData != null && rawData.getXp() > 0) {
                data.setXp(rawData.getXp());
            }
        }
        return data;
    }


    private static ConfigData getSmeltingDataByKey(String key) {
        ModConfigSpec.ConfigValue<Integer> levelValue = SMELTING_LEVEL.get(key);
        ModConfigSpec.ConfigValue<Double> xpValue = SMELTING_XP.get(key);
        ModConfigSpec.ConfigValue<String> rawValue = SMELTING_RAW_MATERIAL.get(key);

        int level = levelValue != null ? levelValue.get() : 0;
        double xp = xpValue != null ? xpValue.get() : 0;
        String raw = rawValue != null ? rawValue.get() : "";
        if (xp == 0 && level > 0) {
            xp = Skills.getXpForMaterial(level, SkillType.SMELTING);
        }
        if (level != 0 || xp != 0 || !raw.isEmpty()) {
            return new ConfigData(level, xp, "none", raw);
        }
        return null;
    }

    private static ConfigData findDataByRawVariant(String rawItemId) {
        for (Map.Entry<String, ModConfigSpec.ConfigValue<String>> entry : SMELTING_RAW_MATERIAL.entrySet()) {
            if (rawItemId.equals(entry.getValue().get())) {

                String mainItemId = entry.getKey();
                ConfigData mainItemData = getSmeltingDataByKey(mainItemId);
                if (mainItemData != null) {
                    return new ConfigData(
                            mainItemData.getLevel(),
                            mainItemData.getXp(),
                            mainItemData.getTags(),
                            mainItemData.getRaw());
                }
            }
        }
        return null;
    }

    public static void refresh() {
        for (String key : SMELTING_LEVEL.keySet()) {
            getSmeltingDataByKey(key);
        }
    }
}