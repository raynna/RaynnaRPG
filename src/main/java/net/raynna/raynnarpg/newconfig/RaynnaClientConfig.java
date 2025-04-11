package net.raynna.raynnarpg.newconfig;

import com.iafenvoy.jupiter.config.container.FileConfigContainer;
import com.iafenvoy.jupiter.config.entry.EnumEntry;
import com.iafenvoy.jupiter.interfaces.IConfigEnumEntry;
import net.minecraft.resources.ResourceLocation;
import net.raynna.raynnarpg.RaynnaRPG;
import org.jetbrains.annotations.NotNull;

public class RaynnaClientConfig extends FileConfigContainer {
    public static final RaynnaClientConfig INSTANCE = new RaynnaClientConfig();
    public static final String PATH = "./config/raynna/client";

    // Config entries
    public EnumEntry XP_TEXT_MODE;
    public EnumEntry GUI_POSITION;

    public RaynnaClientConfig() {
        super(ResourceLocation.fromNamespaceAndPath(RaynnaRPG.MOD_ID, "config.raynna.client"), "screen.raynna.client.title", PATH + ".json");
    }

    @Override
    public void init() {
        this.createTab("display", "raynna.client.display")
                .add(XP_TEXT_MODE = new EnumEntry("xp_text_mode", XpDisplayMode.BOTH))
                .add(GUI_POSITION = new EnumEntry("gui_position", GuiPosition.CENTER_LEFT)
                );
    }


    public enum XpDisplayMode implements IConfigEnumEntry {
        XP("XP Only"),
        PERCENT("Percentage Only"),
        BOTH("Both XP and Percentage");

        private final String displayName;

        XpDisplayMode(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String getName() {
            return displayName;
        }

        @Override
        public @NotNull IConfigEnumEntry getByName(String name) {
            try {
                return valueOf(name);
            } catch (IllegalArgumentException e) {
                // Try to match by display name
                for (XpDisplayMode mode : values()) {
                    if (mode.displayName.equalsIgnoreCase(name)) {
                        return mode;
                    }
                }
                return BOTH; // Default fallback
            }
        }

        @Override
        public IConfigEnumEntry cycle(boolean clockWise) {
            int length = values().length;
            int newOrdinal = (this.ordinal() + (clockWise ? 1 : length - 1)) % length;
            return values()[newOrdinal];
        }
    }

    public enum GuiPosition implements IConfigEnumEntry {
        TOP_LEFT("Top Left"),
        TOP_CENTER("Top Center"),
        TOP_RIGHT("Top Right"),
        CENTER_LEFT("Center Left"),
        CENTER("Center"),
        CENTER_RIGHT("Center Right"),
        BOTTOM_LEFT("Bottom Left"),
        BOTTOM_CENTER("Bottom Center"),
        BOTTOM_RIGHT("Bottom Right");

        private final String displayName;

        GuiPosition(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String getName() {
            return displayName;
        }

        @Override
        public @NotNull IConfigEnumEntry getByName(String name) {
            try {
                return valueOf(name);
            } catch (IllegalArgumentException e) {
                // Try to match by display name
                for (GuiPosition pos : values()) {
                    if (pos.displayName.equalsIgnoreCase(name)) {
                        return pos;
                    }
                }
                return CENTER_LEFT; // Default fallback
            }
        }

        @Override
        public IConfigEnumEntry cycle(boolean clockWise) {
            int length = values().length;
            int newOrdinal = (this.ordinal() + (clockWise ? 1 : length - 1)) % length;
            return values()[newOrdinal];
        }
    }
}