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
                foods.add(new SmeltingEntry("minecraft:cooked_salmon", 1, "minecraft:salmon"));
                foods.add(new SmeltingEntry("minecraft:cooked_cod", 1, "minecraft:cod"));
                foods.add(new SmeltingEntry("minecraft:baked_potato", 1, "minecraft:potato"));
                foods.add(new SmeltingEntry("minecraft:cooked_beef", 1, "minecraft:beef"));
                foods.add(new SmeltingEntry("minecraft:cooked_mutton", 1, "minecraft:mutton"));
                foods.add(new SmeltingEntry("minecraft:cooked_porkchop", 1, "minecraft:porkchop"));
                foods.add(new SmeltingEntry("minecraft:cooked_chicken", 1, "minecraft:chicken"));
                foods.add(new SmeltingEntry("minecraft:cooked_rabbit", 1, "minecraft:rabbit"));
                SmeltingConfig.registerMultipleConfigs(builder, "smelting_food", "Food", foods);

                List<SmeltingEntry> metals = new ArrayList<>();
                metals.add(new SmeltingEntry("minecraft:copper_ingot", 10, "minecraft:raw_copper"));
                metals.add(new SmeltingEntry("minecraft:iron_ingot", 15, "minecraft:raw_iron"));
                metals.add(new SmeltingEntry("minecraft:gold_ingot", 25, "minecraft:raw_gold"));
                metals.add(new SmeltingEntry("minecraft:diamond", 30, "minecraft:diamond_ore"));
                metals.add(new SmeltingEntry("minecraft:netherite_scrap", 40, "minecraft:ancient_debris"));
                if (ModList.get().isLoaded("silentgear")) {
                    metals.add(new SmeltingEntry("silentgear:crimson_iron_ingot", 43, "silentgear:raw_crimson_iron"));
                    metals.add(new SmeltingEntry("silentgear:azure_silver_ingot", 48, "silentgear:raw_azure_silver"));
                }
                SmeltingConfig.registerMultipleConfigs(builder, "smelting_metals", "Metals", metals);


                List<SmeltingEntry> materials = new ArrayList<>();
                materials.add(new SmeltingEntry("minecraft:charcoal", 1, "minecraft:oak_logs"));
                materials.add(new SmeltingEntry("minecraft:glass", 2, "minecraft:sand"));
                materials.add(new SmeltingEntry("minecraft:bricks", 3, "minecraft:clay_ball"));
                materials.add(new SmeltingEntry("minecraft:stone", 5, "minecraft:cobblestone"));
                materials.add(new SmeltingEntry("minecraft:smooth_stone", 8, "minecraft:stone"));
                SmeltingConfig.registerMultipleConfigs(builder, "smelting_general", "General", materials);
            });
        }

        public static void registerCraftingConfigs(ModConfigSpec.Builder builder) {
            boolean hasSilentGear = ModList.get().isLoaded("silentgear");
            boolean hasSilentGems = ModList.get().isLoaded("silentgems");
            ConfigRegister.registerCategory(builder, "crafting_config", "Crafting", "Settings for Crafting", () -> {

                //Wood
                List<CraftingEntry> wood = new ArrayList<>();
                wood.add(new CraftingEntry("minecraft:logs", 1, "minecraft:logs"));
                wood.add(new CraftingEntry("minecraft:planks", 1, 1.0, "minecraft:planks"));
                wood.add(new CraftingEntry("minecraft:stick", 1, 0.25, "c:rods/wooden"));
                CraftingConfig.registerMultipleConfigs(builder, "crafting_wood_materials", "Wood", wood);

                //Stone
                List<CraftingEntry> stone = new ArrayList<>();
                stone.add(new CraftingEntry("minecraft:slabs", 1, "minecraft:slabs"));
                stone.add(new CraftingEntry("minecraft:terracotta", 4, "minecraft:terracotta"));
                stone.add(new CraftingEntry("minecraft:stone", 5, "c:stones", "stone_bricks"));
                stone.add(new CraftingEntry("minecraft:cobblestone", 5, "c:cobblestones", "minecraft:stone_crafting_materials"));
                CraftingConfig.registerMultipleConfigs(builder, "crafting_stone_materials", "Stone", stone);

                //Metals
                List<CraftingEntry> metals = new ArrayList<>();
                metals.add(new CraftingEntry("minecraft:iron_ingot", 15, "c:ingots/iron"));
                metals.add(new CraftingEntry("minecraft:copper_ingot", 10, "c:ingots/copper"));
                metals.add(new CraftingEntry("minecraft:gold_ingot", 20, "c:ingots/gold"));
                metals.add(new CraftingEntry("minecraft:netherite_ingot", 40, "c:ingots/netherite"));
                metals.add(new CraftingEntry("minecraft:netherite_scrap", 40, "c:ingots/netherite"));
                if (hasSilentGear) {
                    metals.add(new CraftingEntry("silentgear:blaze_gold", 25, "c:ingots/blaze_gold"));
                    metals.add(new CraftingEntry("silentgear:crimson_iron", 43, "c:ingots/crimson_iron"));
                    metals.add(new CraftingEntry("silentgear:crimson_steel_ingot", 46, "c:ingots/crimson_steel"));
                    metals.add(new CraftingEntry("silentgear:azure_silver_ingot", 48, "c:ingots/azure_silver"));
                    metals.add(new CraftingEntry("silentgear:azure_electrum_ingot", 49, "c:ingots/azure_electrum"));
                    metals.add(new CraftingEntry("silentgear:tyrian_steel_ingot", 50, "c:ingots/tyrian_steel"));
                }
                CraftingConfig.registerMultipleConfigs(builder, "crafting_metal_materials", "Metals", metals);

                //Coal/Misc?
                List<CraftingEntry> misc = new ArrayList<>();
                misc.add(new CraftingEntry("minecraft:wheat", 1, 1.0, "c:crops"));
                misc.add(new CraftingEntry("minecraft:coal_block", 3, "minecraft:coals"));
                CraftingConfig.registerMultipleConfigs(builder, "crafting_misc_materials", "Misc", misc);

                //Gems
                List<CraftingEntry> gems = new ArrayList<>();
                gems.add(new CraftingEntry("silentgear:bort", 15, "c:gems/bort"));
                gems.add(new CraftingEntry("minecraft:diamond", 30, "c:gems/diamond"));
                CraftingConfig.registerMultipleConfigs(builder, "crafting_gem_materials", "Gems", gems);
                if (hasSilentGems) {
                    List<CraftingEntry> silentgems = new ArrayList<>();
                    //ruby
                    silentgems.add(new CraftingEntry("silentgems:ruby", 15));
                    //sapphire
                    silentgems.add(new CraftingEntry("silentgems:sapphire", 15));
                    //iolite
                    silentgems.add(new CraftingEntry("silentgems:iolite", 15));
                    //modavite
                    silentgems.add(new CraftingEntry("silentgems:modavite", 15));
                    //peridot
                    silentgems.add(new CraftingEntry("silentgems:peridot", 15));
                    //carnelian
                    silentgems.add(new CraftingEntry("silentgems:carnelian", 17));
                    //topaz
                    silentgems.add(new CraftingEntry("silentgems:topaz", 19));
                    //citrine
                    silentgems.add(new CraftingEntry("silentgems:citrine", 22));
                    //citrine
                    silentgems.add(new CraftingEntry("silentgems:heliodor", 25));
                    //turquoise
                    silentgems.add(new CraftingEntry("silentgems:turquoise", 30));
                    //alexandrite
                    silentgems.add(new CraftingEntry("silentgems:alexandrite", 30));
                    //ammolite
                    silentgems.add(new CraftingEntry("silentgems:ammolite", 30));
                    //black_diamond
                    silentgems.add(new CraftingEntry("silentgems:black_diamond", 32));
                    //kyanite
                    silentgems.add(new CraftingEntry("silentgems:kyanite", 40));
                    //rose_quartz
                    silentgems.add(new CraftingEntry("silentgems:rose_quartz", 40));
                    //kyanite
                    silentgems.add(new CraftingEntry("silentgems:kyanite", 40));
                    //white_diamond
                    silentgems.add(new CraftingEntry("silentgems:white_diamond", 45));
                    CraftingConfig.registerMultipleConfigs(builder, "crafting_silentgems", "Silent Gems", silentgems);
                }
                if (ModList.get().isLoaded("iceandfire")) {
                    List<CraftingEntry> iceandfire = new ArrayList<>();
                    //ruby
                    iceandfire.add(new CraftingEntry("iceandfire:witherbone", 15));
                    iceandfire.add(new CraftingEntry("iceandfire:dragonbone", 20));
                    iceandfire.add(new CraftingEntry("iceandfire:serperentscales", 20, "iceandfire:scales/sea_serpent"));
                    iceandfire.add(new CraftingEntry("iceandfire:dragonscales", 25, "iceandfire:scales/dragon"));
                    iceandfire.add(new CraftingEntry("iceandfire:dragonsteel_fire_ingot", 45));
                    iceandfire.add(new CraftingEntry("iceandfire:dragonsteel_ice_ingot", 45));
                    iceandfire.add(new CraftingEntry("iceandfire:dragonsteel_lightning_ingot", 45));
                    CraftingConfig.registerMultipleConfigs(builder, "crafting_iceandfire", "Ice & Fire", iceandfire);
                }
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
                    blazeGoldTools.add(new ToolEntry("blaze_gold", 25, true));
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
                if (hasSilentGems) {
                    List<ToolEntry> silentgems = new ArrayList<>();
                    //ruby
                    silentgems.add(new ToolEntry("silentgems:ruby", 15, true));
                    //sapphire
                    silentgems.add(new ToolEntry("silentgems:sapphire", 15, true));
                    //iolite
                    silentgems.add(new ToolEntry("silentgems:iolite", 15, true));
                    //modavite
                    silentgems.add(new ToolEntry("silentgems:modavite", 15, true));
                    //peridot
                    silentgems.add(new ToolEntry("silentgems:peridot", 15, true));
                    //carnelian
                    silentgems.add(new ToolEntry("silentgems:carnelian", 17, true));
                    //topaz
                    silentgems.add(new ToolEntry("silentgems:topaz", 19, true));
                    //citrine
                    silentgems.add(new ToolEntry("silentgems:citrine", 22, true));
                    //citrine
                    silentgems.add(new ToolEntry("silentgems:heliodor", 25, true));
                    //turquoise
                    silentgems.add(new ToolEntry("silentgems:turquoise", 30, true));
                    //alexandrite
                    silentgems.add(new ToolEntry("silentgems:alexandrite", 30, true));
                    //ammolite
                    silentgems.add(new ToolEntry("silentgems:ammolite", 30, true));
                    //black_diamond
                    silentgems.add(new ToolEntry("silentgems:black_diamond", 32, true));
                    //kyanite
                    silentgems.add(new ToolEntry("silentgems:kyanite", 40, true));
                    //rose_quartz
                    silentgems.add(new ToolEntry("silentgems:rose_quartz", 40, true));
                    //kyanite
                    silentgems.add(new ToolEntry("silentgems:kyanite", 40, true));
                    //white_diamond
                    silentgems.add(new ToolEntry("silentgems:white_diamond", 45, true));
                    ToolConfig.registerMultipleConfigs(builder, "tool_silentgems", "Silent Gems", silentgems);
                }
            });
        }

        public static void registerMiningConfigs(ModConfigSpec.Builder builder) {
            // Start of Mining category
            boolean hasSilentGear = ModList.get().isLoaded("silentgear");
            boolean hasSilentGems = ModList.get().isLoaded("silentgems");
            ConfigRegister.registerCategory(builder, "mining_config", "Mining", "Settings for Mining", () -> {

                // Stones
                List<MiningEntry> stones = new ArrayList<>();
                stones.add(new MiningEntry("minecraft:stone", 1, "minecraft:stones", "end_stones", "c:cobblestones", "c:stones"));
                stones.add(new MiningEntry("minecraft:stone_bricks", 2, "minecraft:stone_bricks"));
                stones.add(new MiningEntry("minecraft:terracotta", 4, "minecraft:terracotta"));
                MiningConfig.registerMultipleConfigs(builder, "mining_stone_ores", "Stone", stones);

                // Metals
                List<MiningEntry> metals = new ArrayList<>();
                metals.add(new MiningEntry("minecraft:iron_ore", 15, "minecraft:iron_ores"));
                metals.add(new MiningEntry("minecraft:copper_ore", 10, "minecraft:copper_ores"));
                metals.add(new MiningEntry("minecraft:gold_ore", 20, "minecraft:gold_ores"));
                metals.add(new MiningEntry("minecraft:ancient_debris", 40, "c:ores/netherite_scrap"));
                if (hasSilentGear) {
                    metals.add(new MiningEntry("silentgear:crimson_iron_ore", 43, "c:ores/crimson_iron"));
                    metals.add(new MiningEntry("silentgear:azure_silver_ore", 48, "c:ores/azure_silver"));
                }
                MiningConfig.registerMultipleConfigs(builder, "mining_metal_ores", "Metals", metals);

                // Gems
                List<MiningEntry> gems = new ArrayList<>();
                if (hasSilentGear) {
                    gems.add(new MiningEntry("silentgear:bort_ore", 15, "c:ores/bort"));
                }
                gems.add(new MiningEntry("minecraft:lapis_ore", 16, "minecraft:lapis_ores"));
                gems.add(new MiningEntry("minecraft:diamond_ore", 30, "minecraft:diamond_ores"));
                gems.add(new MiningEntry("minecraft:emerald_ore", 35, "minecraft:emerald_ores"));
                MiningConfig.registerMultipleConfigs(builder, "mining_gem_ores", "Gems", gems);

                // Misc
                List<MiningEntry> ores = new ArrayList<>();
                ores.add(new MiningEntry("minecraft:coal_ore", 3, "minecraft:coal_ores"));
                ores.add(new MiningEntry("minecraft:redstone_ore", 13, "minecraft:redstone_ores"));
                ores.add(new MiningEntry("minecraft:nether_quartz_ore", 18, "c:ores/quartz"));
                MiningConfig.registerMultipleConfigs(builder, "mining_misc_ores", "Misc", ores);
                if (hasSilentGems) {
                    List<MiningEntry> silentgems = new ArrayList<>();
                    //ruby
                    silentgems.add(new MiningEntry("silentgems:ruby_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:deepslate_ruby_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:nether_ruby_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_ruby_ore", 40));

                    //sapphire
                    silentgems.add(new MiningEntry("silentgems:sapphire_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:deepslate_sapphire_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:nether_sapphire_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_sapphire_ore", 40));

                    //iolite
                    silentgems.add(new MiningEntry("silentgems:iolite_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:deepslate_iolite_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:nether_iolite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_iolite_ore", 40));

                    //modavite
                    silentgems.add(new MiningEntry("silentgems:modavite_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:deepslate_modavite_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:nether_modavite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_modavite_ore", 40));

                    //peridot
                    silentgems.add(new MiningEntry("silentgems:peridot_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:deepslate_peridot_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:nether_peridot_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_peridot_ore", 40));

                    //carnelian
                    silentgems.add(new MiningEntry("silentgems:carnelian_ore", 17));
                    silentgems.add(new MiningEntry("silentgems:deepslate_carnelian_ore", 17));
                    silentgems.add(new MiningEntry("silentgems:nether_carnelian_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_carnelian_ore", 40));

                    //topaz
                    silentgems.add(new MiningEntry("silentgems:topaz_ore", 19));
                    silentgems.add(new MiningEntry("silentgems:deepslate_topaz_ore", 19));
                    silentgems.add(new MiningEntry("silentgems:nether_topaz_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_topaz_ore", 40));

                    //citrine
                    silentgems.add(new MiningEntry("silentgems:citrine_ore", 22));
                    silentgems.add(new MiningEntry("silentgems:deepslate_citrine_ore", 22));
                    silentgems.add(new MiningEntry("silentgems:nether_citrine_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_citrine_ore", 40));

                    //citrine
                    silentgems.add(new MiningEntry("silentgems:heliodor_ore", 25));
                    silentgems.add(new MiningEntry("silentgems:deepslate_heliodor_ore", 25));
                    silentgems.add(new MiningEntry("silentgems:nether_heliodor_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_heliodor_ore", 40));

                    //turquoise
                    silentgems.add(new MiningEntry("silentgems:turquoise_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:deepslate_turquoise_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:nether_turquoise_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_turquoise_ore", 40));

                    //alexandrite
                    silentgems.add(new MiningEntry("silentgems:alexandrite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:deepslate_alexandrite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:nether_alexandrite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_alexandrite_ore", 40));

                    //ammolite
                    silentgems.add(new MiningEntry("silentgems:ammolite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:deepslate_ammolite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:nether_ammolite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_ammolite_ore", 40));

                    //black_diamond
                    silentgems.add(new MiningEntry("silentgems:black_diamond_ore", 32));
                    silentgems.add(new MiningEntry("silentgems:deepslate_black_diamond_ore", 32));
                    silentgems.add(new MiningEntry("silentgems:nether_black_diamond_ore", 32));
                    silentgems.add(new MiningEntry("silentgems:end_black_diamond_ore", 40));

                    //kyanite
                    silentgems.add(new MiningEntry("silentgems:kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:deepslate_kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:nether_kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:end_kyanite_ore", 40));

                    //rose_quartz
                    silentgems.add(new MiningEntry("silentgems:rose_quartz_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:deepslate_rose_quartz_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:nether_rose_quartz_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:end_rose_quartz_ore", 40));

                    //kyanite
                    silentgems.add(new MiningEntry("silentgems:kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:deepslate_kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:nether_kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:end_kyanite_ore", 40));

                    //white_diamond
                    silentgems.add(new MiningEntry("silentgems:white_diamond_ore", 45));
                    silentgems.add(new MiningEntry("silentgems:deepslate_white_diamond_ore", 45));
                    silentgems.add(new MiningEntry("silentgems:nether_white_diamond_ore", 45));
                    silentgems.add(new MiningEntry("silentgems:end_white_diamond_ore", 45));
                    MiningConfig.registerMultipleConfigs(builder, "mining_silentgems", "Silent Gems", silentgems);
                }
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