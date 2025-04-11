package net.raynna.raynnarpg.newconfig;

import com.google.gson.JsonObject;
import com.iafenvoy.jupiter.config.container.AutoInitConfigContainer;
import net.minecraft.resources.ResourceLocation;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.newconfig.combat.CombatConfig;
import net.raynna.raynnarpg.newconfig.server.ServerConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RaynnaServerConfig extends AutoInitConfigContainer {

    public static final RaynnaServerConfig INSTANCE = new RaynnaServerConfig();
    public static final int CURRENT_VERSION = 1;
    public static final String PATH = "./config/raynna/common";

    String VERSION_KEY = "version";

    public ServerConfig server = new ServerConfig();
    public CombatConfig combat = new CombatConfig();

    public static ServerConfig getServerConfig() {
        return INSTANCE.server;
    }

    public static CombatConfig getCombatConfig() {
         return INSTANCE.combat;
    }

    public RaynnaServerConfig() {
        super(ResourceLocation.fromNamespaceAndPath(RaynnaRPG.MOD_ID, "common"), "screen.raynna.common.title", PATH + ".json");
    }

    @Override
    protected boolean shouldLoad(JsonObject obj) {
        if (!obj.has(VERSION_KEY)) return true;
        int version = obj.get(VERSION_KEY).getAsInt();
        if (version != CURRENT_VERSION) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
                FileUtils.copyFile(new File(this.path), new File(PATH + "-"+ sdf.format(new Date()) + ".json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            RaynnaRPG.LOGGER.info("Wrong server config version {} for mod {}! Automatically use version {} and backup old one.", version, RaynnaRPG.MOD_NAME, CURRENT_VERSION);
            return false;
        } else RaynnaRPG.LOGGER.info("{} server config version match.", RaynnaRPG.MOD_NAME);
        return true;
    }

    @Override
    protected void writeCustomData(JsonObject obj) {
        obj.addProperty("version", CURRENT_VERSION);
    }
}