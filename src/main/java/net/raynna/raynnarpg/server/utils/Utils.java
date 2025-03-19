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
}
