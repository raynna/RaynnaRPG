package net.raynna.raynnarpg.utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.Arrays;

public class RegistryUtils {

    /**
     * Returns the localized display name of a block given its registry name (e.g., "minecraft:iron_ore").
     */
    public static String getDisplayName(String key) {
        if (key == null || key.isEmpty()) return "Unknown";

        if (key.startsWith("item.") || key.startsWith("block.")) {
            key = key.substring(key.indexOf('.') + 1);
        }

        String[] parts = key.split("\\.");
        if (parts.length < 2) return "Invalid Resource Key";

        String namespace = parts[0];
        String path = String.join("_", Arrays.copyOfRange(parts, 1, parts.length));

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item != Items.AIR) {
            return new ItemStack(item).getHoverName().getString();
        }

        Block block = BuiltInRegistries.BLOCK.get(id);

        if (block != Blocks.AIR) {
            return new ItemStack(block).getHoverName().getString();
        }

        return "Unknown";
    }


    /**
     * Returns the localized display name of an item given its registry name (e.g., "minecraft:iron_ingot").
     */
    public static String getItemDisplayName(String key) {
        ResourceLocation id = ResourceLocation.parse(key);
        Item item = BuiltInRegistries.ITEM.get(id);

        if (item == null || item == net.minecraft.world.item.Items.AIR) {
            return "Unknown Item";
        }

        return new ItemStack(item).getHoverName().getString();
    }

    /**
     * Returns the registry ID (e.g., "minecraft:iron_ore") of a block.
     */
    public static String getBlockId(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    /**
     * Returns the registry ID of a block from its BlockState.
     */
    public static String getBlockId(BlockState state) {
        return getBlockId(state.getBlock());
    }

    /**
     * Returns the registry ID of an item.
     */
    public static String getItemId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }
}
