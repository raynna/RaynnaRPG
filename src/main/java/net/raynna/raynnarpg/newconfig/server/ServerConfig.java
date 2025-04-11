package net.raynna.raynnarpg.newconfig.server;

import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer;
import com.iafenvoy.jupiter.config.entry.DoubleEntry;
import com.iafenvoy.jupiter.config.entry.IntegerEntry;

public class ServerConfig extends AutoInitConfigContainer.AutoInitConfigCategoryBase {
        public final DoubleEntry XP_RATE = new DoubleEntry("xp_rate", 1.0, 0.01, 100.0);
        public final IntegerEntry MAX_LEVEL = new IntegerEntry("max_level", 50, 1, Integer.MAX_VALUE);
        public final IntegerEntry MAX_XP = new IntegerEntry("max_xp", 303000, 1, 100000000);
        public final IntegerEntry LEVEL_CAP = new IntegerEntry("level_cap", 0, 0, Integer.MAX_VALUE);
        public final DoubleEntry BASE_MINING_XP = new DoubleEntry("base_mining_xp", 3.0, 0.1, 1000.0);
        public final DoubleEntry BASE_SMELTING_XP = new DoubleEntry("base_smelting_xp", 3.0, 0.1, 1000.0);
        public final DoubleEntry BASE_CRAFTING_XP = new DoubleEntry("base_crafting_xp", 3.0, 0.1, 1000.0);

        public ServerConfig() {
            super("server", "raynna.server.tab");
        }
    }