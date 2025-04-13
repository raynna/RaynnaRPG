package net.raynna.raynnarpg.config.combat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.raynna.raynnarpg.compat.silentgear.SilentGearCompat;
import net.raynna.raynnarpg.config.ConfigData;
import net.raynna.raynnarpg.utils.ItemUtils;
import net.raynna.raynnarpg.utils.Utils;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.setup.gear.GearProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CombatConfig {

    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> WEAPONS = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> GEARS = new HashMap<>();

    /**
     * usage: CombatConfig.registerConfig(builder, "minecraft:netherite_pickaxe", 40, false);
     * usage: CombatConfig.registerConfig(builder, "netherite", 40, true);
     */

    public static void registerMultipleConfigs(ModConfigSpec.Builder builder, String subCategoryKey, String translation, List<CombatEntry> entries) {
        builder.translation(translation).push(subCategoryKey);
        for (CombatEntry entry : entries) {
            registerConfig(builder, entry.key(), entry.level(), entry.armour());
        }
        builder.pop();
    }

    public static void registerConfig(ModConfigSpec.Builder builder, String key, int level, boolean armour) {
        String modId = key.contains(":") ? key.split(":")[0] : key;
        String itemId = key.contains(":") ? key.split(":")[1] : key;
        if (key.contains("tier"))
            key = key.split(":")[1];
        String readableType = itemId.replace("_", " ");
        String keyTranslation = Utils.capitalize(readableType) + " Level";

        ModConfigSpec.ConfigValue<Integer> configValue = builder
                .translation(keyTranslation)
                .comment("Configurations for level requirements for " + readableType)
                .comment("Default: " + level)
                .define(modId+"_"+itemId +"_level", level);
        if (armour) {
            GEARS.put(key, configValue);
        } else {
            WEAPONS.put(key, configValue);
        }
    }

    public static ConfigData getSilentGearData(String tier, boolean armour) {
        ModConfigSpec.ConfigValue<Integer> levelValue = GEARS.get(tier.toLowerCase());
        return levelValue != null ?
                new ConfigData(levelValue.get(), 0) :
                null;
    }

    public static ConfigData getData(ItemStack stack, boolean armour) {
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        if (!ItemUtils.isWeapon(stack) && !ItemUtils.isArmor(stack)) {
            return null;
        }
        if (SilentGearCompat.IS_LOADED && SilentGearCompat.isGearItem(stack)) {
            String tier = ItemUtils.getGearProperty(stack, GearProperties.HARVEST_TIER.get());
            return getSilentGearData(tier, armour);
        }

        ModConfigSpec.ConfigValue<Integer> levelValue = armour ?
                GEARS.get(itemId) :
                WEAPONS.get(itemId);

        return levelValue != null ?
                new ConfigData(levelValue.get(), 0) :
                null;
    }

    private static ConfigData getDataByKey(String key, boolean armour) {
        ModConfigSpec.ConfigValue<Integer> levelValue = armour ?
                GEARS.get(key) :
                WEAPONS.get(key);
        return levelValue != null ?
                new ConfigData(levelValue.get(), 0) :
                null;
    }

    public static void refresh() {
        for (String key : GEARS.keySet()) {
            getDataByKey(key, true);
        }
        for (String key : WEAPONS.keySet()) {
            getDataByKey(key, false);
        }
    }
}