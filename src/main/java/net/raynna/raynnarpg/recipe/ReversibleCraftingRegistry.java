package net.raynna.raynnarpg.recipe;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.raynna.raynnarpg.RaynnaRPG;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ReversibleCraftingRegistry {
    private static final Set<Item> reversibleItems = new HashSet<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path SAVE_PATH = Path.of("config", "reversible_recipes.json");

    public static void init(ServerStartedEvent event) {
        if (Files.exists(SAVE_PATH)) {
            loadFromJson();
        } else {
            detectAndSave(event.getServer().getRecipeManager());
        }
    }

    private static void detectAndSave(RecipeManager recipeManager) {
        detectReversibleRecipes(recipeManager);
        saveToJson();
    }

    private static void detectReversibleRecipes(RecipeManager recipeManager) {
        Map<Item, Set<Item>> forwardMap = new HashMap<>();

        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            Recipe<?> recipe = holder.value();
            if (recipe instanceof CraftingRecipe craftingRecipe) {
                Item output = craftingRecipe.getResultItem(RegistryAccess.EMPTY).getItem();

                for (Ingredient ingredient : craftingRecipe.getIngredients()) {
                    for (ItemStack match : ingredient.getItems()) {
                        forwardMap.computeIfAbsent(match.getItem(), k -> new HashSet<>()).add(output);
                    }
                }
            }
        }

        for (Map.Entry<Item, Set<Item>> entry : forwardMap.entrySet()) {
            Item input = entry.getKey();
            for (Item result : entry.getValue()) {
                if (forwardMap.getOrDefault(result, Collections.emptySet()).contains(input)) {
                    reversibleItems.add(input);
                    reversibleItems.add(result);
                }
            }
        }
    }

    private static void saveToJson() {
        try {
            Files.createDirectories(SAVE_PATH.getParent());
            List<String> itemIds = reversibleItems.stream()
                    .map(item -> BuiltInRegistries.ITEM.getKey(item).toString())
                    .sorted()
                    .toList();

            Map<String, Object> data = Map.of("reversible_items", itemIds);
            try (Writer writer = Files.newBufferedWriter(SAVE_PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            RaynnaRPG.LOGGER.error("Failed to save reversible crafting data", e);
        }
    }

    private static void loadFromJson() {
        try (Reader reader = Files.newBufferedReader(SAVE_PATH)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray array = json.getAsJsonArray("reversible_items");
            for (JsonElement element : array) {
                ResourceLocation id = ResourceLocation.parse(element.getAsString());
                Item item = BuiltInRegistries.ITEM.get(id);
                reversibleItems.add(item);
            }
        } catch (IOException | JsonParseException e) {
            RaynnaRPG.LOGGER.error("Failed to load reversible crafting data", e);
        }
    }

    public static boolean isReversible(Item item) {
        return reversibleItems.contains(item);
    }
}
