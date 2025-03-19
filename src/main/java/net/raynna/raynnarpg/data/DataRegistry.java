package net.raynna.raynnarpg.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.raynna.raynnarpg.server.utils.Utils;

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
    private static final Path BLOCK_CONFIG_PATH = Path.of("config/raynnarpg/mining_data.json");
    private static final Path TOOL_CONFIG_PATH = Path.of("config/raynnarpg/tool_data.json");
    private static final Path CRAFTING_MATERIAL_PATH = Path.of("config/raynnarpg/crafting_data.json");
    private static final Path SMELTING_CONFIG_PATH = Path.of("config/raynnarpg/smelting_data.json");

    public static void loadData() {
        try {
            // Ensure default configs exist
            saveDefaultConfig(BLOCK_CONFIG_PATH, "/assets/raynnarpg/mining_data.json");
            saveDefaultConfig(TOOL_CONFIG_PATH, "/assets/raynnarpg/tool_data.json");
            saveDefaultConfig(CRAFTING_MATERIAL_PATH, "/assets/raynnarpg/crafting_data.json");
            saveDefaultConfig(SMELTING_CONFIG_PATH, "/assets/raynnarpg/smelting_data.json");

            // Load tools
            if (Files.exists(TOOL_CONFIG_PATH)) {
                try (FileReader toolReader = new FileReader(TOOL_CONFIG_PATH.toFile())) {
                    JsonObject toolJson = GSON.fromJson(toolReader, JsonObject.class);
                    if (toolJson != null && toolJson.has("tools")) {
                        Type toolMapType = new TypeToken<Map<String, JsonObject>>() {}.getType();
                        Map<String, JsonObject> tool = GSON.fromJson(toolJson.getAsJsonObject("tools"), toolMapType);
                        if (tool != null) {
                            for (Map.Entry<String, JsonObject> entry : tool.entrySet()) {
                                String itemId = entry.getKey();
                                JsonObject toolDataJson = entry.getValue();

                                int levelRequirement = toolDataJson.has("level_requirement") ? toolDataJson.get("level_requirement").getAsInt() : 0;
                                Set<String> tags = new HashSet<>();

                                if (toolDataJson.has("tags")) {
                                    JsonArray tagArray = toolDataJson.getAsJsonArray("tags");
                                    for (JsonElement tagElement : tagArray) {
                                        tags.add(tagElement.getAsString());
                                    }
                                }

                                ToolData toolData = new ToolData(itemId, levelRequirement, tags);
                                TOOLS.put(itemId, toolData);
                            }
                        }
                    }
                }
            }

            // Load smelting Materials
            if (Files.exists(SMELTING_CONFIG_PATH)) {
                try (FileReader smeltReader = new FileReader(SMELTING_CONFIG_PATH.toFile())) {
                    JsonObject smeltingJson = GSON.fromJson(smeltReader, JsonObject.class);
                    if (smeltingJson != null && smeltingJson.has("smelting_materials")) {
                        Type smeltingMapType = new TypeToken<Map<String, JsonObject>>() {}.getType();
                        Map<String, JsonObject> smeltMaterial = GSON.fromJson(smeltingJson.getAsJsonObject("smelting_materials"), smeltingMapType);
                        if (smeltMaterial != null) {
                            for (Map.Entry<String, JsonObject> entry : smeltMaterial.entrySet()) {
                                String itemId = entry.getKey();
                                JsonObject smeltingDataJson = entry.getValue();

                                int levelRequirement = smeltingDataJson.has("level_requirement") ? smeltingDataJson.get("level_requirement").getAsInt() : 0;
                                int experience = smeltingDataJson.has("experience") ? smeltingDataJson.get("experience").getAsInt() : 0;
                                Set<String> tags = new HashSet<>();

                                if (smeltingDataJson.has("tags")) {
                                    JsonArray tagArray = smeltingDataJson.getAsJsonArray("tags");
                                    for (JsonElement tagElement : tagArray) {
                                        tags.add(tagElement.getAsString());
                                    }
                                }
                                String rawMaterial = smeltingDataJson.has("raw_material") ? smeltingDataJson.get("raw_material").getAsString() : null;

                                SmeltingData smeltingData = new SmeltingData(itemId, levelRequirement, experience, rawMaterial, tags);
                                SMELTING_MATERIALS.put(itemId, smeltingData);
                            }
                        }
                    }
                }
            }

            // Load crafting Materials
            if (Files.exists(CRAFTING_MATERIAL_PATH)) {
                try (FileReader craftReader = new FileReader(CRAFTING_MATERIAL_PATH.toFile())) {
                    JsonObject craftingJson = GSON.fromJson(craftReader, JsonObject.class);
                    if (craftingJson != null && craftingJson.has("crafting_materials")) {
                        Type craftingMapType = new TypeToken<Map<String, JsonObject>>() {}.getType();
                        Map<String, JsonObject> craftMaterial = GSON.fromJson(craftingJson.getAsJsonObject("crafting_materials"), craftingMapType);
                        if (craftMaterial != null) {
                            for (Map.Entry<String, JsonObject> entry : craftMaterial.entrySet()) {
                                String itemId = entry.getKey();
                                JsonObject craftingDataJson = entry.getValue();

                                int levelRequirement = craftingDataJson.has("level_requirement") ? craftingDataJson.get("level_requirement").getAsInt() : 0;
                                int experience = craftingDataJson.has("experience") ? craftingDataJson.get("experience").getAsInt() : 0;
                                Set<String> tags = new HashSet<>();

                                if (craftingDataJson.has("tags")) {
                                    JsonArray tagArray = craftingDataJson.getAsJsonArray("tags");
                                    for (JsonElement tagElement : tagArray) {
                                        tags.add(tagElement.getAsString());
                                    }
                                }

                                CraftingData craftingData = new CraftingData(itemId, levelRequirement, experience, tags);
                                CRAFTING_MATERIALS.put(itemId, craftingData);
                            }
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
            if (blockData.getTags().contains(tag)) {
                return blockData;
            }
        }
        return null;
    }

    public static CraftingData getCraftingDataByTag(String tag) {
        for (CraftingData craftingData : CRAFTING_MATERIALS.values()) {
            if (craftingData.getTags().contains(tag)) {
                return craftingData;
            }
        }
        return null;
    }

    public static ToolData getToolByTag(String tag) {
        for (ToolData toolData : TOOLS.values()) {
            if (toolData.getTags().contains(tag)) {
                return toolData;
            }
        }
        return null;
    }

    public static SmeltingData getSmeltingDataByTag(String tag) {
        for (SmeltingData smeltingData : SMELTING_MATERIALS.values()) {
            if (smeltingData.getTags().contains(tag)) {
                return smeltingData;
            }
        }
        return null;
    }

    public static SmeltingData getSmeltingItemByRawMaterial(String itemId) {
        itemId = Utils.fixItemName(itemId);
        for (SmeltingData smeltingData : SMELTING_MATERIALS.values()) {
            if (smeltingData.getRawMaterial() != null && smeltingData.getRawMaterial().equals(itemId)) {
                return smeltingData;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getDataFromItem(ItemStack stack, Class<T> dataType) {
        String itemId = stack.getItem().getDescriptionId();

        Object data = switch (dataType.getSimpleName()) {
            case "BlockData" -> getBlock(itemId);
            case "CraftingData" -> getMaterial(itemId);
            case "SmeltingData" -> getSmeltingItem(itemId);
            case "ToolData" -> getTool(itemId);
            default -> null;
        };

        if (data == null) {
            data = switch (dataType.getSimpleName()) {
                case "SmeltingData" -> getSmeltingItemByRawMaterial(itemId);
                default -> null;
            };
        }

        if (data == null) {
            for (TagKey<Item> tagKey : stack.getTags().toList()) {
                String tagName = tagKey.location().toString();

                data = switch (dataType.getSimpleName()) {
                    case "BlockData" -> getBlockByTag(tagName);
                    case "CraftingData" -> getCraftingDataByTag(tagName);
                    case "SmeltingData" -> getSmeltingDataByTag(tagName);
                    case "ToolData" -> getToolByTag(tagName);
                    default -> null;
                };

                if (data != null) {
                    return (T) data;
                }
            }
        }

        return (T) data;
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

    public static String determineToolType(ItemStack stack) {
        for (TagKey<Item> tag : stack.getTags().toList()) {
            String tagName = tag.location().toString();

            if (tagName.contains("c:tools/mining_tool") || tagName.contains("minecraft:pickaxes") || tagName.contains("minecraft:shovels")) return "Mining";
            if (tagName.contains("minecraft:axes")) return "Woodcutting";
            if (tagName.contains("minecraft:hoes")) return "Farming";
            if (tagName.contains("c:tools/melee_weapon")) return "Combat";
        }
        return "General";
    }
}
