package net.raynna.raynnarpg.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.ui.OverlayManager;
import net.raynna.raynnarpg.client.ui.floating_text.FloatingText;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class Utils {

    public static void checkMiningMiss(Player player, BlockPos pos, float range) {
        OverlayManager overlayManager = RaynnaRPG.getOverlayManager();
        boolean canShowMiss = CooldownManager.checkCooldown(player, "miss", 2000);
        if (canShowMiss) {
            Vec3 randomVec = Utils.randomizeBlockPos(pos, range);
            FloatingText missText = FloatingText.createWorldSpace("Miss!", randomVec.x, randomVec.y, randomVec.z).withColor(Colour.RED.getTextColor());
            overlayManager.addText(missText);
        }
    }

    private static final Random RANDOM = new Random();

    /**
     * Generates a random Vec3 offset within a specified range.
     * @param range Maximum distance from origin (e.g., 1.0 = -1.0 to +1.0)
     * @return Randomized Vec3 offset
     */
    public static Vec3 randomOffset(float range) {
        return new Vec3(
                (RANDOM.nextFloat() * 2 - 1) * range,
                (RANDOM.nextFloat() * 2 - 1) * range,
                (RANDOM.nextFloat() * 2 - 1) * range
        );
    }

    /**
     * Randomizes a BlockPos with floating-point precision.
     * @param pos Original position
     * @param range Maximum offset distance
     * @return Randomized Vec3 (world coordinates)
     */
    public static Vec3 randomizeBlockPos(BlockPos pos, float range) {
        return new Vec3(
                pos.getX() + 0.5 + (RANDOM.nextFloat() * 2 - 1) * range,
                pos.getY() + 0.5 + (RANDOM.nextFloat() * 2 - 1) * range,
                pos.getZ() + 0.5 + (RANDOM.nextFloat() * 2 - 1) * range
        );
    }

    /**
     * Randomizes a Vec3 within a given range.
     * @param original Original position
     * @param range Maximum offset distance
     * @return Randomized Vec3
     */
    public static Vec3 randomizeVec3(Vec3 original, float range) {
        return original.add(
                (RANDOM.nextFloat() * 2 - 1) * range,
                (RANDOM.nextFloat() * 2 - 1) * range,
                (RANDOM.nextFloat() * 2 - 1) * range
        );
    }

    /**
     * Formats an integer to include commas (e.g., 10000 -> 10,000)
     *
     * @param number The number to format
     * @return The formatted number as a string
     */
    public static String formatNumber(int number) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
        return formatter.format(number);
    }

    public static String formatNumber(double number) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
        return formatter.format(number);
    }

    public static String fixItemName(String itemName) {
        if (itemName.startsWith("item.")) {
            int firstDotIndex = itemName.indexOf('.') + 1;

            int secondDotIndex = itemName.indexOf('.', firstDotIndex);

            if (secondDotIndex != -1) {
                String modId = itemName.substring(firstDotIndex, secondDotIndex);

                itemName = modId + ":" + itemName.substring(secondDotIndex + 1);
            }
        }
        return itemName;
    }

    public static String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
