package net.raynna.silentrpg.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataRegistry {

    private static final Map<String, ToolData> TOOLS = new HashMap<>();
    private static final Map<String, BlockData> BLOCKS = new HashMap<>();
    private static final Map<String, CraftingData> CRAFTING_MATERIALS = new HashMap<>();
    private static final Map<String, SmeltingData> SMELTING_MATERIALS = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path BLOCK_CONFIG_PATH = Path.of("config/silentrpg/block_data.json");
    private static final Path TOOL_CONFIG_PATH = Path.of("config/silentrpg/tool_data.json");
    private static final Path MATERIAL_CONFIG_PATH = Path.of("config/silentrpg/material_data.json");
    private static final Path SMELTING_CONFIG_PATH = Path.of("config/silentrpg/smelting_data.json");

    public static void loadData() {
        try {
            // Ensure default configs exist
            saveDefaultConfig(BLOCK_CONFIG_PATH, "/assets/silentrpg/block_data.json");
            saveDefaultConfig(TOOL_CONFIG_PATH, "/assets/silentrpg/tool_data.json");
            saveDefaultConfig(MATERIAL_CONFIG_PATH, "/assets/silentrpg/material_data.json");
            saveDefaultConfig(SMELTING_CONFIG_PATH, "/assets/silentrpg/smelting_data.json");

            // Load tools
            if (Files.exists(TOOL_CONFIG_PATH)) {
                try (FileReader toolReader = new FileReader(TOOL_CONFIG_PATH.toFile())) {
                    JsonObject toolJson = GSON.fromJson(toolReader, JsonObject.class);
                    if (toolJson != null && toolJson.has("tools")) {
                        Type toolMapType = new TypeToken<Map<String, ToolData>>() {}.getType();
                        Map<String, ToolData> loadedTools = GSON.fromJson(toolJson.getAsJsonObject("tools"), toolMapType);
                        if (loadedTools != null) {
                            TOOLS.putAll(loadedTools);
                        }
                    }
                }
            }

            // Load crafting materials
            if (Files.exists(MATERIAL_CONFIG_PATH)) {
                try (FileReader materialReader = new FileReader(MATERIAL_CONFIG_PATH.toFile())) {
                    JsonObject materialJson = GSON.fromJson(materialReader, JsonObject.class);
                    if (materialJson != null && materialJson.has("crafting_materials")) {
                        Type materialMapType = new TypeToken<Map<String, CraftingData>>() {}.getType();
                        Map<String, CraftingData> loadedMaterials = GSON.fromJson(materialJson.getAsJsonObject("crafting_materials"), materialMapType);
                        if (loadedMaterials != null) {
                            CRAFTING_MATERIALS.putAll(loadedMaterials);
                        }
                    }
                }
            }

            // Load smelting materials
            if (Files.exists(SMELTING_CONFIG_PATH)) {
                try (FileReader materialReader = new FileReader(SMELTING_CONFIG_PATH.toFile())) {
                    JsonObject materialJson = GSON.fromJson(materialReader, JsonObject.class);
                    if (materialJson != null && materialJson.has("smelting_materials")) {
                        Type materialMapType = new TypeToken<Map<String, SmeltingData>>() {}.getType();
                        Map<String, SmeltingData> loadedMaterials = GSON.fromJson(materialJson.getAsJsonObject("smelting_materials"), materialMapType);
                        if (loadedMaterials != null) {
                            SMELTING_MATERIALS.putAll(loadedMaterials);
                        }
                    }
                }
            }

            // Load blocks
            if (Files.exists(BLOCK_CONFIG_PATH)) {
                try (FileReader blockReader = new FileReader(BLOCK_CONFIG_PATH.toFile())) {
                    JsonObject blockJson = GSON.fromJson(blockReader, JsonObject.class);
                    if (blockJson != null && blockJson.has("blocks")) {
                        Type blockMapType = new TypeToken<Map<String, JsonObject>>() {}.getType();
                        Map<String, JsonObject> rawBlocks = GSON.fromJson(blockJson.getAsJsonObject("blocks"), blockMapType);
                        if (rawBlocks != null) {
                            for (Map.Entry<String, JsonObject> entry : rawBlocks.entrySet()) {
                                String blockId = entry.getKey();
                                JsonObject blockDataJson = entry.getValue();

                                int levelRequirement = blockDataJson.has("level_requirement") ? blockDataJson.get("level_requirement").getAsInt() : 0;
                                int experience = blockDataJson.has("experience") ? blockDataJson.get("experience").getAsInt() : 0;
                                Set<String> tags = new HashSet<>();

                                if (blockDataJson.has("tags")) {
                                    JsonArray tagArray = blockDataJson.getAsJsonArray("tags");
                                    for (JsonElement tagElement : tagArray) {
                                        tags.add(tagElement.getAsString());
                                    }
                                }

                                BlockData blockData = new BlockData(blockId, levelRequirement, experience, tags);
                                BLOCKS.put(blockId, blockData);
                            }
                        }
                    }
                }
            }

            System.out.println("Loaded Tools(" + TOOLS.size() + "), Blocks("+BLOCKS.size()+"), Crafting(" + CRAFTING_MATERIALS.size() + "), Smelting(" + SMELTING_MATERIALS.size() + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveDefaultConfig(Path configPath, String resourcePath) {
        try {
            InputStream defaultStream = DataRegistry.class.getResourceAsStream(resourcePath);
            if (defaultStream == null) {
                System.err.println("Default config not found inside JAR: " + resourcePath);
                return;
            }

            JsonObject defaultData = GSON.fromJson(new InputStreamReader(defaultStream), JsonObject.class);
            File configFile = configPath.toFile();
            configFile.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(defaultData, writer);
            }

            System.out.println("Saved default config to: " + configPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BlockData getBlock(String id) {
        return BLOCKS.get(id);
    }

    public static ToolData getTool(String id) {
        return TOOLS.get(id);
    }

    public static CraftingData getMaterial(String id) {
        return CRAFTING_MATERIALS.get(id);
    }

    public static SmeltingData getSmeltingItem(String id) {
        return SMELTING_MATERIALS.get(id);
    }

    public static BlockData getBlockByTag(String tag) {
        for (BlockData blockData : BLOCKS.values()) {
            //System.out.println("[DataRegistry.getBlockByTag] Checking block: " + blockData.getId());
            //System.out.println("[DataRegistry.getBlockByTag] Block tags: " + blockData.getTags());
            //System.out.println("[DataRegistry.getBlockByTag] Tag: " + tag);
            if (blockData.getTags().contains(tag)) {
                return blockData;
            }
        }
        return null;
    }

    public static BlockData getDataFromBlock(BlockState state) {
        String blockId = state.getBlock().getDescriptionId();
        BlockData blockData = getBlock(blockId);

        //System.out.println("[DataRegistry.getDataFromBlock] blockId: " + blockId);
        //System.out.println("[DataRegistry.getDataFromBlock]Block data: " + blockData);
        if (blockData == null) {
            for (TagKey<Block> tagKey : state.getTags().toList()) {
                String tagName = tagKey.location().toString();
                //System.out.println("[DataRegistry.getDataFromBlock] Tag: " + tagName);
                blockData = getBlockByTag(tagName);

                if (blockData != null) {
                    //System.out.println("[DataRegistry.getDataFromBlock] Found block data with tag: " + tagName);
                    return blockData;
                }
            }
        }
        return blockData;
    }
}
