package net.raynna.raynnarpg.utils;

import net.minecraft.network.chat.TextColor;


public class Colour {

    public static final String BLACK = "§0";       // Black
    public static final String DARK_BLUE = "§1";   // Dark Blue
    public static final String DARK_GREEN = "§2";   // Dark Green
    public static final String DARK_AQUA = "§3";    // Dark Aqua/Cyan
    public static final String DARK_RED = "§4";     // Dark Red
    public static final String DARK_PURPLE = "§5";  // Dark Purple
    public static final String GOLD = "§6";         // Gold/Orange
    public static final String GRAY = "§7";        // Gray
    public static final String DARK_GRAY = "§8";   // Dark Gray
    public static final String BLUE = "§9";        // Blue
    public static final String GREEN = "§a";       // Green
    public static final String AQUA = "§b";        // Aqua/Cyan
    public static final String RED = "§c";         // Red
    public static final String LIGHT_PURPLE = "§d"; // Light Purple/Pink
    public static final String YELLOW = "§e";      // Yellow
    public static final String WHITE = "§f";       // White

    public static final String HEART_ICON = "❤";
    public static final String SATURATION_ICON = "🍗";
    public static final String EFFECT_ICON = "⚡";
    public static final String SHIELD_ICON = "⛨";
    public static final String SWORD_ICON = "⚔";
    public static final String BOW_ICON = "🏹";
    public static final String BLOOD_ICON = "🩸";
    public static final String PICKAXE_ICON = "⛏️";
    public static final String FEATHER_ICON = "🪶";
    public static final String FIRE_ICON = "🔥";
    public static final String ICE_ICON = "❄️";
    public static final String MAGIC_ICON = "✨";


    public static final String BULLET = "▪";
    public static final String SUB_BULLET = "◦";
    public static final String SMALL_BULLET = "·";

    public static final String FORMAT_OBFUSCATED = "§k"; // Obfuscated (random chars)
    public static final String FORMAT_BOLD = "§l";       // Bold
    public static final String FORMAT_STRIKETHROUGH = "§m"; // Strikethrough
    public static final String FORMAT_UNDERLINE = "§n";  // Underlined
    public static final String FORMAT_ITALIC = "§o";     // Italic
    public static final String FORMAT_RESET = "§r";

    public enum Colours {
        DARK_RED(0xAA0000),
        RED(0xFF5555),
        GOLD(0xFFAA00),
        YELLOW(0xFFFF55),
        DARK_GREEN(0x00AA00),
        GREEN(0x55FF55),
        AQUA(0x55FFFF),
        DARK_AQUA(0x00AAAA),
        DARK_BLUE(0x0000AA),
        BLUE(0x5555FF),
        LIGHT_PURPLE(0xFF55FF),
        DARK_PURPLE(0xAA00AA),
        WHITE(0xFFFFFF),
        GRAY(0xAAAAAA),
        DARK_GRAY(0x555555),
        BLACK(0x000000);

        private final int rgb;

        Colours(int rgb) {
            this.rgb = rgb;
        }

        public int getTextColor() {
            return TextColor.fromRgb(rgb).getValue();
        }
    }
}