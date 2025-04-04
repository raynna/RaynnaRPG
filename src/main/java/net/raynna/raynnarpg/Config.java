package net.raynna.raynnarpg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.raynna.raynnarpg.config.*;
import net.raynna.raynnarpg.config.combat.CombatConfig;
import net.raynna.raynnarpg.config.combat.CombatEntry;
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

    public static final int SERVER_VERSION = 1;
    public static final int CLIENT_VERSION = 1;

    public static final class Server {

        public static ModConfigSpec SPEC;
        public static final ModConfigSpec.IntValue SERVER_CONFIG_VERSION;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
            SERVER_CONFIG_VERSION = builder.translation("Server Config Version: ").comment("DO NOT CHANGE. Used for tracking config updates.").defineInRange("config_version", SERVER_VERSION, 1, Integer.MAX_VALUE);
            registerCombatConfigs(builder);
            registerToolConfigs(builder);
            registerCraftingConfigs(builder);
            registerMiningConfigs(builder);
            registerSmeltingConfigs(builder);
            SPEC = builder.build();
        }

        public static void registerCombatConfigs(ModConfigSpec.Builder builder) {
            boolean hasSilentGear = ModList.get().isLoaded("silentgear");
            boolean hasSilentGems = ModList.get().isLoaded("silentgems");
            boolean hasIceAndFire = ModList.get().isLoaded("iceandfire");
            ConfigRegister.registerCategory(builder, "combat_config", "Combat", "Settings for Combat", () -> {

                List<CombatEntry> weapons = new ArrayList<>();
                weapons.add(new CombatEntry("minecraft:wooden_sword", 1, false));
                weapons.add(new CombatEntry("minecraft:wooden_axe", 1, false));
                weapons.add(new CombatEntry("minecraft:stone_sword", 5, false));
                weapons.add(new CombatEntry("minecraft:stone_axe", 5, false));
                weapons.add(new CombatEntry("minecraft:iron_sword", 15, false));
                weapons.add(new CombatEntry("minecraft:iron_axe", 15, false));
                weapons.add(new CombatEntry("minecraft:golden_sword", 15, false));
                weapons.add(new CombatEntry("minecraft:golden_axe", 15, false));
                weapons.add(new CombatEntry("minecraft:diamond_sword", 30, false));
                weapons.add(new CombatEntry("minecraft:diamond_axe", 30, false));
                weapons.add(new CombatEntry("minecraft:netherite_sword", 40, false));
                weapons.add(new CombatEntry("minecraft:netherite_axe", 40, false));
                CombatConfig.registerMultipleConfigs(builder, "combat_weapons", "Weapons", weapons);

                List<CombatEntry> gears = new ArrayList<>();
                gears.add(new CombatEntry("minecraft:leather_helmet", 1));
                gears.add(new CombatEntry("minecraft:leather_chestplate", 1));
                gears.add(new CombatEntry("minecraft:leather_leggings", 1));
                gears.add(new CombatEntry("minecraft:leather_boots", 1));
                gears.add(new CombatEntry("minecraft:stone_helmet", 5));
                gears.add(new CombatEntry("minecraft:stone_chestplate", 5));
                gears.add(new CombatEntry("minecraft:stone_leggings", 5));
                gears.add(new CombatEntry("minecraft:stone_boots", 5));
                gears.add(new CombatEntry("minecraft:chainmail_helmet", 12));
                gears.add(new CombatEntry("minecraft:chainmail_chestplate", 12));
                gears.add(new CombatEntry("minecraft:chainmail_leggings", 12));
                gears.add(new CombatEntry("minecraft:chainmail_boots", 12));
                gears.add(new CombatEntry("minecraft:iron_helmet", 15));
                gears.add(new CombatEntry("minecraft:iron_chestplate", 15));
                gears.add(new CombatEntry("minecraft:iron_leggings", 15));
                gears.add(new CombatEntry("minecraft:iron_boots", 15));
                gears.add(new CombatEntry("minecraft:golden_helmet", 15));
                gears.add(new CombatEntry("minecraft:golden_chestplate", 15));
                gears.add(new CombatEntry("minecraft:golden_leggings", 15));
                gears.add(new CombatEntry("minecraft:golden_boots", 15));
                gears.add(new CombatEntry("minecraft:diamond_helmet", 30));
                gears.add(new CombatEntry("minecraft:diamond_chestplate", 30));
                gears.add(new CombatEntry("minecraft:diamond_leggings", 30));
                gears.add(new CombatEntry("minecraft:diamond_boots", 30));
                gears.add(new CombatEntry("minecraft:netherite_helmet", 40));
                gears.add(new CombatEntry("minecraft:netherite_chestplate", 40));
                gears.add(new CombatEntry("minecraft:netherite_leggings", 40));
                gears.add(new CombatEntry("minecraft:netherite_boots", 40));
                CombatConfig.registerMultipleConfigs(builder, "combat_armour", "Armours", gears);

                if (hasSilentGear) {
                    List<CombatEntry> silentgear_weapons = new ArrayList<>();
                    silentgear_weapons.add(new CombatEntry("tier:wood", 1, false));
                    silentgear_weapons.add(new CombatEntry("tier:terracotta", 3, false));
                    silentgear_weapons.add(new CombatEntry("tier:stone", 5, false));
                    silentgear_weapons.add(new CombatEntry("tier:flint", 7, false));
                    silentgear_weapons.add(new CombatEntry("tier:copper", 10, false));
                    silentgear_weapons.add(new CombatEntry("tier:iron", 15, false));
                    silentgear_weapons.add(new CombatEntry("tier:gold", 15, false));
                    silentgear_weapons.add(new CombatEntry("tier:bronze", 17, false));
                    silentgear_weapons.add(new CombatEntry("tier:blaze_gold", 20, false));
                    silentgear_weapons.add(new CombatEntry("tier:emerald", 25, false));
                    silentgear_weapons.add(new CombatEntry("tier:diamond", 30, false));
                    silentgear_weapons.add(new CombatEntry("tier:dimerald", 32, false));
                    silentgear_weapons.add(new CombatEntry("tier:netherite", 40, false));
                    silentgear_weapons.add(new CombatEntry("tier:crimson_iron", 36, false));
                    silentgear_weapons.add(new CombatEntry("tier:crimson_steel", 45, false));
                    silentgear_weapons.add(new CombatEntry("tier:azure_silver", 47, false));
                    silentgear_weapons.add(new CombatEntry("tier:azure_electrum", 49, false));
                    silentgear_weapons.add(new CombatEntry("tier:tyrian_steel", 50, false));
                    CombatConfig.registerMultipleConfigs(builder, "silentgear_weapons", "Silentgear Weapons", silentgear_weapons);

                    List<CombatEntry> silentgear_armours = new ArrayList<>();
                    silentgear_armours.add(new CombatEntry("tier:wood", 1));
                    silentgear_armours.add(new CombatEntry("tier:terracotta", 3));
                    silentgear_armours.add(new CombatEntry("tier:stone", 5));
                    silentgear_armours.add(new CombatEntry("tier:flint", 7));
                    silentgear_armours.add(new CombatEntry("tier:copper", 10));
                    silentgear_armours.add(new CombatEntry("tier:iron", 15));
                    silentgear_armours.add(new CombatEntry("tier:gold", 15));
                    silentgear_armours.add(new CombatEntry("tier:bronze", 17));
                    silentgear_armours.add(new CombatEntry("tier:blaze_gold", 20));
                    silentgear_armours.add(new CombatEntry("tier:emerald", 25));
                    silentgear_armours.add(new CombatEntry("tier:diamond", 30));
                    silentgear_armours.add(new CombatEntry("tier:dimerald", 32));
                    silentgear_armours.add(new CombatEntry("tier:netherite", 40));
                    silentgear_armours.add(new CombatEntry("tier:crimson_iron", 36));
                    silentgear_armours.add(new CombatEntry("tier:crimson_steel", 45));
                    silentgear_armours.add(new CombatEntry("tier:azure_silver", 47));
                    silentgear_armours.add(new CombatEntry("tier:azure_electrum", 49));
                    silentgear_armours.add(new CombatEntry("tier:tyrian_steel", 50));
                    CombatConfig.registerMultipleConfigs(builder, "silentgear_armours", "Silentgear Armours", silentgear_armours);
                }

                if (hasSilentGems) {
                    List<CombatEntry> silentgem_weapons = new ArrayList<>();
                    silentgem_weapons.add(new CombatEntry("tier:ruby", 15, false));
                    silentgem_weapons.add(new CombatEntry("tier:sapphire", 15, false));
                    silentgem_weapons.add(new CombatEntry("tier:iolite", 15, false));
                    silentgem_weapons.add(new CombatEntry("tier:moldavite", 15, false));
                    silentgem_weapons.add(new CombatEntry("tier:peridot", 15, false));
                    silentgem_weapons.add(new CombatEntry("tier:carnelian", 17, false));
                    silentgem_weapons.add(new CombatEntry("tier:topaz", 19, false));
                    silentgem_weapons.add(new CombatEntry("tier:citrine", 22, false));
                    silentgem_weapons.add(new CombatEntry("tier:heliodor", 25, false));
                    silentgem_weapons.add(new CombatEntry("tier:turquoise", 30, false));
                    silentgem_weapons.add(new CombatEntry("tier:alexandrite", 30, false));
                    silentgem_weapons.add(new CombatEntry("tier:ammolite", 30, false));
                    silentgem_weapons.add(new CombatEntry("tier:black_diamond", 32, false));
                    silentgem_weapons.add(new CombatEntry("tier:kyanite", 40, false));
                    silentgem_weapons.add(new CombatEntry("tier:rose_quartz", 40, false));
                    silentgem_weapons.add(new CombatEntry("tier:kyanite", 40, false));
                    silentgem_weapons.add(new CombatEntry("tier:white_diamond", 45, false));
                    CombatConfig.registerMultipleConfigs(builder, "silentgem_weapons", "Silent gem Weapons", silentgem_weapons);

                    List<CombatEntry> silentgem_armours = new ArrayList<>();
                    silentgem_armours.add(new CombatEntry("tier:ruby", 15));
                    silentgem_armours.add(new CombatEntry("tier:sapphire", 15));
                    silentgem_armours.add(new CombatEntry("tier:iolite", 15));
                    silentgem_armours.add(new CombatEntry("tier:moldavite", 15));
                    silentgem_armours.add(new CombatEntry("tier:peridot", 15));
                    silentgem_armours.add(new CombatEntry("tier:carnelian", 17));
                    silentgem_armours.add(new CombatEntry("tier:topaz", 19));
                    silentgem_armours.add(new CombatEntry("tier:citrine", 22));
                    silentgem_armours.add(new CombatEntry("tier:heliodor", 25));
                    silentgem_armours.add(new CombatEntry("tier:turquoise", 30));
                    silentgem_armours.add(new CombatEntry("tier:alexandrite", 30));
                    silentgem_armours.add(new CombatEntry("tier:ammolite", 30));
                    silentgem_armours.add(new CombatEntry("tier:black_diamond", 32));
                    silentgem_armours.add(new CombatEntry("tier:kyanite", 40));
                    silentgem_armours.add(new CombatEntry("tier:rose_quartz", 40));
                    silentgem_armours.add(new CombatEntry("tier:kyanite", 40));
                    silentgem_armours.add(new CombatEntry("tier:white_diamond", 45));
                    CombatConfig.registerMultipleConfigs(builder, "silentgem_armours", "Silent gem Armours", silentgem_armours);
                }

                if (hasIceAndFire) {
                    List<CombatEntry> iceandfire_weapons = new ArrayList<>();
                    iceandfire_weapons.add(new CombatEntry("iceandfire:copper_sword", 10, false));
                    iceandfire_weapons.add(new CombatEntry("iceandfire:copper_axe", 10, false));
                    iceandfire_weapons.add(new CombatEntry("iceandfire:silver_sword", 12, false));
                    iceandfire_weapons.add(new CombatEntry("iceandfire:silver_axe", 12, false));
                    CombatConfig.registerMultipleConfigs(builder, "iceandfire_weapons", "Ice & Fire Weapons", iceandfire_weapons);

                    List<CombatEntry> iceandfire_armours = new ArrayList<>();
                    iceandfire_armours.add(new CombatEntry("iceandfire:armor_copper_metal_helmet", 10));
                    iceandfire_armours.add(new CombatEntry("iceandfire:armor_copper_metal_chestplate", 10));
                    iceandfire_armours.add(new CombatEntry("iceandfire:armor_copper_metal_leggings", 10));
                    iceandfire_armours.add(new CombatEntry("iceandfire:armor_copper_metal_boots", 10));
                    iceandfire_armours.add(new CombatEntry("iceandfire:armor_silver_metal_helmet", 12));
                    iceandfire_armours.add(new CombatEntry("iceandfire:armor_silver_metal_chestplate", 12));
                    iceandfire_armours.add(new CombatEntry("iceandfire:armor_silver_metal_leggings", 12));
                    iceandfire_armours.add(new CombatEntry("iceandfire:armor_silver_metal_boots", 12));
                    CombatConfig.registerMultipleConfigs(builder, "iceandfire_armours", "Ice & Fire Armours", iceandfire_armours);
                }
            });
        }

        public static void registerSmeltingConfigs(ModConfigSpec.Builder builder) {
            boolean hasAllTheOres = ModList.get().isLoaded("alltheores");
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
                metals.add(new SmeltingEntry("iceandfire:silver_ingot", 12, "iceandfire:raw_silver"));
                metals.add(new SmeltingEntry("minecraft:iron_ingot", 15, "minecraft:raw_iron"));
                metals.add(new SmeltingEntry("minecraft:gold_ingot", 25, "minecraft:raw_gold"));
                metals.add(new SmeltingEntry("minecraft:gold_ingot", 25, "minecraft:gilded_blackstone"));
                metals.add(new SmeltingEntry("minecraft:diamond", 30, "minecraft:diamond_ore"));
                metals.add(new SmeltingEntry("minecraft:netherite_scrap", 40, "minecraft:ancient_debris"));
                if (ModList.get().isLoaded("silentgear")) {
                    metals.add(new SmeltingEntry("silentgear:crimson_iron_ingot", 36, "silentgear:raw_crimson_iron"));
                    metals.add(new SmeltingEntry("silentgear:azure_silver_ingot", 47, "silentgear:raw_azure_silver"));
                }
                SmeltingConfig.registerMultipleConfigs(builder, "smelting_metals", "Metals", metals);


                List<SmeltingEntry> materials = new ArrayList<>();
                materials.add(new SmeltingEntry("minecraft:charcoal", 1, "minecraft:oak_logs"));
                materials.add(new SmeltingEntry("minecraft:glass", 2, "minecraft:sand"));
                materials.add(new SmeltingEntry("minecraft:bricks", 3, "minecraft:clay_ball"));
                materials.add(new SmeltingEntry("minecraft:stone", 5, "minecraft:cobblestone"));
                materials.add(new SmeltingEntry("minecraft:smooth_stone", 8, "minecraft:stone"));
                materials.add(new SmeltingEntry("minecraft:redstone", 13, "minecraft:redstone_ore"));
                materials.add(new SmeltingEntry("minecraft:redstone", 13, "minecraft:deepslate_redstone_ore"));
                materials.add(new SmeltingEntry("minecraft:lapis_lazuli", 16, "minecraft:lapis_ore"));
                materials.add(new SmeltingEntry("minecraft:quartz", 18, "minecraft:nether_quartz_ore"));
                SmeltingConfig.registerMultipleConfigs(builder, "smelting_general", "General", materials);

                if (hasAllTheOres) {
                    List<SmeltingEntry> smelting_alltheores = new ArrayList<>();
                    smelting_alltheores.add(new SmeltingEntry("alltheores:zinc_ingot", 9, "alltheores:raw_zinc"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:tin_ingot", 11, "alltheores:raw_tin"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:lead_ingot", 16, "alltheores:raw_lead"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:aluminum_ingot", 18, "alltheores:raw_aluminum"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:nickel_ingot", 19, "alltheores:raw_nickel"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:silver_ingot", 12, "alltheores:raw_silver"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:brass_ingot", 13, "alltheores:raw_brass"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:osmium_ingot", 20, "alltheores:raw_osmium"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:bronze_ingot", 20, "alltheores:raw_bronze"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:uranium_ingot", 22, "alltheores:raw_uranium"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:invar_ingot", 23, "alltheores:raw_invar"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:electrum_ingot", 24, "alltheores:raw_electrum"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:signalum_ingot", 32, "alltheores:raw_signalum"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:lumium_ingot", 34, "alltheores:raw_lumium"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:steel_ingot", 41, "alltheores:raw_steel"));
                    smelting_alltheores.add(new SmeltingEntry("alltheores:enderium_ingot", 47, "alltheores:raw_enderium"));
                    SmeltingConfig.registerMultipleConfigs(builder, "smelting_alltheores", "All The ores", smelting_alltheores);

                }


            });
        }

        public static void registerCraftingConfigs(ModConfigSpec.Builder builder) {
            boolean hasSilentGear = ModList.get().isLoaded("silentgear");
            boolean hasSilentGems = ModList.get().isLoaded("silentgems");
            boolean hasIceAndFire = ModList.get().isLoaded("iceandfire");
            boolean hasAllTheMods = ModList.get().isLoaded("allthemods");
            boolean hasBno = ModList.get().isLoaded("bno");
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
                CraftingConfig.registerMultipleConfigs(builder, "crafting_metal_materials", "Metals", metals);

                //Coal/Misc?
                List<CraftingEntry> misc = new ArrayList<>();
                misc.add(new CraftingEntry("minecraft:wheat", 1, 1.0, "c:crops"));
                misc.add(new CraftingEntry("minecraft:coal_block", 3, "minecraft:coals"));
                misc.add(new CraftingEntry("minecraft:redstone", 13, "c:dusts/redstone"));
                CraftingConfig.registerMultipleConfigs(builder, "crafting_misc_materials", "Misc", misc);

                //Gems
                List<CraftingEntry> gems = new ArrayList<>();
                gems.add(new CraftingEntry("silentgear:bort", 15, "c:gems/bort"));
                gems.add(new CraftingEntry("minecraft:quartz", 18, 20.0, "c:gems/quartz"));
                gems.add(new CraftingEntry("quartz", 18));
                gems.add(new CraftingEntry("iron_rod", 15, "c:rods/iron"));
                gems.add(new CraftingEntry("blaze_rod", 20, "c:rods/blaze"));
                gems.add(new CraftingEntry("blaze_powder", 20));
                gems.add(new CraftingEntry("minecraft:diamond", 30, "c:gems/diamond"));
                gems.add(new CraftingEntry("minecraft:emerald", 32, "c:gems/emerald"));
                CraftingConfig.registerMultipleConfigs(builder, "crafting_gem_materials", "Gems", gems);

                List<CraftingEntry> silentgear = new ArrayList<>();
                if (hasSilentGear) {
                    silentgear.add(new CraftingEntry("silentgear:bronze_ingot", 18, "c:ingots/bronze"));
                    silentgear.add(new CraftingEntry("silentgear:blaze_gold", 25, "c:ingots/blaze_gold"));
                    silentgear.add(new CraftingEntry("silentgear:crimson_iron", 36, "c:ingots/crimson_iron"));
                    silentgear.add(new CraftingEntry("silentgear:crimson_steel_ingot", 45, "c:ingots/crimson_steel"));
                    silentgear.add(new CraftingEntry("silentgear:azure_silver_ingot", 47, "c:ingots/azure_silver"));
                    silentgear.add(new CraftingEntry("silentgear:azure_electrum_ingot", 49, "c:ingots/azure_electrum"));
                    silentgear.add(new CraftingEntry("silentgear:tyrian_steel_ingot", 50, "c:ingots/tyrian_steel"));
                    CraftingConfig.registerMultipleConfigs(builder, "crafting_silentgear", "Silent Gear", silentgear);
                }
                if (hasSilentGems) {
                    List<CraftingEntry> silentgems = new ArrayList<>();
                    //silver
                    silentgems.add(new CraftingEntry("silentgems:silver_ingot", 12, "c:ingots/silver"));
                    //ruby
                    silentgems.add(new CraftingEntry("silentgems:ruby", 15));
                    //sapphire
                    silentgems.add(new CraftingEntry("silentgems:sapphire", 15));
                    //iolite
                    silentgems.add(new CraftingEntry("silentgems:iolite", 15));
                    //moldavite
                    silentgems.add(new CraftingEntry("silentgems:moldavite", 15));
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
                if (ModList.get().isLoaded("bno")) {
                    List<CraftingEntry> bno = new ArrayList<>();
                    bno.add(new CraftingEntry("bno:zinc_ingot", 9, "c:ingots/zinc"));
                    bno.add(new CraftingEntry("bno:tin_ingot", 11, "c:ingots/tin"));
                    bno.add(new CraftingEntry("bno:lead_ingot", 16, "c:ingots/lead"));
                    bno.add(new CraftingEntry("bno:aluminum_ingot", 18, "c:ingots/aluminum"));
                    bno.add(new CraftingEntry("bno:nickel_ingot", 19, "c:ingots/nickel"));
                    bno.add(new CraftingEntry("bno:silver_ingot", 12, "c:ingots/silver"));
                    bno.add(new CraftingEntry("bno:osmium_ingot", 20, "c:ingots/osmium"));
                    bno.add(new CraftingEntry("bno:uranium_ingot", 22, "c:ingots/uranium"));
                    CraftingConfig.registerMultipleConfigs(builder, "crafting_bno", "Basic Nether", bno);
                }
                if (ModList.get().isLoaded("alltheores")) {
                    List<CraftingEntry> bno = new ArrayList<>();
                    bno.add(new CraftingEntry("alltheores:zinc_ingot", 9, "c:ingots/zinc"));
                    bno.add(new CraftingEntry("alltheores:tin_ingot", 11, "c:ingots/tin"));
                    bno.add(new CraftingEntry("alltheores:lead_ingot", 16, "c:ingots/lead"));
                    bno.add(new CraftingEntry("alltheores:aluminum_ingot", 18, "c:ingots/aluminum"));
                    bno.add(new CraftingEntry("alltheores:nickel_ingot", 19, "c:ingots/nickel"));
                    bno.add(new CraftingEntry("alltheores:silver_ingot", 12, "c:ingots/silver"));
                    bno.add(new CraftingEntry("alltheores:brass_ingot", 13, "c:ingots/brass"));
                    bno.add(new CraftingEntry("alltheores:osmium_ingot", 20, "c:ingots/osmium"));
                    bno.add(new CraftingEntry("alltheores:bronze_ingot", 20, "c:ingots/bronze"));
                    bno.add(new CraftingEntry("alltheores:uranium_ingot", 22, "c:ingots/uranium"));
                    bno.add(new CraftingEntry("alltheores:invar_ingot", 23, "c:ingots/invar"));
                    bno.add(new CraftingEntry("alltheores:electrum_ingot", 24, "c:ingots/electrum"));
                    bno.add(new CraftingEntry("alltheores:signalum_ingot", 32, "c:ingots/signalum"));
                    bno.add(new CraftingEntry("alltheores:lumium_ingot", 34, "c:ingots/lumium"));
                    bno.add(new CraftingEntry("alltheores:steel_ingot", 41, "c:ingots/steel"));
                    bno.add(new CraftingEntry("alltheores:enderium_ingot", 47, "c:ingots/enderium"));
                    CraftingConfig.registerMultipleConfigs(builder, "crafting_bno", "Basic Nether", bno);
                }
            });
        }

        public static void registerToolConfigs(ModConfigSpec.Builder builder) {
            ConfigRegister.registerCategory(builder, "tools_config", "Tools", "Settings for Tools", () -> {
                boolean hasSilentGear = ModList.get().isLoaded("silentgear");
                boolean hasSilentGems = ModList.get().isLoaded("silentgems");
                boolean hasIceAndFire = ModList.get().isLoaded("iceandfire");
                List<ToolEntry> tools = new ArrayList<>();
                tools.add(new ToolEntry("minecraft:wooden_pickaxe", 1));
                tools.add(new ToolEntry("minecraft:stone_pickaxe", 5));
                tools.add(new ToolEntry("minecraft:iron_pickaxe", 15));
                tools.add(new ToolEntry("minecraft:golden_pickaxe", 15));
                tools.add(new ToolEntry("minecraft:diamond_pickaxe", 30));
                tools.add(new ToolEntry("minecraft:netherite_pickaxe", 40));
                ToolConfig.registerMultipleConfigs(builder, "minecraft_tools", "Tools", tools);
                if (hasSilentGear) {
                    List<ToolEntry> silentgear_tools = new ArrayList<>();
                    silentgear_tools.add(new ToolEntry("tier:wood", 1));
                    silentgear_tools.add(new ToolEntry("tier:terracotta", 3));
                    silentgear_tools.add(new ToolEntry("tier:stone", 5));
                    silentgear_tools.add(new ToolEntry("tier:flint", 7));
                    silentgear_tools.add(new ToolEntry("tier:copper", 10));
                    silentgear_tools.add(new ToolEntry("tier:iron", 15));
                    silentgear_tools.add(new ToolEntry("tier:bronze", 15));
                    silentgear_tools.add(new ToolEntry("tier:gold", 15));
                    silentgear_tools.add(new ToolEntry("tier:blaze_gold", 25));
                    silentgear_tools.add(new ToolEntry("tier:emerald", 25));
                    silentgear_tools.add(new ToolEntry("tier:diamond", 30));
                    silentgear_tools.add(new ToolEntry("tier:dimerald", 32));
                    silentgear_tools.add(new ToolEntry("tier:netherite", 40));
                    silentgear_tools.add(new ToolEntry("tier:crimson_iron", 36));
                    silentgear_tools.add(new ToolEntry("tier:crimson_steel", 45));
                    silentgear_tools.add(new ToolEntry("tier:azure_silver", 47));
                    silentgear_tools.add(new ToolEntry("tier:azure_electrum", 49));
                    silentgear_tools.add(new ToolEntry("tier:tyrian_steel", 50));
                    ToolConfig.registerMultipleConfigs(builder, "silentgear_tools", "Silentgear Tools", silentgear_tools);
                }

                if (hasSilentGems) {
                    List<ToolEntry> silentgems_tools = new ArrayList<>();
                    silentgems_tools.add(new ToolEntry("tier:ruby", 15));
                    silentgems_tools.add(new ToolEntry("tier:sapphire", 15));
                    silentgems_tools.add(new ToolEntry("tier:iolite", 15));
                    silentgems_tools.add(new ToolEntry("tier:moldavite", 15));
                    silentgems_tools.add(new ToolEntry("tier:peridot", 15));
                    silentgems_tools.add(new ToolEntry("tier:carnelian", 17));
                    silentgems_tools.add(new ToolEntry("tier:topaz", 19));
                    silentgems_tools.add(new ToolEntry("tier:citrine", 22));
                    silentgems_tools.add(new ToolEntry("tier:heliodor", 25));
                    silentgems_tools.add(new ToolEntry("tier:turquoise", 30));
                    silentgems_tools.add(new ToolEntry("tier:alexandrite", 30));
                    silentgems_tools.add(new ToolEntry("tier:ammolite", 30));
                    silentgems_tools.add(new ToolEntry("tier:black_diamond", 32));
                    silentgems_tools.add(new ToolEntry("tier:kyanite", 40));
                    silentgems_tools.add(new ToolEntry("tier:rose_quartz", 40));
                    silentgems_tools.add(new ToolEntry("tier:kyanite", 40));
                    silentgems_tools.add(new ToolEntry("tier:white_diamond", 45));
                    ToolConfig.registerMultipleConfigs(builder, "silentgems_tools", "Silentgems Tools", silentgems_tools);
                }

                if (hasIceAndFire) {
                    List<ToolEntry> iceandfire_tools = new ArrayList<>();
                    iceandfire_tools.add(new ToolEntry("iceandfire:copper_pickaxe", 10));
                    iceandfire_tools.add(new ToolEntry("iceandfire:silver_pickaxe", 12));
                    ToolConfig.registerMultipleConfigs(builder, "iceandfire_tools", "Ice & Fire Tools", iceandfire_tools);
                }
            });
        }

        public static void registerMiningConfigs(ModConfigSpec.Builder builder) {
            boolean hasSilentGear = ModList.get().isLoaded("silentgear");
            boolean hasSilentGems = ModList.get().isLoaded("silentgems");
            boolean hasIceAndFire = ModList.get().isLoaded("iceandfire");
            boolean hasAlltheores = ModList.get().isLoaded("alltheores");
            boolean hasBno = ModList.get().isLoaded("bno");
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
                MiningConfig.registerMultipleConfigs(builder, "mining_metal_ores", "Metals", metals);

                // Gems
                List<MiningEntry> gems = new ArrayList<>();
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

                if (hasSilentGear) {
                    List<MiningEntry> mining_silentgear = new ArrayList<>();
                    mining_silentgear.add(new MiningEntry("silentgear:bort_ore", 15, "c:ores/bort"));
                    mining_silentgear.add(new MiningEntry("silentgear:crimson_iron_ore", 36, "c:ores/crimson_iron"));
                    mining_silentgear.add(new MiningEntry("silentgear:azure_silver_ore", 47, "c:ores/azure_silver"));
                    MiningConfig.registerMultipleConfigs(builder, "mining_silentgear", "Silentgear", mining_silentgear);
                }

                if (hasSilentGems) {
                    List<MiningEntry> silentgems = new ArrayList<>();
                    silentgems.add(new MiningEntry("silentgems:silver_ore", 12));
                    silentgems.add(new MiningEntry("silentgems:deepslate_silver_ore", 12));
                    silentgems.add(new MiningEntry("silentgems:ruby_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:deepslate_ruby_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:nether_ruby_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_ruby_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:sapphire_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:deepslate_sapphire_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:nether_sapphire_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_sapphire_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:iolite_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:deepslate_iolite_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:nether_iolite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_iolite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:moldavite_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:deepslate_moldavite_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:nether_moldavite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_moldavite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:peridot_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:deepslate_peridot_ore", 15));
                    silentgems.add(new MiningEntry("silentgems:nether_peridot_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_peridot_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:carnelian_ore", 17));
                    silentgems.add(new MiningEntry("silentgems:deepslate_carnelian_ore", 17));
                    silentgems.add(new MiningEntry("silentgems:nether_carnelian_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_carnelian_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:topaz_ore", 19));
                    silentgems.add(new MiningEntry("silentgems:deepslate_topaz_ore", 19));
                    silentgems.add(new MiningEntry("silentgems:nether_topaz_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_topaz_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:citrine_ore", 22));
                    silentgems.add(new MiningEntry("silentgems:deepslate_citrine_ore", 22));
                    silentgems.add(new MiningEntry("silentgems:nether_citrine_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_citrine_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:heliodor_ore", 25));
                    silentgems.add(new MiningEntry("silentgems:deepslate_heliodor_ore", 25));
                    silentgems.add(new MiningEntry("silentgems:nether_heliodor_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_heliodor_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:turquoise_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:deepslate_turquoise_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:nether_turquoise_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_turquoise_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:alexandrite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:deepslate_alexandrite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:nether_alexandrite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_alexandrite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:ammolite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:deepslate_ammolite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:nether_ammolite_ore", 30));
                    silentgems.add(new MiningEntry("silentgems:end_ammolite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:black_diamond_ore", 32));
                    silentgems.add(new MiningEntry("silentgems:deepslate_black_diamond_ore", 32));
                    silentgems.add(new MiningEntry("silentgems:nether_black_diamond_ore", 32));
                    silentgems.add(new MiningEntry("silentgems:end_black_diamond_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:deepslate_kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:nether_kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:end_kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:rose_quartz_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:deepslate_rose_quartz_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:nether_rose_quartz_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:end_rose_quartz_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:deepslate_kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:nether_kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:end_kyanite_ore", 40));
                    silentgems.add(new MiningEntry("silentgems:white_diamond_ore", 45));
                    silentgems.add(new MiningEntry("silentgems:deepslate_white_diamond_ore", 45));
                    silentgems.add(new MiningEntry("silentgems:nether_white_diamond_ore", 45));
                    silentgems.add(new MiningEntry("silentgems:end_white_diamond_ore", 45));
                    MiningConfig.registerMultipleConfigs(builder, "mining_silentgems", "Silent Gems", silentgems);
                }

                if (hasIceAndFire) {
                    List<MiningEntry> iceandfire = new ArrayList<>();
                    iceandfire.add(new MiningEntry("iceandfire:silver_ore", 12));
                    iceandfire.add(new MiningEntry("iceandfire:deepslate_silver_ore", 12));
                    iceandfire.add(new MiningEntry("iceandfire:sapphire_ore", 15));
                    MiningConfig.registerMultipleConfigs(builder, "mining_iceandfire", "Ice & Fire", iceandfire);
                }
                if (hasBno) {
                    List<MiningEntry> mining_bno = new ArrayList<>();
                    mining_bno.add(new MiningEntry("alltheores:zinc_ore", 9, "c:ores/zinc"));
                    mining_bno.add(new MiningEntry("alltheores:tin_ore", 11, "c:ores/tin"));
                    mining_bno.add(new MiningEntry("alltheores:lead_ore", 16, "c:ores/lead"));
                    mining_bno.add(new MiningEntry("alltheores:aluminum_ore", 18, "c:ores/aluminum"));
                    mining_bno.add(new MiningEntry("alltheores:nickel_ore", 19, "c:ores/nickel"));
                    mining_bno.add(new MiningEntry("alltheores:silver_ore", 12, "c:ores/silver"));
                    mining_bno.add(new MiningEntry("alltheores:brass_ore", 13, "c:ores/brass"));
                    mining_bno.add(new MiningEntry("alltheores:osmium_ore", 20, "c:ores/osmium"));
                    mining_bno.add(new MiningEntry("alltheores:bronze_ingot", 20, "c:ores/bronze"));
                    mining_bno.add(new MiningEntry("alltheores:uranium_ingot", 22, "c:ores/uranium"));
                    mining_bno.add(new MiningEntry("alltheores:invar_ingot", 23, "c:ores/invar"));
                    mining_bno.add(new MiningEntry("alltheores:electrum_ingot", 24, "c:ores/electrum"));
                    mining_bno.add(new MiningEntry("alltheores:signalum_ingot", 32, "c:ores/signalum"));
                    mining_bno.add(new MiningEntry("alltheores:lumium_ingot", 34, "c:ores/lumium"));
                    mining_bno.add(new MiningEntry("alltheores:steel_ingot", 47, "c:ores/steel"));
                    mining_bno.add(new MiningEntry("alltheores:enderium_ingot", 47, "c:ores/enderium"));
                    MiningConfig.registerMultipleConfigs(builder, "mining_bno", "Basic Nether", mining_bno);
                }
                if (hasAlltheores) {
                    List<MiningEntry> mining_alltheores = new ArrayList<>();
                    mining_alltheores.add(new MiningEntry("alltheores:zinc_ore", 9, "c:ores/zinc"));
                    mining_alltheores.add(new MiningEntry("alltheores:tin_ore", 11, "c:ores/tin"));
                    mining_alltheores.add(new MiningEntry("alltheores:lead_ore", 16, "c:ores/lead"));
                    mining_alltheores.add(new MiningEntry("alltheores:aluminum_ore", 18, "c:ores/aluminum"));
                    mining_alltheores.add(new MiningEntry("alltheores:nickel_ore", 19, "c:ores/nickel"));
                    mining_alltheores.add(new MiningEntry("alltheores:silver_ore", 12, "c:ores/silver"));
                    mining_alltheores.add(new MiningEntry("alltheores:brass_ore", 13, "c:ores/brass"));
                    mining_alltheores.add(new MiningEntry("alltheores:osmium_ore", 20, "c:ores/osmium"));
                    mining_alltheores.add(new MiningEntry("alltheores:bronze_ore", 20, "c:ores/bronze"));
                    mining_alltheores.add(new MiningEntry("alltheores:uranium_ore", 22, "c:ores/uranium"));
                    mining_alltheores.add(new MiningEntry("alltheores:invar_ore", 23, "c:ores/invar"));
                    mining_alltheores.add(new MiningEntry("alltheores:electrum_ore", 24, "c:ores/electrum"));
                    mining_alltheores.add(new MiningEntry("alltheores:signalum_ore", 32, "c:ores/signalum"));
                    mining_alltheores.add(new MiningEntry("alltheores:lumium_ore", 34, "c:ores/lumium"));
                    mining_alltheores.add(new MiningEntry("alltheores:steel_ore", 47, "c:ores/steel"));
                    mining_alltheores.add(new MiningEntry("alltheores:enderium_ore", 47, "c:ores/enderium"));
                    MiningConfig.registerMultipleConfigs(builder, "mining_alltheores", "All The Ores", mining_alltheores);

                }
            });
        }
    }

    public static final class Client {

        static ModConfigSpec SPEC;

        public static final ModConfigSpec.IntValue CLIENT_CONFIG_VERSION;

        public static ModConfigSpec.EnumValue<XpDisplayMode> XP_TEXT_MODE;
        public enum XpDisplayMode {XP, PERCENT, BOTH}

        public static ModConfigSpec.EnumValue<GuiPosition> GUI_POSITION;
        public enum GuiPosition {
            TOP_LEFT,
            TOP_CENTER,
            TOP_RIGHT,
            CENTER_LEFT,
            CENTER,
            CENTER_RIGHT,
            BOTTOM_LEFT,
            BOTTOM_CENTER,
            BOTTOM_RIGHT
        }
        public static final ModConfigSpec.IntValue X_PADDING;
        public static final ModConfigSpec.IntValue Y_PADDING;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
            CLIENT_CONFIG_VERSION = builder.translation("Client Config Version: ").comment("DO NOT CHANGE. Used for tracking config updates.").defineInRange("config_version", CLIENT_VERSION, 1, Integer.MAX_VALUE);

            XP_TEXT_MODE = builder
                    .translation("Xp Display Mode: ")
                    .comment("Skill Overlay Xp mode. Valid values are: BOTH, XP, and PERCENT")
                    .comment("Default: BOTH")
                    .defineEnum("xp_text_mode", XpDisplayMode.BOTH);
            GUI_POSITION = builder
                    .translation("Overlay Position:")
                    .comment("Where on the screen should the overlay display?")
                    .defineEnum("gui_position", GuiPosition.CENTER_LEFT);
            X_PADDING = builder.translation("Overlay X Adjustments: ").comment("Fine adjust the X position for the overlay").comment("Default: 10").defineInRange("x_adjustment", 10, 1, Integer.MAX_VALUE);
            Y_PADDING = builder.translation("Overlay Y Adjustments: ").comment("Fine adjust the Y position for the overlay").comment("Default: 10").defineInRange("y_adjustment", 10, 1, Integer.MAX_VALUE);

            SPEC = builder.build();
        }
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Loading event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == Config.Server.SPEC) {
            int storedVersion = Config.Server.SERVER_CONFIG_VERSION.get();
            if (storedVersion < SERVER_VERSION) {
                ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
                Path configFilePath = Paths.get("config", MOD_ID + "-common.toml");

                if (Files.exists(configFilePath)) {
                    try {
                        Files.delete(configFilePath);
                        System.out.println("Old config file deleted due to version change.");
                    } catch (IOException e) {
                        System.err.println("Failed to delete old config file: " + e.getMessage());
                    }
                }
                // Register all config sections again
                Config.Server.registerCombatConfigs(builder);
                Config.Server.registerSmeltingConfigs(builder);
                Config.Server.registerCraftingConfigs(builder);
                Config.Server.registerToolConfigs(builder);
                Config.Server.registerMiningConfigs(builder);

                Config.Server.SERVER_CONFIG_VERSION.set(SERVER_VERSION);
                Config.Server.SPEC = builder.build();
                config.getSpec().validateSpec(config);
                assert config.getLoadedConfig() != null;
                config.getLoadedConfig().save();
                System.out.println("Config rebuilt due to version change.");
            }
            System.out.println("Reloaded on server");
        }
        if (config.getSpec() == Config.Client.SPEC) {
            int storedVersion = Client.CLIENT_CONFIG_VERSION.get();
            if (storedVersion < CLIENT_VERSION) {
                ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
                Config.Client.XP_TEXT_MODE = builder
                        .translation("Xp Display Mode: ")
                        .comment("Skill Overlay Xp mode. Valid values are: BOTH, XP, and PERCENT")
                        .comment("Default: BOTH")
                        .defineEnum("xp_text_mode", Client.XpDisplayMode.BOTH);
                Client.CLIENT_CONFIG_VERSION.set(CLIENT_VERSION);
                Config.Client.SPEC = builder.build();
            }
            System.out.println("Reloaded on client");
        }
    }
}