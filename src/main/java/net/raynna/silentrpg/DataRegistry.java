package net.raynna.silentrpg;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.raynna.silentrpg.data.BlockData;
import net.raynna.silentrpg.data.ToolData;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DataRegistry {

    private static final Map<String, ToolData> TOOLS = new HashMap<>();
    private static final Map<String, BlockData> BLOCKS = new HashMap<>();

    /**
     * Load the JSON file and parse tool and block data
     */
    public static void loadData() {
        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(DataRegistry.class.getResourceAsStream("/assets/silentrpg/block_data.json")))) {
            Gson gson = new Gson();

            JsonObject jsonData = gson.fromJson(reader, JsonObject.class);

            Type toolMapType = new TypeToken<Map<String, ToolData>>() {}.getType();
            Map<String, ToolData> tools = gson.fromJson(jsonData.getAsJsonObject("tools"), toolMapType);
            TOOLS.putAll(tools);

            Type blockMapType = new TypeToken<Map<String, BlockData>>() {}.getType();
            Map<String, BlockData> blocks = gson.fromJson(jsonData.getAsJsonObject("blocks"), blockMapType);
            BLOCKS.putAll(blocks);
            System.out.println("Loaded " + TOOLS.size() + " tools and " + BLOCKS.size() + " blocks.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get ToolData by ID
     */
    public static ToolData getTool(String id) {
        return TOOLS.get(id);
    }

    /**
     * Get BlockData by ID
     */
    public static BlockData getBlock(String id) {
        return BLOCKS.get(id);
    }

    public static BlockData getBlockTag(String tag) {
        return BLOCKS.get(tag);
    }
}

