package net.raynna.raynnarpg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

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
        static final ModConfigSpec SPEC;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
            registerConfigs(builder);
            SPEC = builder.build();
        }
    }

    public static final class Client {
        static final ModConfigSpec SPEC;

        private static final ModConfigSpec.BooleanValue MOD_IS_ENABLED_CLIENT;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
            MOD_IS_ENABLED_CLIENT = builder.comment("Wheather if mod should be enabled or not; DEFAULT: true").define("mod_is_enabled", true);
            registerConfigs(builder);
            SPEC = builder.build();
        }
    }

    private static void registerConfigs(ModConfigSpec.Builder builder) {
        int level;
        double xp;
        List<String> tags;
        String raw_variant;

        //START OF MINING
        builder.translation("Mining").comment("Settings for Mining").push("mining");

                builder.translation("Stone").push("mining_stone");
                    level = 1;
                    xp = 4;
                    tags = List.of("c:stones", "c:cobblestones", "minecraft:stones", "minecraft:cobblestones");
                    MINING_LEVEL.put("minecraft:stone", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:stone", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:stone", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Stone Bricks").push("mining_stone_bricks");
                    level = 2;
                    xp = 8;
                    tags = List.of("minecraft:stone_bricks");
                    MINING_LEVEL.put("minecraft:stone_bricks", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:stone_bricks", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:stone_bricks", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Coal Ore").push("mining_coal_ore");
                    level = 3;
                    xp = 12;
                    tags = List.of("minecraft:coal_ores", "c:ores/coal");
                    MINING_LEVEL.put("minecraft:coal_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:coal_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:coal_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Terracotta").push("mining_terracotta");
                    level = 4;
                    xp = 16;
                    tags = List.of("minecraft:terracotta");
                    MINING_LEVEL.put("minecraft:terracotta", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:terracotta", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:terracotta", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Copper Ore").push("mining_copper_ore");
                    level = 10;
                    xp = 40;
                    tags = List.of("minecraft:copper_ores", "c:ores/copper");
                    MINING_LEVEL.put("minecraft:copper_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:copper_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:copper_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Redstone Ore").push("mining_redstone_ore");
                    level = 13;
                    xp = 52;
                    tags = List.of("minecraft:redstone_ores", "c:ores/redstone");
                    MINING_LEVEL.put("minecraft:redstone_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:redstone_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:redstone_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Bort Ore").push("mining_bort_ore");
                    level = 15;
                    xp = 60;
                    tags = List.of("c:ores/bort");
                    MINING_LEVEL.put("minecraft:bort_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:bort_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:bort_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Iron Ore").push("mining_iron_ore");
                    level = 15;
                    xp = 60;
                    tags = List.of("minecraft:iron_ores", "c:ores/iron");
                    MINING_LEVEL.put("minecraft:iron_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:iron_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:iron_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Lapis Ore").push("mining_lapis_ore");
                    level = 16;
                    xp = 64;
                    tags = List.of("minecraft:lapis_ores", "c:ores/lapis");
                    MINING_LEVEL.put("minecraft:lapis_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:lapis_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:lapis_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Quartz Ore").push("mining_quartz_ore");
                    level = 18;
                    xp = 72;
                    tags = List.of("c:ores/quartz");
                    MINING_LEVEL.put("minecraft:nether_quartz_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:nether_quartz_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:nether_quartz_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Gold Ore").push("mining_gold_ore");
                    level = 20;
                    xp = 80;
                    tags = List.of("minecraft:gold_ores", "c:ores/gold");
                    MINING_LEVEL.put("minecraft:gold_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:gold_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:gold_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Diamond Ore").push("mining_diamond_ore");
                    level = 30;
                    xp = 120;
                    tags = List.of("minecraft:diamond_ores", "c:ores/diamond");
                    MINING_LEVEL.put("minecraft:diamond_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:diamond_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:diamond_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Emerald Ore").push("mining_emerald_ore");
                    level = 35;
                    xp = 140;
                    tags = List.of("minecraft:emerald_ores", "c:ores/emerald");
                    MINING_LEVEL.put("minecraft:emerald_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:emerald_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:emerald_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Crimson Iron Ore").push("mining_crimson_iron_ore");
                    level = 43;
                    xp = 172;
                    tags = List.of("c:ores/crimson_iron");
                    MINING_LEVEL.put("minecraft:crimson_iron_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:crimson_iron_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:crimson_iron_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Azure Silver Ore").push("mining_azure_silver_ore");
                    level = 48;
                    xp = 192;
                    tags = List.of("c:ores/azure_silver");
                    MINING_LEVEL.put("silentgear:azure_silver_ore", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("silentgear:azure_silver_ore", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("silentgear:azure_silver_ore", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Ancient Debris").push("mining_ancient_debris");
                    level = 40;
                    xp = 160;
                    tags = List.of("c:ores/netherite_scrap");
                    MINING_LEVEL.put("minecraft:ancient_debris", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:ancient_debris", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:ancient_debris", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("End Stone").push("mining_end_stone");
                    level = 40;
                    xp = 10;
                    tags = List.of("minecraft:end_stones");
                    MINING_LEVEL.put("minecraft:end_stone", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("minecraft:end_stone", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("minecraft:end_stone", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Silent Gems Ore").push("mining_silent_gems");
                    level = 15;
                    xp = 60;
                    tags = List.of("silentgems:ores");
                    MINING_LEVEL.put("silentgems:silent_gems", builder.translation("Ore Level: ").comment("Configuration on what level requirement it is for mining").comment("Default: " + level).define("level", level));
                    MINING_XP.put("silentgems:silent_gems", builder.translation("Ore Xp: ").comment("Configuration on what how much xp it yields.").comment("Default: " + xp).define("xp", xp));
                    MINING_TAGS.put("silentgems:silent_gems", builder.translation("Ore Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();
        builder.pop();
        //END OF MINING


        builder.translation("Crafting").comment("Settings for Crafting").push("crafting");

            builder.translation("Wooden Materials").push("crafting_wood");
                builder.translation("Logs").push("crafting_logs");
                    level = 1;
                    xp = 4;
                    tags = List.of("minecraft:logs");
                    CRAFTING_LEVEL.put("minecraft:oak_log", builder.translation("Log Level: ").comment("Configuration on how much logs should yield in xp while crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("minecraft:oak_log", builder.translation("Log Xp: ").comment("Configuration on what level requirement an oak log should have to be able to craft with").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("minecraft:oak_log", builder.translation("Log Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Planks").push("crafting_planks");
                    level = 1;
                    xp = 1;
                    tags = List.of("minecraft:planks");
                    CRAFTING_LEVEL.put("minecraft:oak_plank", builder.translation("Plank Level: ").comment("Configuration on what level requirement an oak plank should have to be able to craft with").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("minecraft:oak_plank", builder.translation("Plank Xp: ").comment("Configuration on how much oak plank should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("minecraft:oak_plank", builder.translation("Plank Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Stick").push("crafting_stick");
                    level = 1;
                    xp = 0.25;
                    tags = List.of("c:rods/wooden");
                    CRAFTING_LEVEL.put("minecraft:stick", builder.translation("Stick Level: ").comment("Configuration on what level requirement a stick should have to be able to craft with").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("minecraft:stick", builder.translation("Stick Xp: ").comment("Configuration on how much stick should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("minecraft:stick", builder.translation("Stick Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();
            builder.pop();

            builder.translation("Iron Materials").push("crafting_iron");
                builder.translation("Iron Ingot").push("crafting_iron_ingot");
                    level = 15;
                    xp = 60;
                    tags = List.of("c:ingots/iron");
                    CRAFTING_LEVEL.put("minecraft:iron_ingot", builder.translation("Iron Ingot Level: ").comment("Configuration on what level requirement an iron ingot should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("minecraft:iron_ingot", builder.translation("Iron Ingot Xp: ").comment("Configuration on how much iron ingot should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("minecraft:iron_ingot", builder.translation("Iron Ingot Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Copper Ingot").push("crafting_copper_ingot");
                    level = 10;
                    xp = 40;
                    tags = List.of("c:ingots/copper");
                    CRAFTING_LEVEL.put("minecraft:copper_ingot", builder.translation("Copper Ingot Level: ").comment("Configuration on what level requirement a copper ingot should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("minecraft:copper_ingot", builder.translation("Copper Ingot Xp: ").comment("Configuration on how much copper ingot should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("minecraft:copper_ingot", builder.translation("Copper Ingot Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();
            builder.pop();

            builder.translation("Coal Materials").push("crafting_coal");
                builder.translation("Coal Block").push("crafting_coal_block");
                    level = 3;
                    xp = 6;
                    tags = List.of("minecraft:coals");
                    CRAFTING_LEVEL.put("minecraft:coal_block", builder.translation("Coal Block Level: ").comment("Configuration on what level requirement a coal block should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("minecraft:coal_block", builder.translation("Coal Block Xp: ").comment("Configuration on how much coal block should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("minecraft:coal_block", builder.translation("Coal Block Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();
            builder.pop();

            builder.translation("Gem Materials").push("crafting_gems");
                builder.translation("Bort").push("crafting_bort");
                    level = 15;
                    xp = 60;
                    tags = List.of("c:gems/bort");
                    CRAFTING_LEVEL.put("silentgear:bort", builder.translation("Bort Level: ").comment("Configuration on what level requirement a bort should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("silentgear:bort", builder.translation("Bort Xp: ").comment("Configuration on how much bort should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("silentgear:bort", builder.translation("Bort Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Gold Ingot").push("crafting_gold_ingot");
                    level = 20;
                    xp = 80;
                    tags = List.of("c:ingots/gold");
                    CRAFTING_LEVEL.put("minecraft:gold_ingot", builder.translation("Gold Ingot Level: ").comment("Configuration on what level requirement a gold ingot should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("minecraft:gold_ingot", builder.translation("Gold Ingot Xp: ").comment("Configuration on how much gold ingot should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("minecraft:gold_ingot", builder.translation("Gold Ingot Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Diamond").push("crafting_diamond");
                    level = 30;
                    xp = 120;
                    tags = List.of("c:gems/diamond");
                    CRAFTING_LEVEL.put("minecraft:diamond", builder.translation("Diamond Level: ").comment("Configuration on what level requirement a diamond should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("minecraft:diamond", builder.translation("Diamond Xp: ").comment("Configuration on how much diamond should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("minecraft:diamond", builder.translation("Diamond Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();
            builder.pop();

            builder.translation("Netherite Materials").push("crafting_netherite");
                builder.translation("Netherite Ingot").push("crafting_netherite_ingot");
                    level = 40;
                    xp = 320;
                    tags = List.of("c:ingots/netherite");
                    CRAFTING_LEVEL.put("minecraft:netherite_ingot", builder.translation("Netherite Ingot Level: ").comment("Configuration on what level requirement a netherite ingot should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("minecraft:netherite_ingot", builder.translation("Netherite Ingot Xp: ").comment("Configuration on how much netherite ingot should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("minecraft:netherite_ingot", builder.translation("Netherite Ingot Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Netherite Scrap").push("crafting_netherite_scrap");
                    level = 40;
                    xp = 80;
                    tags = List.of("c:ingots/netherite");
                    CRAFTING_LEVEL.put("minecraft:netherite_scrap", builder.translation("Netherite Scrap Level: ").comment("Configuration on what level requirement a netherite scrap should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("minecraft:netherite_scrap", builder.translation("Netherite Scrap Xp: ").comment("Configuration on how much netherite scrap should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("minecraft:netherite_scrap", builder.translation("Netherite Scrap Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();
            builder.pop();

            builder.translation("Crimson Materials").push("crafting_crimson");
                builder.translation("Crimson Iron").push("crafting_crimson_iron");
                    level = 43;
                    xp = 172;
                    tags = List.of("c:ingots/crimson_iron");
                    CRAFTING_LEVEL.put("silentgear:crimson_iron", builder.translation("Crimson Iron Level: ").comment("Configuration on what level requirement a crimson iron should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("silentgear:crimson_iron", builder.translation("Crimson Iron Xp: ").comment("Configuration on how much crimson iron should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("silentgear:crimson_iron", builder.translation("Crimson Iron Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();

                builder.translation("Blaze Gold").push("crafting_blaze_gold");
                    level = 20;
                    xp = 140;
                    tags = List.of("c:ingots/blaze_gold");
                    CRAFTING_LEVEL.put("silentgear:blaze_gold", builder.translation("Blaze Gold Level: ").comment("Configuration on what level requirement a blaze gold should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("silentgear:blaze_gold", builder.translation("Blaze Gold Xp: ").comment("Configuration on how much blaze gold should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("silentgear:blaze_gold", builder.translation("Blaze Gold Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();
            builder.pop();

            builder.translation("Crimson Steel Materials").push("crafting_crimson_steel");
                builder.translation("Crimson Steel Ingot").push("crafting_crimson_steel_ingot");
                    level = 46;
                    xp = 184;
                    tags = List.of("c:ingots/crimson_steel");
                    CRAFTING_LEVEL.put("silentgear:crimson_steel_ingot", builder.translation("Crimson Steel Ingot Level: ").comment("Configuration on what level requirement a crimson steel ingot should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("silentgear:crimson_steel_ingot", builder.translation("Crimson Steel Ingot Xp: ").comment("Configuration on how much crimson steel ingot should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("silentgear:crimson_steel_ingot", builder.translation("Crimson Steel Ingot Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();
            builder.pop();

            builder.translation("Azure Silver Materials").push("crafting_azure_silver");
                builder.translation("Azure Silver Ingot").push("crafting_azure_silver_ingot");
                    level = 48;
                    xp = 192;
                    tags = List.of("c:ingots/azure_silver");
                    CRAFTING_LEVEL.put("silentgear:azure_silver_ingot", builder.translation("Azure Silver Ingot Level: ").comment("Configuration on what level requirement an azure silver ingot should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("silentgear:azure_silver_ingot", builder.translation("Azure Silver Ingot Xp: ").comment("Configuration on how much azure silver ingot should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("silentgear:azure_silver_ingot", builder.translation("Azure Silver Ingot Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();
            builder.pop();

            builder.translation("Azure Electrum Materials").push("crafting_azure_electrum");
                builder.translation("Azure Electrum Ingot").push("crafting_azure_electrum_ingot");
                    level = 49;
                    xp = 196;
                    tags = List.of("c:ingots/azure_electrum");
                    CRAFTING_LEVEL.put("silentgear:azure_electrum_ingot", builder.translation("Azure Electrum Ingot Level: ").comment("Configuration on what level requirement an azure electrum ingot should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("silentgear:azure_electrum_ingot", builder.translation("Azure Electrum Ingot Xp: ").comment("Configuration on how much azure electrum ingot should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("silentgear:azure_electrum_ingot", builder.translation("Azure Electrum Ingot Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();
            builder.pop();

            builder.translation("Tyrian Steel Materials").push("crafting_tyrian_steel");
                builder.translation("Tyrian Steel Ingot").push("crafting_tyrian_steel_ingot");
                    level = 50;
                    xp = 200;
                    tags = List.of("c:ingots/tyrian_steel");
                    CRAFTING_LEVEL.put("silentgear:tyrian_steel_ingot", builder.translation("Tyrian Steel Ingot Level: ").comment("Configuration on what level requirement a tyrian steel ingot should have to be able to use it in crafting").comment("Default: " + level).define("level", level));
                    CRAFTING_XP.put("silentgear:tyrian_steel_ingot", builder.translation("Tyrian Steel Ingot Xp: ").comment("Configuration on how much tyrian steel ingot should yield in xp while crafting").comment("Default: " + xp).define("xp", xp));
                    CRAFTING_TAGS.put("silentgear:tyrian_steel_ingot", builder.translation("Tyrian Steel Ingot Tags: ").comment("Default tags: " + tags).define("tags", tags));
                builder.pop();
            builder.pop();

        builder.pop();


        builder.translation("Smelting").comment("Settings for Smelting").push("smelting");
            builder.translation("Food").push("cooking_food");
                builder.translation("Fish").push("cooking_fish");
                    level = 1;
                    xp = 6;
                    raw_variant = "minecraft:salmon";
                    SMELTING_LEVEL.put("minecraft:cooked_salmon", builder.translation("Fish Level: ").comment("Configuration on what level requirement a raw fish should have to be able to cook it.").comment("Default: " + level).define("level", level));
                    SMELTING_XP.put("minecraft:cooked_salmon", builder.translation("Fish Xp: ").comment("Configuration on what level requirement a raw fish should have to be able to cook it.").comment("Default: " + xp).define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:cooked_salmon", builder.translation("Raw fish id: ").comment("Configuration on what the meats raw versions id is.").define("raw", raw_variant));
                builder.pop();

                builder.translation("Beef").push("cooking_beef");
                    level = 1;
                    xp = 6;
                    raw_variant = "minecraft:beef";
                    SMELTING_LEVEL.put("minecraft:cooked_beef", builder.translation("Beef Level: ").comment("Level for cooked beef").define("level", level));
                    SMELTING_XP.put("minecraft:cooked_beef", builder.translation("Beef XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:cooked_beef", builder.translation("Raw beef id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Pork").push("cooking_porkchop");
                    level = 1;
                    xp = 6;
                    raw_variant = "minecraft:porkchop";
                    SMELTING_LEVEL.put("minecraft:cooked_porkchop", builder.translation("Porkchop Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:cooked_porkchop", builder.translation("Porkchop XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:cooked_porkchop", builder.translation("Raw pork id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Chicken").push("cooking_chicken");
                    level = 1;
                    xp = 6;
                    raw_variant = "minecraft:chicken";
                    SMELTING_LEVEL.put("minecraft:cooked_chicken", builder.translation("Chicken Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:cooked_chicken", builder.translation("Chicken XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:cooked_chicken", builder.translation("Raw chicken id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Rabbit").push("cooking_rabbit");
                    level = 1;
                    xp = 6;
                    raw_variant = "minecraft:rabbit";
                    SMELTING_LEVEL.put("minecraft:cooked_rabbit", builder.translation("Rabbit Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:cooked_rabbit", builder.translation("Rabbit XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:cooked_rabbit", builder.translation("Raw rabbit id: ").define("raw", raw_variant));
                builder.pop();
            builder.pop();

            builder.translation("Materials").push("smelting_materials");
                builder.translation("Charcoal").push("charcoal");
                    level = 1;
                    xp = 6;
                    raw_variant = "minecraft:oak_logs";
                    SMELTING_LEVEL.put("minecraft:charcoal", builder.translation("Charcoal Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:charcoal", builder.translation("Charcoal XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:charcoal", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Baked Potato").push("baked_potato");
                    level = 1;
                    xp = 6;
                    raw_variant = "minecraft:potato";
                    SMELTING_LEVEL.put("minecraft:baked_potato", builder.translation("Baked Potato Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:baked_potato", builder.translation("Baked Potato XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:baked_potato", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Cooked Cod").push("cooked_cod");
                    level = 1;
                    xp = 6;
                    raw_variant = "minecraft:cod";
                    SMELTING_LEVEL.put("minecraft:cooked_cod", builder.translation("Cooked Cod Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:cooked_cod", builder.translation("Cooked Cod XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:cooked_cod", builder.translation("Raw cod id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Glass").push("glass");
                    level = 2;
                    xp = 12;
                    raw_variant = "minecraft:sand";
                    SMELTING_LEVEL.put("minecraft:glass", builder.translation("Glass Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:glass", builder.translation("Glass XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:glass", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Bricks").push("bricks");
                    level = 3;
                    xp = 18;
                    raw_variant = "minecraft:clay_ball";
                    SMELTING_LEVEL.put("minecraft:bricks", builder.translation("Bricks Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:bricks", builder.translation("Bricks XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:bricks", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Stone").push("stone");
                    level = 5;
                    xp = 30;
                    raw_variant = "minecraft:cobblestone";
                    SMELTING_LEVEL.put("minecraft:stone", builder.translation("Stone Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:stone", builder.translation("Stone XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:stone", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Smooth Stone").push("smooth_stone");
                    level = 8;
                    xp = 48;
                    raw_variant = "minecraft:stone";
                    SMELTING_LEVEL.put("minecraft:smooth_stone", builder.translation("Smooth Stone Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:smooth_stone", builder.translation("Smooth Stone XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:smooth_stone", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Copper Ingot").push("copper_ingot");
                    level = 10;
                    xp = 60;
                    raw_variant = "minecraft:raw_copper";
                    SMELTING_LEVEL.put("minecraft:copper_ingot", builder.translation("Copper Ingot Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:copper_ingot", builder.translation("Copper Ingot XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:copper_ingot", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Iron Ingot").push("iron_ingot");
                    level = 20;
                    xp = 120;
                    raw_variant = "minecraft:raw_iron";
                    SMELTING_LEVEL.put("minecraft:iron_ingot", builder.translation("Iron Ingot Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:iron_ingot", builder.translation("Iron Ingot XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:iron_ingot", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Gold Ingot").push("gold_ingot");
                    level = 25;
                    xp = 150;
                    raw_variant = "minecraft:raw_gold";
                    SMELTING_LEVEL.put("minecraft:gold_ingot", builder.translation("Gold Ingot Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:gold_ingot", builder.translation("Gold Ingot XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:gold_ingot", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Diamond").push("diamond");
                    level = 30;
                    xp = 180;
                    raw_variant = "minecraft:diamond_ore";
                    SMELTING_LEVEL.put("minecraft:diamond", builder.translation("Diamond Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:diamond", builder.translation("Diamond XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:diamond", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Netherite Scrap").push("netherite_scrap");
                    level = 40;
                    xp = 240;
                    raw_variant = "minecraft:ancient_debris";
                    SMELTING_LEVEL.put("minecraft:netherite_scrap", builder.translation("Netherite Scrap Level: ").define("level", level));
                    SMELTING_XP.put("minecraft:netherite_scrap", builder.translation("Netherite Scrap XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("minecraft:netherite_scrap", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Crimson Iron Ingot").push("crimson_iron_ingot");
                    level = 43;
                    xp = 258;
                    raw_variant = "silentgear:raw_crimson_iron";
                    SMELTING_LEVEL.put("silentgear:crimson_iron_ingot", builder.translation("Crimson Iron Level: ").define("level", level));
                    SMELTING_XP.put("silentgear:crimson_iron_ingot", builder.translation("Crimson Iron XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("silentgear:crimson_iron_ingot", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();

                builder.translation("Azure Silver Ingot").push("azure_silver_ingot");
                    level = 48;
                    xp = 288;
                    raw_variant = "silentgear:raw_azure_silver";
                    SMELTING_LEVEL.put("silentgear:azure_silver_ingot", builder.translation("Azure Silver Level: ").define("level", level));
                    SMELTING_XP.put("silentgear:azure_silver_ingot", builder.translation("Azure Silver XP: ").define("xp", xp));
                    SMELTING_RAW_MATERIAL.put("silentgear:azure_silver_ingot", builder.translation("Raw material id: ").define("raw", raw_variant));
                builder.pop();
            builder.pop();
        builder.pop();


        builder.translation("Tools").comment("Settings for Tools").push("tools");
            builder.translation("Wood").comment("Wooden tools").push("wood");
                level = 1;
                SILENT_GEAR_TOOLS.put("wood",
                builder.translation("Silentgear Wooden tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Wooden tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
                TOOLS.put("minecraft:wooden_pickaxe",
                builder.translation("Minecraft Wooden tools level requirement: ")
                        .comment("Configurations for level requirements for Minecraft Wooden tools")
                        .comment("Default: " + level)
                        .define("[MINECRAFT] level", level));
            builder.pop();

            builder.translation("Stone").comment("Stone tools").push("STONE");
                level = 5;
                SILENT_GEAR_TOOLS.put("stone",
                builder.translation("[SilentGear] Stone Level Requirement: ")
                        .comment("Configurations for level requirements for Silent gears Stone tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
                TOOLS.put("minecraft:stone_pickaxe",
                builder.translation("[Minecraft] Stone Level Requirement: ")
                        .comment("Configurations for level requirements for Minecraft Stone tools")
                        .comment("Default: " + level)
                        .define("[MINECRAFT] level", level));
            builder.pop();

            builder.translation("Terracotta").comment("Terracotta tools").push("TERRACOTTA");
                level = 4;
                SILENT_GEAR_TOOLS.put("terracotta",
                builder.translation("Silentgear Terracotta tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Terracotta tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
            builder.pop();

            builder.translation("Flint").comment("Flint tools").push("FLINT");
                level = 7;
                SILENT_GEAR_TOOLS.put("flint",
                builder.translation("Silentgear Flint tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Flint tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
            builder.pop();

            builder.translation("Copper").comment("Copper tools").push("COPPER");
                level = 10;
                SILENT_GEAR_TOOLS.put("copper",
                builder.translation("Silentgear Copper tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Copper tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
                TOOLS.put("minecraft:copper_pickaxe",
                builder.translation("Minecraft Copper tools level requirement: ")
                        .comment("Configurations for level requirements for Minecraft Copper tools")
                        .comment("Default: " + level)
                        .define("[MINECRAFT] level", level));
            builder.pop();

            builder.translation("Iron").comment("Iron tools").push("IRON");
                level = 15;

                SILENT_GEAR_TOOLS.put("iron",
                builder.translation("Silentgear Iron tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Iron tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));

                TOOLS.put("minecraft:iron_pickaxe",
                builder.translation("Minecraft Iron tools level requirement: ")
                        .comment("Configurations for level requirements for Minecraft Iron tools")
                        .comment("Default: " + level)
                        .define("[MINECRAFT] level", level));

            builder.pop();


            builder.translation("Bronze").comment("Bronze tools").push("BRONZE");
                //level = 15
                SILENT_GEAR_TOOLS.put("bronze",
                builder.translation("Silentgear Bronze tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Bronze tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
                TOOLS.put("minecraft:bronze_pickaxe",
                builder.translation("Minecraft Bronze tools level requirement: ")
                        .comment("Configurations for level requirements for Minecraft Bronze tools")
                        .comment("Default: " + level)
                        .define("[MINECRAFT] level", level));
            builder.pop();

            builder.translation("Gold").comment("Gold tools").push("GOLD");
                //level = 15;
                SILENT_GEAR_TOOLS.put("gold",
                builder.translation("Silentgear Gold tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Gold tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
                TOOLS.put("minecraft:golden_pickaxe",
                builder.translation("Minecraft Gold tools level requirement: ")
                        .comment("Configurations for level requirements for Minecraft Gold tools")
                        .comment("Default: " + level)
                        .define("[MINECRAFT] level", level));
            builder.pop();

            builder.translation("Blaze Gold").comment("Blaze Gold tools").push("BLAZE_GOLD");
                level = 20;
                SILENT_GEAR_TOOLS.put("blaze_gold",
                builder.translation("Silentgear Blaze Gold tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Blaze Gold tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
            builder.pop();

            builder.translation("Emerald").comment("Emerald tools").push("EMERALD");
                level = 25;
                SILENT_GEAR_TOOLS.put("emerald",
                builder.translation("Silentgear Emerald tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Emerald tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
            builder.pop();

            builder.translation("Diamond").comment("Diamond tools").push("DIAMOND");
                level = 30;
                SILENT_GEAR_TOOLS.put("diamond",
                builder.translation("Silentgear Diamond tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Diamond tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
                TOOLS.put("minecraft:diamond_pickaxe",
                builder.translation("Minecraft Diamond tools level requirement: ")
                        .comment("Configurations for level requirements for Minecraft Diamond tools")
                        .comment("Default: " + level)
                        .define("[MINECRAFT] level", level));
            builder.pop();

            builder.translation("Dimerald").comment("Dimerald tools").push("DIMERALD");
                level = 32;
                SILENT_GEAR_TOOLS.put("dimerald",
                builder.translation("Silentgear Dimerald tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Dimerald tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
            builder.pop();

            builder.translation("Netherite").comment("Netherite tools").push("NETHERITE");
                level = 40;
                SILENT_GEAR_TOOLS.put("netherite",
                builder.translation("Silentgear Netherite tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Netherite tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
                TOOLS.put("minecraft:netherite_pickaxe",
                builder.translation("Minecraft Netherite tools level requirement: ")
                        .comment("Configurations for level requirements for Minecraft Netherite tools")
                        .comment("Default: " + level)
                        .define("[MINECRAFT] level", level));
            builder.pop();

            builder.translation("Crimson Iron").comment("Crimson Iron tools").push("CRIMSON_IRON");
                level = 43;
                SILENT_GEAR_TOOLS.put("crimson_iron",
                builder.translation("Silentgear Crimson Iron tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Crimson Iron tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
            builder.pop();

            builder.translation("Crimson Steel").comment("Crimson Steel tools").push("CRIMSON_STEEL");
                level = 46;
                SILENT_GEAR_TOOLS.put("crimson_steel",
                builder.translation("Silentgear Crimson Steel tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Crimson Steel tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
            builder.pop();

            builder.translation("Azure Silver").comment("Azure Silver tools").push("AZURE_SILVER");
                level = 48;
                SILENT_GEAR_TOOLS.put("azure_silver",
                builder.translation("Silentgear Azure Silver tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Azure Silver tools").
                        comment("Default: " + level).
                        define("[SILENTGEAR] level", level));
            builder.pop();

            builder.translation("Azure Electrum").comment("Azure Electrum tools").push("AZURE_ELECTRUM");
                level = 49;
                SILENT_GEAR_TOOLS.put("azure_electrum",
                builder.translation("Silentgear Azure Electrum tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Azure Electrum tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
            builder.pop();

            builder.translation("Tyrian Steel").comment("Tyrian Steel tools").push("TYRIAN_STEEL");
                level = 50;
                SILENT_GEAR_TOOLS.put("tyrian_steel",
                builder.translation("Silentgear Tyrian Steel tools level requirement: ")
                        .comment("Configurations for level requirements for Silentgear Tyrian Steel tools")
                        .comment("Default: " + level)
                        .define("[SILENTGEAR] level", level));
            builder.pop();
        builder.pop();
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

    public static ConfigData getSilentGearData(String harvestTier) {
        return getSilentGearDataByKey(harvestTier);
    }

    private static ConfigData getSilentGearDataByKey(String key){
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


    private static ConfigData getToolDataByKey(String key){
        ModConfigSpec.ConfigValue<Integer> levelValue = TOOLS.get(key);
        int level = levelValue != null ? levelValue.get() : 0;
        if (level != 0) {
            return new ConfigData(level, 0);
        }
        return null;
    }

    public static class ConfigData {
        public final int level;
        public final double xp;
        public final String tags;
        public final String raw;

        public ConfigData(int level, double xp, String tags, String raw) {
            this.level = level;
            this.xp = xp;
            this.tags = tags;
            this.raw = raw;
        }

        public ConfigData(int level, double xp, String tags) {
            this(level, xp, tags, "none");
        }

        public ConfigData(int level, double xp) {
            this(level, xp, "none", "none");
        }

        public int getLevel() {
            return level;
        }

        public double getXp() {
            return xp;
        }

        public String getTags() {
            return tags;
        }

        public String getRaw() {
            return raw;
        }


        @Override
        public String toString() {
            return "ConfigData{" +
                    "level='" + level +
                    "', xp='" + xp +
                    "', tags='" + tags + '\'' +
                    ", raw='" + raw + "'}";
        }
    }


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (Dist.CLIENT.isDedicatedServer()) {
            System.out.println("Config reloaded! New value for default: " +
                    Config.SILENT_GEAR_TOOLS.get("default").get());
        }
    }
}
