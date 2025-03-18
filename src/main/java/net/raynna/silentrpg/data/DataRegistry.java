package net.raynna.silentrpg.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DataRegistry {

    private static final Map<String, ToolData> TOOLS = new HashMap<>();
    private static final Map<String, BlockData> BLOCKS = new HashMap<>();
    private static final Map<String, MaterialData> MATERIALS = new HashMap<>();
    private static final Gson GSON = new Gson();
    private static final Path CONFIG_PATH = Path.of("config/silentrpg/block_data.json");

    public static void loadData() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                // If config file does not exist, copy default from JAR
                saveDefaultConfig();
            }

            // Read the JSON from config folder
            FileReader reader = new FileReader(CONFIG_PATH.toFile());
            JsonObject jsonData = GSON.fromJson(reader, JsonObject.class);

            Type toolMapType = new TypeToken<Map<String, ToolData>>() {}.getType();
            TOOLS.putAll(GSON.fromJson(jsonData.getAsJsonObject("tools"), toolMapType));

            Type materialMapType = new TypeToken<Map<String, MaterialData>>() {}.getType();
            MATERIALS.putAll(GSON.fromJson(jsonData.getAsJsonObject("materials"), materialMapType));

            Type blockMapType = new TypeToken<Map<String, BlockData>>() {}.getType();
            BLOCKS.putAll(GSON.fromJson(jsonData.getAsJsonObject("blocks"), blockMapType));

            System.out.println("Loaded " + TOOLS.size() + " tools, " + BLOCKS.size() + " blocks, and " + MATERIALS.size() + " materials.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveDefaultConfig() {
        try {
            InputStream defaultStream = DataRegistry.class.getResourceAsStream("/assets/silentrpg/block_data.json");
            if (defaultStream == null) {
                System.err.println("Default block_data.json not found inside JAR!");
                return;
            }

            JsonObject defaultData = GSON.fromJson(new InputStreamReader(defaultStream), JsonObject.class);
            File configFile = CONFIG_PATH.toFile();
            configFile.getParentFile().mkdirs(); // Ensure directory exists

            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(defaultData, writer);
            }

            System.out.println("Saved default block_data.json to config folder.");
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

    public static MaterialData getMaterial(String id) {
        return MATERIALS.get(id);
    }
}
