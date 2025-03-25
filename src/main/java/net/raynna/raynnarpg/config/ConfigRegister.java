package net.raynna.raynnarpg.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.raynna.raynnarpg.utils.Utils;

import java.util.Objects;

public class ConfigRegister {

    /**
     * Registers a category of tools with a block, allowing for a more fluent approach.
     * The tools will be registered inside the provided block.
     */

    public static void registerCategory(ModConfigSpec.Builder builder, String path, String translation, String comment, Runnable category) {
        registerSub(builder, path, translation, comment, category);
        builder.pop();
    }

    public static void registerSub(ModConfigSpec.Builder builder, String path, String translation, String comment, Runnable category) {
        builder.translation(translation).comment(comment).push(path);
        category.run();
    }

    public static String extractCategory(String key) {
        if (!key.contains(":")) {
            return Utils.capitalize(key.replace("_", " "));
        }

        String itemName = key.split(":")[1];
        String[] parts = itemName.split("_");

        if (parts.length > 0) {
            return Utils.capitalize(parts[0]);
        }

        return key;
    }
}
