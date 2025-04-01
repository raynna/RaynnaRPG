package net.raynna.raynnarpg.config;

import net.neoforged.neoforge.common.ModConfigSpec;


public class ConfigRegister {

    /**
     * Registers a category of tools with a block, allowing for a more fluent approach.
     * The tools will be registered inside the provided block.
     */

    public static void registerCategory(ModConfigSpec.Builder builder, String path, String translation, String comment, Runnable category) {
        builder.translation(translation).comment(comment).push(path);
        category.run();
        builder.pop();
    }
}
