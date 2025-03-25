package net.raynna.raynnarpg;

import java.util.*;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.raynna.raynnarpg.config.*;
import net.raynna.raynnarpg.config.crafting.CraftingConfig;
import net.raynna.raynnarpg.config.crafting.CraftingEntry;
import net.raynna.raynnarpg.config.mining.MiningEntry;
import net.raynna.raynnarpg.config.smelting.SmeltingEntry;
import net.raynna.raynnarpg.config.tools.ToolEntry;
import net.raynna.raynnarpg.config.mining.MiningConfig;
import net.raynna.raynnarpg.config.smelting.SmeltingConfig;
import net.raynna.raynnarpg.config.tools.ToolConfig;

import static net.raynna.raynnarpg.RaynnaRPG.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> SILENT_GEAR_TOOLS = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> TOOLS = new HashMap<>();

    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> CRAFTING_LEVEL = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<Double>> CRAFTING_XP = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<List<String>>> CRAFTING_TAGS = new HashMap<>();

    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> MINING_LEVEL = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<Double>> MINING_XP = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<List<String>>> MINING_TAGS = new HashMap<>();

    public static final Map<String, ModConfigSpec.ConfigValue<Integer>> SMELTING_LEVEL = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<Double>> SMELTING_XP = new HashMap<>();
    public static final Map<String, ModConfigSpec.ConfigValue<String>> SMELTING_RAW_MATERIAL = new HashMap<>();


    public static final class Server {

        public static ModConfigSpec SPEC;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
            registerToolConfigs(builder);
            registerCraftingConfigs(builder);
            registerMiningConfigs(builder);
            registerSmeltingConfigs(builder);
            SPEC = builder.build();
        }

        public static void registerSmeltingConfigs(ModConfigSpec.Builder builder) {
            ConfigRegister.registerCategory(builder, "smelting_config", "Smelting", "Settings for Smelting", () -> {
                List<SmeltingEntry> foods = new ArrayList<>();
                foods.add(new SmeltingEntry("minecraft:cooked_salmon", 1, 6.0, "minecraft:salmon"));
                foods.add(new SmeltingEntry("minecraft:cooked_cod", 1, 6.0, "minecraft:cod"));
                foods.add(new SmeltingEntry("minecraft:baked_potato", 1, 6.0, "minecraft:potato"));
                foods.add(new SmeltingEntry("minecraft:cooked_beef", 1, 6.0, "minecraft:beef"));
                foods.add(new SmeltingEntry("minecraft:cooked_porkchop", 1, 6.0, "minecraft:porkchop"));
                foods.add(new SmeltingEntry("minecraft:cooked_chicken", 1, 6.0, "minecraft:chicken"));
                foods.add(new SmeltingEntry("minecraft:cooked_rabbit", 1, 6.0, "minecraft:rabbit"));
                SmeltingConfig.registerMultipleConfigs(builder, "smelting_food", "Food", foods);

                List<SmeltingEntry> metals = new ArrayList<>();
                metals.add(new SmeltingEntry("minecraft:copper_ingot", 10, 60.0, "minecraft:raw_copper"));
                metals.add(new SmeltingEntry("minecraft:iron_ingot", 20, 120.0, "minecraft:raw_iron"));
                metals.add(new SmeltingEntry("minecraft:gold_ingot", 25, 150.0, "minecraft:raw_gold"));
                metals.add(new SmeltingEntry("minecraft:diamond", 30, 180.0, "minecraft:diamond_ore"));
                metals.add(new SmeltingEntry("minecraft:netherite_scrap", 40, 240.0, "minecraft:ancient_debris"));
                if (ModList.get().isLoaded("silentgear")) {
                    metals.add(new SmeltingEntry("silentgear:crimson_iron_ingot", 43, 258.0, "silentgear:raw_crimson_iron"));
                    metals.add(new SmeltingEntry("silentgear:azure_silver_ingot", 48, 288.0, "silentgear:raw_azure_silver"));
                }
                SmeltingConfig.registerMultipleConfigs(builder, "smelting_metals", "Metals", metals);


                List<SmeltingEntry> materials = new ArrayList<>();
                materials.add(new SmeltingEntry("minecraft:charcoal", 1, 6.0, "minecraft:oak_logs"));
                materials.add(new SmeltingEntry("minecraft:glass", 2, 12.0, "minecraft:sand"));
                materials.add(new SmeltingEntry("minecraft:bricks", 3, 18.0, "minecraft:clay_ball"));
                materials.add(new SmeltingEntry("minecraft:stone", 5, 30.0, "minecraft:cobblestone"));
                materials.add(new SmeltingEntry("minecraft:smooth_stone", 8, 48.0, "minecraft:stone"));
                SmeltingConfig.registerMultipleConfigs(builder, "smelting_general", "General", materials);
            });
        }

        public static void registerCraftingConfigs(ModConfigSpec.Builder builder) {
            boolean hasSilentGear = ModList.get().isLoaded("silentgear");
            ConfigRegister.registerCategory(builder, "crafting_config", "Crafting", "Settings for Crafting", () -> {
                List<CraftingEntry> wood = new ArrayList<>();

                //Wood
                wood.add(new CraftingEntry("minecraft:logs", 1, 4.0, "minecraft:logs"));
                wood.add(new CraftingEntry("minecraft:planks", 1, 1.0, "minecraft:planks"));
                wood.add(new CraftingEntry("minecraft:stick", 1, 0.25, "c:rods/wooden"));
                CraftingConfig.registerMultipleConfigs(builder, "crafting_wood_materials", "Wood", wood);

                //Metals
                List<CraftingEntry> metals = new ArrayList<>();
                metals.add(new CraftingEntry("minecraft:iron_ingot", 15, 60.0, "c:ingots/iron"));
                metals.add(new CraftingEntry("minecraft:copper_ingot", 10, 40.0, "c:ingots/copper"));
                metals.add(new CraftingEntry("minecraft:gold_ingot", 20, 80.0, "c:ingots/gold"));
                metals.add(new CraftingEntry("minecraft:netherite_ingot", 40, 320.0, "c:ingots/netherite"));
                metals.add(new CraftingEntry("minecraft:netherite_scrap", 40, 80.0, "c:ingots/netherite"));
                if (hasSilentGear) {
                    metals.add(new CraftingEntry("silentgear:blaze_gold", 20, 140.0, "c:ingots/blaze_gold"));
                    metals.add(new CraftingEntry("silentgear:crimson_iron", 43, 172.0, "c:ingots/crimson_iron"));
                    metals.add(new CraftingEntry("silentgear:crimson_steel_ingot", 46, 184.0, "c:ingots/crimson_steel"));
                    metals.add(new CraftingEntry("silentgear:azure_silver_ingot", 48, 192.0, "c:ingots/azure_silver"));
                    metals.add(new CraftingEntry("silentgear:azure_electrum_ingot", 49, 196.0, "c:ingots/azure_electrum"));
                    metals.add(new CraftingEntry("silentgear:tyrian_steel_ingot", 50, 200.0, "c:ingots/tyrian_steel"));
                }
                CraftingConfig.registerMultipleConfigs(builder, "crafting_metal_materials", "Metals", metals);

                //Coal/Misc?
                List<CraftingEntry> misc = new ArrayList<>();
                misc.add(new CraftingEntry("minecraft:coal_block", 3, 6.0, "minecraft:coals"));
                CraftingConfig.registerMultipleConfigs(builder, "crafting_misc_materials", "Misc", misc);

                //Gems
                List<CraftingEntry> gems = new ArrayList<>();
                gems.add(new CraftingEntry("silentgear:bort", 15, 60.0, "c:gems/bort"));
                gems.add(new CraftingEntry("minecraft:diamond", 30, 120.0, "c:gems/diamond"));
                CraftingConfig.registerMultipleConfigs(builder, "crafting_gem_materials", "Gems", gems);
            });
        }

        public static void registerToolConfigs(ModConfigSpec.Builder builder) {
            ConfigRegister.registerCategory(builder, "tools_config", "Tools", "Settings for Tools", () -> {
                boolean hasSilentGear = ModList.get().isLoaded("silentgear");
                boolean hasSilentGems = ModList.get().isLoaded("silentgems");
                // Wooden Tools
                List<ToolEntry> woodTools = new ArrayList<>();
                woodTools.add(new ToolEntry("minecraft:wooden_pickaxe", 1));
                woodTools.add(new ToolEntry("wood", 1, true));
                ToolConfig.registerMultipleConfigs(builder, "wood_tools", "Wood Tools", woodTools);

                // Stone Tools
                List<ToolEntry> stoneTools = new ArrayList<>();
                stoneTools.add(new ToolEntry("minecraft:stone_pickaxe", 5));
                stoneTools.add(new ToolEntry("stone", 5, true));
                ToolConfig.registerMultipleConfigs(builder, "stone_tools", "Stone Tools", stoneTools);

                // Iron Tools
                List<ToolEntry> ironTools = new ArrayList<>();
                ironTools.add(new ToolEntry("minecraft:iron_pickaxe", 15));
                ironTools.add(new ToolEntry("iron", 15, true));
                ToolConfig.registerMultipleConfigs(builder, "iron_tools", "Iron Tools", ironTools);

                // Bronze Tools
                List<ToolEntry> bronzeTools = new ArrayList<>();
                bronzeTools.add(new ToolEntry("minecraft:bronze_pickaxe", 15));
                bronzeTools.add(new ToolEntry("bronze", 15, true));
                ToolConfig.registerMultipleConfigs(builder, "bronze_tools", "Bronze Tools", bronzeTools);

                // Gold Tools
                List<ToolEntry> goldTools = new ArrayList<>();
                goldTools.add(new ToolEntry("minecraft:golden_pickaxe", 15));
                goldTools.add(new ToolEntry("gold", 15, true));
                ToolConfig.registerMultipleConfigs(builder, "gold_tools", "Gold Tools", goldTools);

                // Diamond Tools
                List<ToolEntry> diamondTools = new ArrayList<>();
                diamondTools.add(new ToolEntry("minecraft:diamond_pickaxe", 30));
                diamondTools.add(new ToolEntry("diamond", 30, true));
                ToolConfig.registerMultipleConfigs(builder, "diamond_tools", "Diamond Tools", diamondTools);

                // Netherite Tools
                List<ToolEntry> netheriteTools = new ArrayList<>();
                netheriteTools.add(new ToolEntry("minecraft:netherite_pickaxe", 40));
                netheriteTools.add(new ToolEntry("netherite", 40, true));
                ToolConfig.registerMultipleConfigs(builder, "netherite_tools", "Netherite Tools", netheriteTools);


                if (hasSilentGear) {
                    // Terracotta Tools
                    List<ToolEntry> terracottaTools = new ArrayList<>();
                    terracottaTools.add(new ToolEntry("terracotta", 4, true));
                    ToolConfig.registerMultipleConfigs(builder, "terracotta_tools", "Terracotta Tools", terracottaTools);

                    // Flint Tools
                    List<ToolEntry> flintTools = new ArrayList<>();
                    flintTools.add(new ToolEntry("flint", 7, true));
                    ToolConfig.registerMultipleConfigs(builder, "flint_tools", "Flint Tools", flintTools);

                    // Copper Tools
                    List<ToolEntry> copperTools = new ArrayList<>();
                    copperTools.add(new ToolEntry("minecraft:copper_pickaxe", 10));
                    copperTools.add(new ToolEntry("copper", 10, true));
                    ToolConfig.registerMultipleConfigs(builder, "copper_tools", "Copper Tools", copperTools);
                    // Blaze Gold Tools
                    List<ToolEntry> blazeGoldTools = new ArrayList<>();
                    blazeGoldTools.add(new ToolEntry("blaze_gold", 20, true));
                    ToolConfig.registerMultipleConfigs(builder, "blaze_gold_tools", "Blaze Gold Tools", blazeGoldTools);


                    // Emerald Tools
                    List<ToolEntry> emeraldTools = new ArrayList<>();
                    emeraldTools.add(new ToolEntry("emerald", 25, true));
                    ToolConfig.registerMultipleConfigs(builder, "emerald_tools", "Emerald Tools", emeraldTools);


                    // Dimerald Tools
                    List<ToolEntry> dimeraldTools = new ArrayList<>();
                    dimeraldTools.add(new ToolEntry("dimerald", 32, true));
                    ToolConfig.registerMultipleConfigs(builder, "dimerald_tools", "Dimerald Tools", dimeraldTools);

                    // Crimson Iron Tools
                    List<ToolEntry> crimsonIronTools = new ArrayList<>();
                    crimsonIronTools.add(new ToolEntry("crimson_iron", 43, true));
                    ToolConfig.registerMultipleConfigs(builder, "crimson_iron_tools", "Crimson Iron Tools", crimsonIronTools);

                    // Crimson Steel Tools
                    List<ToolEntry> crimsonSteelTools = new ArrayList<>();
                    crimsonSteelTools.add(new ToolEntry("crimson_steel", 46, true));
                    ToolConfig.registerMultipleConfigs(builder, "crimson_steel_tools", "Crimson Steel Tools", crimsonSteelTools);

                    // Azure Silver Tools
                    List<ToolEntry> azureSilverTools = new ArrayList<>();
                    azureSilverTools.add(new ToolEntry("azure_silver", 48, true));
                    ToolConfig.registerMultipleConfigs(builder, "azure_silver_tools", "Azure Silver Tools", azureSilverTools);

                    // Azure Electrum Tools
                    List<ToolEntry> azureElectrumTools = new ArrayList<>();
                    azureElectrumTools.add(new ToolEntry("azure_electrum", 49, true));
                    ToolConfig.registerMultipleConfigs(builder, "azure_electrum_tools", "Azure Electrum Tools", azureElectrumTools);

                    // Tyrian Steel Tools
                    List<ToolEntry> tyrianSteelTools = new ArrayList<>();
                    tyrianSteelTools.add(new ToolEntry("tyrian_steel", 50, true));
                    ToolConfig.registerMultipleConfigs(builder, "tyrian_steel_tools", "Tyrian Steel Tools", tyrianSteelTools);
                }
            });
        }

        public static void registerMiningConfigs(ModConfigSpec.Builder builder) {
            // Start of Mining category
            ConfigRegister.registerCategory(builder, "mining_config", "Mining", "Settings for Mining", () -> {

                // Stones
                List<MiningEntry> stones = new ArrayList<>();
                stones.add(new MiningEntry("minecraft:stone", 1, 4.0, "minecraft:stones"));
                stones.add(new MiningEntry("minecraft:cobblestone", 1, 4.0, "c:cobblestones"));
                stones.add(new MiningEntry("minecraft:stone_bricks", 2, 8.0, "minecraft:stone_bricks"));
                stones.add(new MiningEntry("minecraft:terracotta", 4, 16.0, "minecraft:terracotta"));
                stones.add(new MiningEntry("minecraft:end_stone", 40, 10.0, "minecraft:end_stones"));
                MiningConfig.registerMultipleConfigs(builder, "mining_stone_ores", "Stone", stones);

                // Metals
                List<MiningEntry> metals = new ArrayList<>();
                metals.add(new MiningEntry("minecraft:iron_ore", 15, 60.0, "minecraft:iron_ores"));
                metals.add(new MiningEntry("minecraft:copper_ore", 10, 40.0, "minecraft:copper_ores"));
                metals.add(new MiningEntry("minecraft:gold_ore", 20, 80.0, "minecraft:gold_ores"));
                metals.add(new MiningEntry("minecraft:ancient_debris", 40, 160.0, "c:ores/netherite_scrap"));
                if (ModList.get().isLoaded("silentgear")) {
                    metals.add(new MiningEntry("silentgear:crimson_iron_ore", 43, 172.0, "c:ores/crimson_iron"));
                    metals.add(new MiningEntry("silentgear:azure_silver_ore", 48, 192.0, "c:ores/azure_silver"));
                }
                MiningConfig.registerMultipleConfigs(builder, "mining_metal_ores", "Metals", metals);

                // Gems
                List<MiningEntry> gems = new ArrayList<>();
                if (ModList.get().isLoaded("silentgear")) {
                    gems.add(new MiningEntry("silentgear:bort_ore", 15, 60.0, "c:ores/bort"));
                }
                if (ModList.get().isLoaded("silentgems")) {
                    gems.add(new MiningEntry("silentgems:silent_gems", 15, 60.0, "silentgems:ores"));
                }
                gems.add(new MiningEntry("minecraft:lapis_ore", 16, 64.0, "minecraft:lapis_ores"));
                gems.add(new MiningEntry("minecraft:diamond_ore", 30, 120.0, "minecraft:diamond_ores"));
                gems.add(new MiningEntry("minecraft:emerald_ore", 35, 140.0, "minecraft:emerald_ores"));
                MiningConfig.registerMultipleConfigs(builder, "mining_gem_ores", "Gems", gems);

                // Misc
                List<MiningEntry> ores = new ArrayList<>();
                ores.add(new MiningEntry("minecraft:coal_ore", 3, 12.0, "minecraft:coal_ores"));
                ores.add(new MiningEntry("minecraft:redstone_ore", 13, 52.0, "minecraft:redstone_ores"));
                ores.add(new MiningEntry("minecraft:nether_quartz_ore", 18, 72.0, "c:ores/quartz"));
                MiningConfig.registerMultipleConfigs(builder, "mining_misc_ores", "Misc", ores);
            });
        }
    }

    public static final class Client {
        static final ModConfigSpec SPEC;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
            SPEC = builder.build();
        }
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Reloading event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == Config.Server.SPEC) {
            System.out.println("Reloaded on server");
        }
        if (config.getSpec() == Config.Client.SPEC) {
            System.out.println("Reloaded on client");
        }
    }
}