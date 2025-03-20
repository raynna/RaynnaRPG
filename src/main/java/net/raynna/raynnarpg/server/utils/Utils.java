package net.raynna.raynnarpg.server.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class Utils {

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
}
