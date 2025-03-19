package net.raynna.raynnarpg.server.utils;

import net.minecraft.network.chat.TextColor;

public enum Colour {
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

    Colour(int rgb) {
        this.rgb = rgb;
    }

    public TextColor getTextColor() {
        return TextColor.fromRgb(rgb);
    }
}