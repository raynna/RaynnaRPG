package net.raynna.raynnarpg.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.raynna.raynnarpg.Config;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.client.ui.OverlayManager;
import net.raynna.raynnarpg.client.ui.floating_text.FloatingText;
import net.raynna.raynnarpg.compat.silentgear.SilentGearCompat;
import net.raynna.raynnarpg.server.player.skills.Skills;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TraitHelper;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class Utils {

    public static boolean hasSilkTouch(ItemStack tool) {
        ItemEnchantments enchantments = tool.get(DataComponents.ENCHANTMENTS);
        if (enchantments != null) {
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                if (entry.getKey().is(Enchantments.SILK_TOUCH)) {
                    return true;
                }
            }
        }
        if (SilentGearCompat.IS_LOADED) {
            return TraitHelper.hasTrait(tool, Const.Traits.SILKY);
        }
        return false;
    }

    public static boolean hasEnchantmentOrTrait(ItemStack tool, ResourceKey<Enchantment> enchantment, Trait trait) {
        ItemEnchantments enchantments = tool.get(DataComponents.ENCHANTMENTS);
        if (enchantments != null) {
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                if (entry.getKey().is(enchantment)) {
                    return true;
                }
            }
        }
        if (SilentGearCompat.IS_LOADED) {
            return trait != null && TraitHelper.hasTrait(tool, trait);
        }
        return false;
    }

    public static boolean hasEnchantment(ItemStack tool,  ResourceKey<Enchantment> enchantment) {
        ItemEnchantments enchantments = tool.get(DataComponents.ENCHANTMENTS);
        if (enchantments != null) {
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                if (entry.getKey().is(enchantment)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void checkMiningMiss(Player player, BlockPos pos, float range) {
        OverlayManager overlayManager = RaynnaRPG.getOverlayManager();
        boolean canShowMiss = CooldownManager.checkCooldown(player, "miss", 2000);
        if (canShowMiss) {
            Vec3 randomVec = Utils.randomizeBlockPos(pos, range);
            FloatingText missText = FloatingText.createWorldSpace("Miss!", randomVec.x, randomVec.y, randomVec.z).withColor(Colour.Colours.RED.getTextColor());
            overlayManager.addText(missText);
        }
    }

    public static String extractModId(String key) {
        if (!key.contains(":")) {
            return Utils.capitalize(key.replace("_", " "));
        }

        String itemName = key.split(":")[0];
        String[] parts = itemName.split("_");

        if (parts.length > 0) {
            return Utils.capitalize(parts[0]);
        }

        return key;
    }

    public static boolean isXpCapped(int playerLevel, int levelReq) {
        int levelCap = Skills.XP_LEVEL_CAP;
        if (levelCap == 0) {
            return false;
        }
        double difference = playerLevel - levelReq;
        return difference >= levelCap;
    }

    public static String extractItemId(String key) {
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

    public static String formatPercentage(double progress, boolean isMaxLevel) {
        double percent = progress * 100;
        if (isMaxLevel) {
            int maxXp = (int) Skills.MAX_XP;
            int currentXpInt = (int) progress;
            percent = (double) currentXpInt / maxXp * 100;
        }

        if (percent == (int) percent) {
            return String.format("%d%%", (int) percent);
        } else if (Math.abs(percent * 10 - (int)(percent * 10)) < 0.0001) {
            return String.format("%.1f%%", percent);
        } else {
            return String.format("%.2f%%", percent);
        }
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
